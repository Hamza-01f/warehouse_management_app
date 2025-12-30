    package com.brief.demo.controller;

    //import com.brief.demo.aop.RequiresAdmin;
    import com.brief.demo.dto.request.ProductRequestDTO;
    import com.brief.demo.dto.request.ProductRequestUpdateDTO;
    import com.brief.demo.dto.response.ApiResponseDTO;
    import com.brief.demo.dto.response.ProductResponseDTO;
    import com.brief.demo.service.ProductService;
    import jakarta.servlet.http.HttpServletRequest;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.security.web.csrf.CsrfToken;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/api/products")
    @RequiredArgsConstructor
    @Slf4j
    public class ProductController {

        private final ProductService productService;

        @PostMapping
        public ResponseEntity<ProductResponseDTO> createProduct( @RequestBody ProductRequestDTO request) {

            log.info("Create product request received");

            ProductResponseDTO response = productService.createProduct(request);

            log.info("Product created successfully");

            return ResponseEntity.ok(response);
        }

        @GetMapping
        public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {

            log.info("Fetch all products request");

            List<ProductResponseDTO> products = productService.getAllProducts();

            log.info("Fetched products and they are  {}", products.size());

            return ResponseEntity.ok(products);
        }

        @GetMapping("/active")
        public ResponseEntity<List<ProductResponseDTO>> getActiveProducts() {

            log.info("Fetch active products request");

            List<ProductResponseDTO> products = productService.getActiveProducts();

            log.info("Fetched {} active products", products.size());

            return ResponseEntity.ok(products);
        }

        @GetMapping("/{id}")
        public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {

            log.info("Fetch product by id: {}", id);

            ProductResponseDTO product = productService.getProductById(id);

            log.info("Product {} fetched successfully", id);

            return ResponseEntity.ok(product);
        }

        @PutMapping("/{id}")
        public ResponseEntity<ProductResponseDTO> updateProduct(
                @PathVariable Long id,
                @RequestBody ProductRequestUpdateDTO request) {

            log.info("Update product request for id: {}", id);

            ProductResponseDTO product = productService.updateProduct(id, request);

            log.info("Product {} updated successfully", id);

            return ResponseEntity.ok(product);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponseDTO> deleteProduct(@PathVariable Long id) {

            log.info("Delete product request for id: {}", id);

            productService.deleteProduct(id);

            log.info("Product {} deleted successfully", id);

            return ResponseEntity.ok(new ApiResponseDTO("Product deleted successfully", true));
        }

        @PatchMapping("/{id}/activate")
        public ResponseEntity<ApiResponseDTO> activateProduct(@PathVariable Long id) {

            log.info("Activate product request for id: {}", id);

            productService.activateProduct(id);

            log.info("Product {} activated successfully", id);

            return ResponseEntity.ok(new ApiResponseDTO("Product activated successfully", true));
        }

    }
