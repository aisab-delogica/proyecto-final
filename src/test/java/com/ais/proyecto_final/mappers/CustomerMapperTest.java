package com.ais.proyecto_final.mappers;

import com.ais.proyecto_final.dto.customer.AddressResponseDTO;
import com.ais.proyecto_final.dto.customer.CustomerRequestDTO;
import com.ais.proyecto_final.dto.customer.CustomerResponseDTO;
import com.ais.proyecto_final.entity.Address;
import com.ais.proyecto_final.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CustomerMapperTest {

    @InjectMocks
    private CustomerMapperImpl customerMapper;

    @Mock
    private AddressMapper addressMapper;

    private CustomerRequestDTO requestDTO;
    private Customer entity;
    private final Long CUSTOMER_ID = 1L;
    private final LocalDateTime FIXED_TIME = LocalDateTime.of(2023, 1, 1, 10, 0);
    private final String OLD_EMAIL = "old@mail.com";

    @BeforeEach
    void setUp() {
        requestDTO = new CustomerRequestDTO();
        requestDTO.setFullName("Ana Lopez");
        requestDTO.setEmail("ana.lopez@test.com");
        requestDTO.setPhone("987654321");

        entity = Customer.builder()
                .id(CUSTOMER_ID)
                .fullName("Ana Lopez")
                .email("ana.lopez@test.com")
                .phone("987654321")
                .createdAt(FIXED_TIME)
                .updatedAt(FIXED_TIME)
                .addresses(List.of(Address.builder().id(10L).build()))
                .build();
    }

    @Test
    void toEntity_ShouldMapRequestDTOToCustomerEntity() {
        Customer result = customerMapper.toEntity(requestDTO);

        assertNotNull(result);
        assertEquals("Ana Lopez", result.getFullName());
        assertEquals("ana.lopez@test.com", result.getEmail());
        assertEquals("987654321", result.getPhone());
        assertNotNull(result.getAddresses());
        assertEquals(0, result.getAddresses().size());
    }

    @Test
    void toResponseDto_ShouldMapCustomerEntityToResponseDTO() {
        AddressResponseDTO addressResponse = new AddressResponseDTO();
        addressResponse.setId(10L);

        
        
        Mockito.lenient().when(addressMapper.toResponseDto(any(Address.class))).thenReturn(addressResponse);

        CustomerResponseDTO result = customerMapper.toResponseDto(entity);

        assertNotNull(result);
        assertEquals(CUSTOMER_ID, result.getId());
        assertEquals("Ana Lopez", result.getFullName());
        assertEquals(FIXED_TIME, result.getCreatedAt());
        assertNotNull(result.getAddresses());
        assertEquals(1, result.getAddresses().size());
        assertEquals(10L, result.getAddresses().get(0).getId());
    }

    @Test
    void updateEntityFromDto_ShouldUpdateFields() {
        CustomerRequestDTO updateDTO = new CustomerRequestDTO();
        updateDTO.setFullName("Ana Updated");
        updateDTO.setPhone("111222333");
        
        
        updateDTO.setEmail(OLD_EMAIL);

        Customer entityToUpdate = Customer.builder()
                .id(CUSTOMER_ID)
                .fullName("Old Name")
                .email(OLD_EMAIL)
                .phone("000000000")
                .createdAt(FIXED_TIME)
                .updatedAt(FIXED_TIME)
                .build();

        customerMapper.updateEntityFromDto(updateDTO, entityToUpdate);

        assertEquals("Ana Updated", entityToUpdate.getFullName());
        assertEquals(OLD_EMAIL, entityToUpdate.getEmail());
        assertEquals("111222333", entityToUpdate.getPhone());
        assertEquals(CUSTOMER_ID, entityToUpdate.getId());
    }
}