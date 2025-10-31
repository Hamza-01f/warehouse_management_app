package com.brief.demo.mappers;

import com.brief.demo.dto.request.ProductRequestDTO;
import com.brief.demo.dto.request.ProductRequestUpdateDTO;
import com.brief.demo.dto.response.ProductResponseDTO;
import com.brief.demo.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequestDTO request) {
        return Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .image(request.getImage())
                .price(request.getPrice())
                .unit(request.getUnit())
                .build();
    }

    public ProductResponseDTO toResponse(Product product) {
        ProductResponseDTO response = new ProductResponseDTO();
        response.setId(product.getId());
        response.setSku(product.getSku());
        response.setName(product.getName());
        response.setImage(product.getImage());
        response.setPrice(product.getPrice());
        response.setUnit(product.getUnit());
        response.setIsActive(product.getIsActive());
        return response;    }

    public void updateEntity(Product product, ProductRequestUpdateDTO request) {
        if (request.getSku() != null) product.setSku(request.getSku());
        if (request.getName() != null) product.setName(request.getName());
        if (request.getImage() != null) product.setImage(request.getImage());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getUnit() != null) product.setUnit(request.getUnit());
        if (request.getIsActive() != null) product.setIsActive(request.getIsActive());
    }
}
