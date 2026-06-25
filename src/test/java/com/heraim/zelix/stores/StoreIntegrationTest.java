package com.heraim.zelix.stores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heraim.zelix.auth.dto.AuthResponse;
import com.heraim.zelix.auth.dto.LoginRequest;
import com.heraim.zelix.auth.dto.RegisterRequest;
import com.heraim.zelix.category.dto.CategoryResponse;
import com.heraim.zelix.stores.dto.CreateStoreRequest;
import com.heraim.zelix.stores.entity.DeliveryOption;
import com.heraim.zelix.stores.entity.PaymentMethod;
import com.heraim.zelix.users.entity.Roles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJson
public class StoreIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testStoreCreationAndSearchFlow() throws Exception {
        // 1. Register a vendor user
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String email = "vendor-" + uuid + "@example.com";
        String username = "vendoruser-" + uuid;
        String phone = "080" + (int)(Math.random() * 10000000);
        RegisterRequest registerRequest = new RegisterRequest(
                email,
                username,
                phone,
                "password123",
                Roles.VENDOR
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // 2. Login to get token
        LoginRequest loginRequest = new LoginRequest(email, "password123");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(loginResult.getResponse().getContentAsString(), AuthResponse.class);
        String token = authResponse.accessToken();

        // 3. Get a real categoryId
        MvcResult categoriesResult = mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andReturn();
        
        String categoriesJson = categoriesResult.getResponse().getContentAsString();
        List<CategoryResponse> categories = objectMapper.readValue(categoriesJson, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, CategoryResponse.class));
        
        UUID categoryId = categories.get(0).id();

        // 4. Create a store
        String storeName = "My Awesome Store " + uuid;
        String expectedSlug = "my-awesome-store-" + uuid;
        CreateStoreRequest createStoreRequest = new CreateStoreRequest(
                storeName,
                "A store that sells awesome things",
                "0987654321",
                "0987654321",
                "store-" + uuid + "@example.com",
                "123 Store Street",
                "Store City",
                "Store State",
                "Store Country",
                new BigDecimal("12.3456"),
                new BigDecimal("65.4321"),
                Set.of(DeliveryOption.DELIVERY, DeliveryOption.PICKUP),
                categoryId,
                Set.of(PaymentMethod.BANK_TRANSFER, PaymentMethod.CASH)
        );

        mockMvc.perform(post("/api/stores")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createStoreRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(storeName))
                .andExpect(jsonPath("$.slug").value(expectedSlug));

        // 5. Test search/pagination
        mockMvc.perform(get("/api/stores/search")
                .param("q", uuid)
                .param("limit", "10")
                .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value(storeName))
                .andExpect(jsonPath("$.pagination.total").value(1))
                .andExpect(jsonPath("$.pagination.page").value(1));
    }
}
