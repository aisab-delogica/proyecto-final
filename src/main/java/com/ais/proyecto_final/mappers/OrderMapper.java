package com.ais.proyecto_final.mappers;

import com.ais.proyecto_final.dto.order.OrderRequestDTO;
import com.ais.proyecto_final.dto.order.OrderResponseDTO;
import com.ais.proyecto_final.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "shippingAddress", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "items", source = "items")
    Order toEntity(OrderRequestDTO dto);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "shippingAddress.id", target = "shippingAddressId")
    @Mapping(source = "items", target = "items")
    OrderResponseDTO toResponseDto(Order entity);
}