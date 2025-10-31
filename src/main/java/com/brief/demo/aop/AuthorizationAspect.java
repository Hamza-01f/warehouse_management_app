package com.brief.demo.aop;


import com.brief.demo.enums.Role;
import com.brief.demo.exception.UnauthorizedException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthorizationAspect {

    private Role getCurrentUserRole(){
        return Role.ADMIN;
    }

    @Before("@annotation(com.example.aop.RequiresAdmin)")
    public void checkAdminAccess(){
        Role currentRole = getCurrentUserRole();
        if (currentRole != Role.ADMIN) {
            throw new UnauthorizedException("Admin role required");
        }
    }

    @Before("@annotation(com.example.aop.RequiresWarehouseManager)")
    public void checkWarehouseManagerAccess(){
        Role currentRole = getCurrentUserRole();
        if(currentRole != Role.ADMIN || currentRole != Role.WAREHOUSE_MANAGER){
            throw new UnauthorizedException("Warehouse manager or admin role required");
        }
    }
}
