package com.brief.demo.dto.response;

import com.brief.demo.enums.POStatus;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class PurchaseOrderResponseDTO {
    private Long id;
    private SupplierResponseDTO supplier;
    private POStatus status;
    private LocalDate createdAt;
    private List<POLineResponseDTO> orderLines;
}