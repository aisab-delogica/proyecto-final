package com.ais.proyecto_final.service.order;

import com.ais.proyecto_final.dto.order.LineItemRequestDTO;
import com.ais.proyecto_final.dto.order.OrderRequestDTO;
import com.ais.proyecto_final.dto.order.OrderResponseDTO;
import com.ais.proyecto_final.dto.order.OrderStatusUpdateDTO;
import com.ais.proyecto_final.entity.*;
import com.ais.proyecto_final.exceptions.OrderBusinessException;
import com.ais.proyecto_final.mappers.OrderItemMapper;
import com.ais.proyecto_final.mappers.OrderMapper;
import com.ais.proyecto_final.repository.AddressRepository;
import com.ais.proyecto_final.repository.CustomerRepository;
import com.ais.proyecto_final.repository.OrderRepository;
import com.ais.proyecto_final.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Customer testCustomer;
    private Address testAddress;
    private Product testProduct;
    private OrderRequestDTO validRequest;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder().id(1L).email("test@mail.com").build();
        testAddress = Address.builder().id(2L).customer(testCustomer).line1("Calle Falsa 123").build();
        testProduct = Product.builder().id(10L).sku("PROD-01").name("Laptop").price(new BigDecimal("1000.00")).stock(5).active(true).build();

        validRequest = OrderRequestDTO.builder()
                .customerId(1L)
                .shippingAddressId(2L)
                .items(List.of(
                        LineItemRequestDTO.builder().productId(10L).quantity(1).build()
                ))
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(addressRepository.findById(2L)).thenReturn(Optional.of(testAddress));
        when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));

        when(orderMapper.toEntity(any(OrderRequestDTO.class))).thenReturn(Order.builder().items(new ArrayList<>()).build());

        when(orderItemMapper.toEntity(any())).thenReturn(OrderItem.builder().build());
    }

    @Test
    void createOrder_ShouldCreateAndReturnOrder_Success() {
        Order persistedOrder = Order.builder()
                .id(1L)
                .status(OrderStatus.CREATED)
                .total(new BigDecimal("1000.00"))
                .items(new ArrayList<>())
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(persistedOrder);
        when(orderMapper.toResponseDto(any(Order.class))).thenReturn(OrderResponseDTO.builder().id(1L).total(new BigDecimal("1000.00")).status(OrderStatus.PAID).build());

        OrderResponseDTO result = orderService.createOrder(validRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(orderRepository, times(2)).save(any(Order.class));
        verify(productRepository, times(1)).save(any(Product.class));
        assertEquals(4, testProduct.getStock());
    }

    @Test
    void createOrder_ShouldThrowException_CustomerNotFound() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(validRequest));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_ShouldThrowException_AddressDoesNotBelongToCustomer() {
        Address otherAddress = Address.builder().id(3L).customer(Customer.builder().id(99L).build()).build();
        when(addressRepository.findById(anyLong())).thenReturn(Optional.of(otherAddress));

        assertThrows(OrderBusinessException.class, () -> orderService.createOrder(validRequest));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_ShouldThrowException_ProductNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(validRequest));
    }

    @Test
    void createOrder_ShouldThrowException_ProductInactive() {
        testProduct.setActive(false);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));

        assertThrows(OrderBusinessException.class, () -> orderService.createOrder(validRequest));
    }

    @Test
    void createOrder_ShouldThrowException_InsufficientStock() {
        testProduct.setStock(0);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));

        assertThrows(OrderBusinessException.class, () -> orderService.createOrder(validRequest));
    }

    @Test
    void updateOrderStatus_ShouldUpdateStatus_ValidTransition() {
        Order existingOrder = Order.builder().id(1L).status(OrderStatus.CREATED).items(Collections.emptyList()).build();
        OrderStatusUpdateDTO dto = OrderStatusUpdateDTO.builder().status(OrderStatus.PAID).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(existingOrder);

        when(orderMapper.toResponseDto(any(Order.class))).thenReturn(OrderResponseDTO.builder().status(OrderStatus.PAID).build());

        OrderResponseDTO result = orderService.updateOrderStatus(1L, dto);

        assertEquals(OrderStatus.PAID, result.getStatus());
        verify(orderRepository, times(1)).save(existingOrder);
    }

    @Test
    void updateOrderStatus_ShouldReturnStock_OnCancellationFromCreated() {
        OrderItem item = OrderItem.builder().quantity(2).product(testProduct).build();
        Order existingOrder = Order.builder().id(1L).status(OrderStatus.CREATED).items(new ArrayList<>(List.of(item))).build();

        int initialStock = testProduct.getStock();
        OrderStatusUpdateDTO dto = OrderStatusUpdateDTO.builder().status(OrderStatus.CANCELLED).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(existingOrder);

        when(orderMapper.toResponseDto(any(Order.class))).thenReturn(OrderResponseDTO.builder().status(OrderStatus.CANCELLED).build());

        orderService.updateOrderStatus(1L, dto);

        verify(productRepository, times(1)).save(testProduct);
        assertEquals(initialStock + 2, testProduct.getStock());
    }

    @Test
    void updateOrderStatus_ShouldThrowException_InvalidTransition() {
        Order existingOrder = Order.builder().id(1L).status(OrderStatus.PAID).items(Collections.emptyList()).build();
        OrderStatusUpdateDTO dto = OrderStatusUpdateDTO.builder().status(OrderStatus.CREATED).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));

        assertThrows(OrderBusinessException.class, () -> orderService.updateOrderStatus(1L, dto));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrderStatus_ShouldThrowException_OrderNotFound() {
        OrderStatusUpdateDTO dto = OrderStatusUpdateDTO.builder().status(OrderStatus.PAID).build();
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.updateOrderStatus(1L, dto));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getOrderById_ShouldReturnOrder_Success() {
        Order mockOrder = Order.builder().id(1L).status(OrderStatus.PAID).build();
        when(orderMapper.toResponseDto(any(Order.class))).thenReturn(OrderResponseDTO.builder().id(1L).status(OrderStatus.PAID).build());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        OrderResponseDTO result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(OrderStatus.PAID, result.getStatus());
    }

    @Test
    void getOrderById_ShouldThrowException_NotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.getOrderById(99L));
    }

    @Test
    void findAllOrders_ShouldReturnPageOfOrders_Success() {
        Order mockOrder = Order.builder().id(1L).status(OrderStatus.CREATED).build();
        Page<Order> mockPage = new PageImpl<>(List.of(mockOrder), PageRequest.of(0, 10), 1);
        when(orderMapper.toResponseDto(any(Order.class))).thenReturn(OrderResponseDTO.builder().id(1L).status(OrderStatus.CREATED).build());

        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(mockPage);

        Pageable pageable = PageRequest.of(0, 10);

        Page<OrderResponseDTO> result = orderService.findAllOrders(1L, null, null, OrderStatus.CREATED, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
    }


    @Test
    void updateOrderStatus_ShouldFail_OnInvalidTransition_FromFinalState() {
        Order existingOrder = Order.builder().id(1L).status(OrderStatus.SHIPPED).items(new ArrayList<>()).build();
        OrderStatusUpdateDTO newStatusDto = OrderStatusUpdateDTO.builder().status(OrderStatus.PAID).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));

        assertThrows(OrderBusinessException.class, () -> orderService.updateOrderStatus(1L, newStatusDto));

        verify(orderRepository, never()).save(any());
    }


    @Test
    void updateOrderStatus_ShouldFail_OnInvalidTransition_FromCreatedToShipped() {
        
        Order existingOrder = Order.builder().id(1L).status(OrderStatus.CREATED).items(new ArrayList<>()).build();
        OrderStatusUpdateDTO newStatusDto = OrderStatusUpdateDTO.builder().status(OrderStatus.SHIPPED).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));

        assertThrows(OrderBusinessException.class, () -> orderService.updateOrderStatus(1L, newStatusDto));
    }


    @Test
    void updateOrderStatus_ShouldFail_OnInvalidTransition_Retrograde() {
        
        Order existingOrder = Order.builder().id(1L).status(OrderStatus.PAID).items(new ArrayList<>()).build();
        OrderStatusUpdateDTO newStatusDto = OrderStatusUpdateDTO.builder().status(OrderStatus.CREATED).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));

        assertThrows(OrderBusinessException.class, () -> orderService.updateOrderStatus(1L, newStatusDto));
    }


    @Test
    void updateOrderStatus_ShouldReturnStock_WhenTransitioningToCancelled() {
        
        Product mockProduct = Product.builder().id(1L).stock(3).build();
        Order existingOrder = Order.builder().id(1L).status(OrderStatus.CREATED).build();

        existingOrder.setItems(List.of(
                OrderItem.builder().product(mockProduct).quantity(2).build()
        ));

        OrderStatusUpdateDTO newStatusDto = OrderStatusUpdateDTO.builder().status(OrderStatus.CANCELLED).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(existingOrder);
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        orderService.updateOrderStatus(1L, newStatusDto);

        assertEquals(5, mockProduct.getStock());

        verify(productRepository, times(1)).save(mockProduct);
        verify(orderRepository, times(1)).save(existingOrder);
    }
}