package com.brief.demo.helpers;


import com.brief.demo.model.Permissions;
import com.brief.demo.model.Roles;
import com.brief.demo.repository.PermissionsRepository;
import com.brief.demo.repository.RolesRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RolesPermissionsInitializer {

    private final RolesRepository rolesRepository;
    private final PermissionsRepository permissionsRepository;

//    @PostConstruct
    public void initRolesPermissions(){

        // --- Fetch roles ---
        Roles adminRole = rolesRepository.findByRoleName("ADMIN");
        Roles warehouseRole = rolesRepository.findByRoleName("WAREHOUSE_MANAGER");
        Roles clientRole = rolesRepository.findByRoleName("CLIENT");

        // --- Fetch permissions ---
        Permissions userCreate = permissionsRepository.findByName("USER_CREATE");
        Permissions userRead = permissionsRepository.findByName("USER_READ");
        Permissions userUpdate = permissionsRepository.findByName("USER_UPDATE");
        Permissions userDelete = permissionsRepository.findByName("USER_DELETE");
        Permissions userProfileRead = permissionsRepository.findByName("USER_PROFILE_READ");
        Permissions userProfileUpdate = permissionsRepository.findByName("USER_PROFILE_UPDATE");

        Permissions productCreate = permissionsRepository.findByName("PRODUCT_CREATE");
        Permissions productRead = permissionsRepository.findByName("PRODUCT_READ");
        Permissions productUpdate = permissionsRepository.findByName("PRODUCT_UPDATE");
        Permissions productDelete = permissionsRepository.findByName("PRODUCT_DELETE");
        Permissions productReadActive = permissionsRepository.findByName("PRODUCT_READ_ACTIVE");

        Permissions warehouseCreate = permissionsRepository.findByName("WAREHOUSE_CREATE");
        Permissions warehouseRead = permissionsRepository.findByName("WAREHOUSE_READ");
        Permissions warehouseUpdate = permissionsRepository.findByName("WAREHOUSE_UPDATE");
        Permissions warehouseDelete = permissionsRepository.findByName("WAREHOUSE_DELETE");

        Permissions inventoryRead = permissionsRepository.findByName("INVENTORY_READ");

        Permissions stockMovementCreate = permissionsRepository.findByName("STOCK_MOVEMENT_CREATE");
        Permissions stockMovementRead = permissionsRepository.findByName("STOCK_MOVEMENT_READ");
        Permissions stockMovementUpdate = permissionsRepository.findByName("STOCK_MOVEMENT_UPDATE");
        Permissions stockMovementDelete = permissionsRepository.findByName("STOCK_MOVEMENT_DELETE");

        Permissions supplierCreate = permissionsRepository.findByName("SUPPLIER_CREATE");
        Permissions supplierRead = permissionsRepository.findByName("SUPPLIER_READ");
        Permissions supplierUpdate = permissionsRepository.findByName("SUPPLIER_UPDATE");
        Permissions supplierDelete = permissionsRepository.findByName("SUPPLIER_DELETE");

        Permissions purchaseOrderCreate = permissionsRepository.findByName("PURCHASE_ORDER_CREATE");
        Permissions purchaseOrderRead = permissionsRepository.findByName("PURCHASE_ORDER_READ");
        Permissions purchaseOrderUpdate = permissionsRepository.findByName("PURCHASE_ORDER_UPDATE");
        Permissions purchaseOrderDelete = permissionsRepository.findByName("PURCHASE_ORDER_DELETE");

        Permissions orderCreate = permissionsRepository.findByName("ORDER_CREATE");
        Permissions orderRead = permissionsRepository.findByName("ORDER_READ");
        Permissions orderUpdate = permissionsRepository.findByName("ORDER_UPDATE");
        Permissions orderDelete = permissionsRepository.findByName("ORDER_DELETE");
        Permissions orderReserve = permissionsRepository.findByName("ORDER_RESERVE");
        Permissions orderShip = permissionsRepository.findByName("ORDER_SHIP");
        Permissions orderReadOwn = permissionsRepository.findByName("ORDER_READ_OWN");

        Permissions shipmentCreate = permissionsRepository.findByName("SHIPMENT_CREATE");
        Permissions shipmentRead = permissionsRepository.findByName("SHIPMENT_READ");
        Permissions shipmentUpdate = permissionsRepository.findByName("SHIPMENT_UPDATE");
        Permissions shipmentDelete = permissionsRepository.findByName("SHIPMENT_DELETE");
        Permissions shipmentReadOwn = permissionsRepository.findByName("SHIPMENT_READ_OWN");

        // --- Assign permissions to ADMIN ---
        adminRole.getPermissions().addAll(Set.of(
                userCreate, userRead, userUpdate, userDelete, userProfileRead, userProfileUpdate,
                productCreate, productRead, productUpdate, productDelete, productReadActive,
                warehouseCreate, warehouseRead, warehouseUpdate, warehouseDelete,
                inventoryRead,
                stockMovementCreate, stockMovementRead, stockMovementUpdate, stockMovementDelete,
                supplierCreate, supplierRead, supplierUpdate, supplierDelete,
                purchaseOrderCreate, purchaseOrderRead, purchaseOrderUpdate, purchaseOrderDelete,
                orderCreate, orderRead, orderUpdate, orderDelete, orderReserve, orderShip, orderReadOwn,
                shipmentCreate, shipmentRead, shipmentUpdate, shipmentDelete, shipmentReadOwn
        ));

        // --- Assign permissions to WAREHOUSE_MANAGER ---
        warehouseRole.getPermissions().addAll(Set.of(
                userRead, userProfileRead, userProfileUpdate,
                productRead, productReadActive,
                warehouseRead, warehouseUpdate,
                inventoryRead,
                stockMovementCreate, stockMovementRead, stockMovementUpdate, stockMovementDelete,
                supplierRead,
                purchaseOrderRead,
                orderRead, orderUpdate, orderReserve, orderShip,
                shipmentCreate, shipmentRead, shipmentUpdate, shipmentDelete
        ));

        // --- Assign permissions to CLIENT ---
        clientRole.getPermissions().addAll(Set.of(
                userProfileRead, userProfileUpdate,
                orderCreate, orderReadOwn, orderUpdate, orderDelete,
                shipmentReadOwn
        ));


        // --- Save roles to update pivot table ---
        rolesRepository.save(adminRole);
        rolesRepository.save(warehouseRole);
        rolesRepository.save(clientRole);

        System.out.println("Roles and permissions initialized successfully!");
    }
}
