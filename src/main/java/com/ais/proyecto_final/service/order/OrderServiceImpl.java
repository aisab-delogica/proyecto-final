package com.ais.proyecto_final.service.order;

import com.ais.proyecto_final.dto.order.OrderRequestDTO;
import com.ais.proyecto_final.dto.order.OrderResponseDTO;
import com.ais.proyecto_final.dto.order.OrderStatusUpdateDTO;
import com.ais.proyecto_final.entity.Order;
import com.ais.proyecto_final.entity.OrderStatus;
import com.ais.proyecto_final.mappers.OrderMapper;
import com.ais.proyecto_final.repository.OrderRepository;
import com.ais.proyecto_final.service.customer.CustomerService;
import com.ais.proyecto_final.service.product.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CustomerService customerService;
    private final ProductService productService;

    @Transactional
    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequest) {
        // TODO: Lógica de negocio crítica:
        // 1. Validar customerId y shippingAddressId existen (usando CustomerService).
        // 2. Iterar items: validar productId existe, validar stock suficiente.
        // 3. Mapear OrderRequestDTO a Entidad Order (incluyendo LineItems) y CALCULAR TOTALES.
        // 4. Aplicar REDUCCIÓN DE STOCK transaccional (usando ProductService).
        // 5. Guardar la Orden y mapear a OrderResponseDTO.
        throw new UnsupportedOperationException("pendiente.");
    }

    @Transactional
    @Override
    public Page<OrderResponseDTO> findAllOrders(Long customerId, LocalDate fromDate, LocalDate toDate, OrderStatus status, Pageable pageable) {
        // TODO: Implementar Specification o query methods en el repositorio para el filtrado.
        // Mapear Page<Order> a Page<OrderResponseDTO>.
        throw new UnsupportedOperationException("Lógica de filtrado y paginación de pedidos pendiente.");
    }
    @Transactional
    @Override
    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido " + id + " no encontrado."));

        return orderMapper.toResponseDto(order);
    }

    @Transactional
    @Override
    public OrderResponseDTO updateOrderStatus(Long id, OrderStatusUpdateDTO dto) {
        // TODO: Lógica de negocio crítica:
        // 1. Buscar Orden por ID.
        // 2. Implementar la lógica de **TRANSICIÓN DE ESTADOS VÁLIDA** (ej. de CREATED a SHIPPED, pero no de SHIPPED a CREATED).
        // 3. Actualizar estado, guardar y mapear a DTO de respuesta.
        throw new UnsupportedOperationException("Lógica de transición de estados de pedido pendiente.");
    }
}