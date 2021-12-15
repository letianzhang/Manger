package com.example.manager.dto;

import lombok.Data;

import java.util.List;

@Data
public class FunctionDTO {
    private Integer code;

    private String message;

    private String type;

    private List<FunctionData> data;

    @Data
    public static class FunctionData {
        private String resuId;
    }
}
