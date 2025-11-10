package com.brief.demo.dto.request;

import lombok.Data;

@Data
public class PurchaseOrderUpdateDTO {
    private com.brief.demo.enums.POStatus status;
}