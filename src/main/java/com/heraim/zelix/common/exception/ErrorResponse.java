package com.heraim.zelix.common.exception;

public record ErrorResponse(
        String message,
        int status
) {
}
