package com.brief.demo.mappers;

import com.brief.demo.dto.response.BackOrderResponseDTO;
import com.brief.demo.enums.BackOrderStatus;
import com.brief.demo.model.BackOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BackOrderMapperTest {

    @InjectMocks
    private BackOrderMapper backOrderMapper;

    private BackOrder backOrder;

    @BeforeEach
    void setUp() {
        backOrder = BackOrder.builder()
                .id(1L)
                .quantity(10)
                .status(BackOrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .fulfilledAt(LocalDateTime.now().plusDays(1))
                .build();
    }

    @Test
    void toResponse_ShouldMapAllFields() {
        //  ARRANGE - Already set up in beforeEach

        //  ACT
        BackOrderResponseDTO response = backOrderMapper.toResponse(backOrder);

        //  ASSERT
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(10, response.getQuantity());
        assertEquals(BackOrderStatus.PENDING, response.getStatus());
        assertEquals(backOrder.getCreatedAt(), response.getCreatedAt());
        assertEquals(backOrder.getFulfilledAt(), response.getFulfilledAt());
    }

    @Test
    void toResponse_WhenNullFulfilledAt_ShouldHandleGracefully() {
        //  ARRANGE
        backOrder.setFulfilledAt(null);

        //  ACT
        BackOrderResponseDTO response = backOrderMapper.toResponse(backOrder);

        //  ASSERT
        assertNotNull(response);
        assertNull(response.getFulfilledAt());
    }
}