package com.brief.demo.service;

import com.brief.demo.dto.request.SupplierRequestDTO;
import com.brief.demo.dto.response.SupplierResponseDTO;
import com.brief.demo.exception.DuplicateResourceException;
import com.brief.demo.exception.ResourceNotFoundException;
import com.brief.demo.mappers.SupplierMapper;
import com.brief.demo.model.Supplier;
import com.brief.demo.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    public SupplierResponseDTO createSupplier(SupplierRequestDTO request) {
        if (supplierRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Supplier with name " + request.getName() + " already exists");
        }

        Supplier supplier = supplierMapper.toEntity(request);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toResponse(savedSupplier);
    }

    public SupplierResponseDTO getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        return supplierMapper.toResponse(supplier);
    }

    public List<SupplierResponseDTO> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(supplierMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<SupplierResponseDTO> getActiveSuppliers() {
        return supplierRepository.findByIsActiveTrue().stream()
                .map(supplierMapper::toResponse)
                .collect(Collectors.toList());
    }

    public SupplierResponseDTO updateSupplier(Long id, SupplierRequestDTO request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));

        if (request.getName() != null && supplierRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new DuplicateResourceException("Supplier with name " + request.getName() + " already exists");
        }

        if (request.getName() != null) supplier.setName(request.getName());
        if (request.getContact() != null) supplier.setContact(request.getContact());

        Supplier updatedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toResponse(updatedSupplier);
    }

    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));

        supplier.setIsActive(false);
        supplierRepository.save(supplier);
    }

    public void activateSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));

        supplier.setIsActive(true);
        supplierRepository.save(supplier);
    }
}