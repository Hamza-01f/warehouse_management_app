package com.brief.demo.dto.request;

import com.brief.demo.enums.POStatus;
import lombok.Data;

@Data
public class PurchaseOrderUpdateDTO {
    private POStatus status;
}