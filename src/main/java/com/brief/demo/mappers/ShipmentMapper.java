package com.brief.demo.mappers;

import com.brief.demo.dto.request.ShipmentRequestDTO;
import com.brief.demo.dto.response.ShipmentResponseDTO;
import com.brief.demo.model.Shipment;

public class ShipmentMapper {

    public Shipment toEntity(ShipmentRequestDTO shipmentRequestDTO){
        return Shipment.builder().salesOrder(shipmentRequestDTO.getSalesOrderId()).
    }

    public ShipmentResponseDTO shipmentResponseDTO(){

    }
}
