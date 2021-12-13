package com.example.manager.dto;

import lombok.Data;

import java.util.List;

/**
 * 子系统查询结果DTO
 */
@Data
public class SubsystemDTO {
    private Integer code;

    private String message;

    private String type;

    private SubsystemData data;

    @Data
    public static class SubsystemData {
        private Long total;

        private List<SubsystemDataResult> result;
    }

    @Data
    public static class SubsystemDataResult {
        private String subsysId;
    }
}
