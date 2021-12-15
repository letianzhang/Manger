package com.example.manager.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MenuDTO {
    private String prntResuId;
    private String resuCodg;
    private String resuName;
    private String resuPath;
    private String resuType;
    private String subsysId;
}
