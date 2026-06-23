package com.heraim.zelix.category.repository;

import com.heraim.zelix.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByParentIsNull();

    List<Category> findByParent(Category parent);
}