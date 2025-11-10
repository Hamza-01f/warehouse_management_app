package com.brief.demo.service;

import com.brief.demo.dto.request.ProductRequestDTO;
import com.brief.demo.dto.request.ProductRequestUpdateDTO;
import com.brief.demo.dto.response.ProductResponseDTO;
import com.brief.demo.enums.OrderStatus;
import com.brief.demo.exception.BusinessException;
import com.brief.demo.exception.DuplicateResourceException;
import com.brief.demo.exception.ResourceNotFoundException;
import com.brief.demo.mappers.ProductMapper;
import com.brief.demo.model.Inventory;
import com.brief.demo.model.Product;
import com.brief.demo.model.SalesOrder;
import com.brief.demo.repository.InventoryRepository;
import com.brief.demo.repository.ProductRepository;
import com.brief.demo.repository.SalesOrderLineRepository;
import com.brief.demo.repository.SalesOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final SalesOrderRepository salesOrderRepository;
    private final InventoryRepository inventoryRepository;
    private final SalesOrderLineRepository salesOrderLineRepository;

    public ProductResponseDTO createProduct(ProductRequestDTO request) {
//        if (productRepository.existsBySku(request.getSku())) {
//            throw new DuplicateResourceException("Product with SKU " + request.getSku() + " already exists");
//        }

        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return productMapper.toResponse(product);
    }

    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDTO> getActiveProducts() {
        return productRepository.findByIsActiveTrue().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO updateProduct(Long id, ProductRequestUpdateDTO request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (request.getSku() != null && productRepository.existsBySkuAndIdNot(request.getSku(), id)) {
            throw new DuplicateResourceException("Product with SKU " + request.getSku() + " already exists");
        }

        productMapper.updateEntity(product, request);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setIsActive(false);
        productRepository.save(product);
    }

    public void activateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setIsActive(true);
        productRepository.save(product);
    }

    //--------------- adding method ----------------
    public void deactivatProduct(String sku){

        try{
            Product product = productRepository.existsBySku(sku);
            if(product != null){

                //---- check if the product is already deactivated

                if(!product.getIsActive()){
                    throw new BusinessException("the product you are trying to deactivate a product with status deactivated");
                }

                //---- check if the product is created or reserved

                List<SalesOrder> salesOrders = salesOrderRepository.findByProductSku(sku);
                for(SalesOrder sales : salesOrders){
                    if(sales.getStatus() == OrderStatus.CREATED || sales.getStatus() == OrderStatus.RESERVED){
                        throw  new BusinessException("can not deactivate the reserved or created product");
                    }
                }

                //------- check if the product quantity is bigger that 0

                List<Inventory> inventories = inventoryRepository.findByProductSku(sku);
                for(Inventory inv : inventories){
                    if(inv.getQuantityReserved() > 0 ){
                        throw  new BusinessException("can not deactivate the product with quantity on hand that is bigger than 0");
                    }
                }
            }
            product.setIsActive(false);
            productRepository.save(product);
        }catch (ResourceNotFoundException e){
            throw  new ResourceNotFoundException("the product with required sku not found");
        }

    }
}
