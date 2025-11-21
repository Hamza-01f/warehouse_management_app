# Spring Security Documentation & Basic Auth Implementation

## 📋 Table of Contents
1. [Project Overview](#project-overview)
2. [Spring Security Fundamentals](#spring-security-fundamentals)
3. [Modern Architecture](#modern-spring-security-architecture)
4. [Basic Auth Implementation](#basic-auth-implementation)
5. [API Endpoints Security](#api-endpoints-security)
6. [Testing & Validation](#testing--validation)
7. [Setup & Usage](#setup--usage)

---

## Project Overview

This project implements **Spring Security Basic Authentication** as a foundational security layer for a REST API. It covers theoretical foundations and provides a working POC with role-based access control.

**Key Features:**
- Basic Authentication with HTTPS requirement
- Role-based authorization (ADMIN, WAREHOUSE_MANAGER, CLIENT)
- In-memory user management
- BCrypt password encoding
- Secure endpoint protection

## Spring Security Fundamentals

### 🔐 Authentication vs Authorization

| Concept | Definition | Example |
|---------|------------|---------|
| **Authentication** | Verify user identity | "Who are you?" |
| **Authorization** | Determine access permissions | "What are you allowed to do?" |

### 🛡️ Common Web Attacks

| Attack | Description | Protection |
|---------|-------------|------------|
| **Brute Force** | Repeated login attempts | Rate limiting, account lock |
| **XSS** | Malicious script injection | Input validation, encoding |
| **CSRF** | Forged requests from trusted users | CSRF tokens, same-site cookies |
| **Session Fixation** | Session ID manipulation | Session regeneration on login |
| **Session Hijacking** | Stealing session data | HTTPS, secure cookies |

### 🔒 Security Principles
- **Defense in Depth**: Multiple security layers
- **HTTPS Mandatory**: Encrypt all communications
- **Backend Security**: Never trust client-side validation

## Modern Spring Security Architecture

### Core Components

| Component | Role |
|-----------|------|
| `SecurityFilterChain` | Main security configuration bean |
| `DelegatingFilterProxy` | Bridges Servlet container and Spring |
| `AuthenticationManager` | Orchestrates authentication process |
| `AuthenticationProvider` | Performs actual authentication |
| `UserDetailsService` | Loads user-specific data |
| `PasswordEncoder` | Handles password encoding/verification |

### Request Flow

HTTP Request → DelegatingFilterProxy → SecurityFilterChain → AuthenticationManager → AuthenticationProvider → UserDetailsService → Authorization


### Roles vs Authorities
- **Roles**: `ROLE_ADMIN`, `ROLE_USER` (grouped permissions)
- **Authorities**: `READ_PRODUCTS`, `WRITE_ORDERS` (specific permissions)

## Basic Auth Implementation

### Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/inventory/**").hasRole("WAREHOUSE_MANAGER")
                .requestMatchers("/api/orders/**").hasRole("CLIENT")
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```
User Configuration
```java
@Bean
public UserDetailsService userDetailsService() {
    UserDetails admin = User.builder()
        .username("admin")
        .password(passwordEncoder().encode("admin123"))
        .roles("ADMIN")
        .build();

    UserDetails warehouseManager = User.builder()
        .username("warehouse")
        .password(passwordEncoder().encode("warehouse123"))
        .roles("WAREHOUSE_MANAGER")
        .build();

    UserDetails client = User.builder()
        .username("client")
        .password(passwordEncoder().encode("client123"))
        .roles("CLIENT")
        .build();

    return new InMemoryUserDetailsManager(admin, warehouseManager, client);
}
```

Role Definitions


Role	| Permissions |
ADMIN	| Full system access, product management |
WAREHOUSE_MANAGER	| Inventory, stock movements, shipments |
CLIENT	| Order creation and tracking |

