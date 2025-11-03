package com.ais.proyecto_final.mappers;

import com.ais.proyecto_final.dto.order.LineItemRequestDTO;
import com.ais.proyecto_final.dto.order.LineItemResponseDTO;
import com.ais.proyecto_final.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)

    @Mapping(target = "product", ignore = true)

    @Mapping(target = "unitPrice", ignore = true)

    OrderItem toEntity(LineItemRequestDTO dto);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(target = "lineTotal", expression = "java(entity.getUnitPrice().multiply(java.math.BigDecimal.valueOf(entity.getQuantity())))")
    LineItemResponseDTO toResponseDto(OrderItem entity);
}