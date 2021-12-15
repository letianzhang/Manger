package com.example.manager.dto;

import lombok.Data;

@Data
public class AddMenuResultDTO {
    private Integer code;

    private String message;

    private String type;

    private AddMenuResultData data;

    @Data
    public static class AddMenuResultData {
        private String resuId;
    }
}
