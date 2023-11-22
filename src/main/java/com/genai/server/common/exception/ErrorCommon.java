package com.genai.server.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ErrorCommon {
    private final String code;
    private final String message;
}
