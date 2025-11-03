package com.ais.proyecto_final.mappers;

import com.ais.proyecto_final.dto.order.LineItemRequestDTO;
import com.ais.proyecto_final.dto.order.LineItemResponseDTO;
import com.ais.proyecto_final.entity.OrderItem;
import com.ais.proyecto_final.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OrderItemMapperTest {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Test
    void shouldMapRequestDtoToEntity() {
        LineItemRequestDTO dto = LineItemRequestDTO.builder()
                .productId(5L)
                .quantity(10)
                .build();

        OrderItem entity = orderItemMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getQuantity()).isEqualTo(10);
        assertThat(entity.getId()).isNull();
        assertThat(entity.getOrder()).isNull();
        assertThat(entity.getProduct()).isNull();
        assertThat(entity.getUnitPrice()).isNull();
    }

    @Test
    void shouldMapEntityToResponseDtoAndCalculateLineTotal() {
        Product mockProduct = Product.builder().id(99L).build();

        OrderItem entity = OrderItem.builder()
                .id(1L)
                .product(mockProduct)
                .quantity(3)
                .unitPrice(new BigDecimal("10.50"))
                .build();

        LineItemResponseDTO dto = orderItemMapper.toResponseDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getProductId()).isEqualTo(99L);
        assertThat(dto.getQuantity()).isEqualTo(3);
        assertThat(dto.getUnitPrice()).isEqualByComparingTo("10.50");

        // 3 * 10.50 = 31.50
        assertThat(dto.getLineTotal()).isEqualByComparingTo("31.50");
    }

    @Test
    void shouldMapEntityToResponseDtoWithZeroQuantity() {
        Product mockProduct = Product.builder().id(99L).build();

        OrderItem entity = OrderItem.builder()
                .id(1L)
                .product(mockProduct)
                .quantity(0) // Caso borde
                .unitPrice(new BigDecimal("10.50"))
                .build();


        LineItemResponseDTO dto = orderItemMapper.toResponseDto(entity);


        assertThat(dto).isNotNull();
        assertThat(dto.getProductId()).isEqualTo(99L);
        assertThat(dto.getQuantity()).isEqualTo(0);
        assertThat(dto.getUnitPrice()).isEqualByComparingTo("10.50");

        // 0 * 10.50 = 0
        assertThat(dto.getLineTotal()).isEqualByComparingTo("0.00");
    }
}