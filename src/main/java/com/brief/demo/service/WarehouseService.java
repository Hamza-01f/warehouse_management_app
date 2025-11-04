package com.brief.demo.service;

import com.brief.demo.dto.request.WarehouseRequestDTO;
import com.brief.demo.dto.response.WarehouseResponseDTO;
import com.brief.demo.exception.DuplicateResourceException;
import com.brief.demo.exception.ResourceNotFoundException;
import com.brief.demo.mappers.WarehouseMapper;
import com.brief.demo.model.Warehouse;
import com.brief.demo.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    public WarehouseResponseDTO createWarehouse(WarehouseRequestDTO request) {
        if (warehouseRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Warehouse with name " + request.getName() + " already exists");
        }

        Warehouse warehouse = warehouseMapper.toEntity(request);
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        return warehouseMapper.toResponse(savedWarehouse);
    }

    public WarehouseResponseDTO getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));
        return warehouseMapper.toResponse(warehouse);
    }

    public List<WarehouseResponseDTO> getAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(warehouseMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<WarehouseResponseDTO> getActiveWarehouses() {
        return warehouseRepository.findByIsActiveTrue().stream()
                .map(warehouseMapper::toResponse)
                .collect(Collectors.toList());
    }

    public WarehouseResponseDTO updateWarehouse(Long id, WarehouseRequestDTO request) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));

        if (request.getName() != null && warehouseRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new DuplicateResourceException("Warehouse with name " + request.getName() + " already exists");
        }

        if (request.getName() != null) warehouse.setName(request.getName());
        if (request.getAddress() != null) warehouse.setAddress(request.getAddress());

        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        return warehouseMapper.toResponse(updatedWarehouse);
    }

    public void deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));

        warehouse.setIsActive(false);
        warehouseRepository.save(warehouse);
    }

    public void activateWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));

        warehouse.setIsActive(true);
        warehouseRepository.save(warehouse);
    }
}