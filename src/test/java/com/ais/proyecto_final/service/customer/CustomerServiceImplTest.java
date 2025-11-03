package com.ais.proyecto_final.service.customer;

import com.ais.proyecto_final.dto.customer.AddressRequestDTO;
import com.ais.proyecto_final.dto.customer.AddressResponseDTO;
import com.ais.proyecto_final.dto.customer.CustomerRequestDTO;
import com.ais.proyecto_final.dto.customer.CustomerResponseDTO;
import com.ais.proyecto_final.entity.Address;
import com.ais.proyecto_final.entity.Customer;
import com.ais.proyecto_final.exceptions.DuplicateResourceException;
import com.ais.proyecto_final.mappers.AddressMapper;
import com.ais.proyecto_final.mappers.CustomerMapper;
import com.ais.proyecto_final.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    private final Long CUSTOMER_ID = 1L;
    private final Long ADDRESS_ID = 10L;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private CustomerRequestDTO customerRequest;
    private Customer customerEntity;
    private CustomerResponseDTO customerResponse;
    private Address addressEntity;
    private AddressResponseDTO addressResponse;
    private final Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        customerRequest = new CustomerRequestDTO();
        customerRequest.setFullName("Juan Perez");
        customerRequest.setEmail("juan.perez@test.com");
        customerRequest.setPhone("123456789");

        customerEntity = Customer.builder()
                .id(CUSTOMER_ID)
                .fullName("Juan Perez")
                .email("juan.perez@test.com")
                .phone("123456789")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .addresses(new ArrayList<>())
                .build();

        customerResponse = new CustomerResponseDTO();
        customerResponse.setId(CUSTOMER_ID);

        addressEntity = Address.builder()
                .id(ADDRESS_ID)
                .line1("Calle Falsa 123")
                .city("Springfield")
                .country("USA")
                .isDefault(true)
                .customer(customerEntity)
                .build();

        addressResponse = new AddressResponseDTO();
        addressResponse.setId(ADDRESS_ID);
        addressResponse.setLine1("Calle Falsa 123");
        addressResponse.setDefault(true);
    }

    
    
    

    @Test
    void createCustomer_ShouldSucceed_WhenEmailIsUnique() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerMapper.toEntity(customerRequest)).thenReturn(customerEntity);
        when(customerRepository.save(customerEntity)).thenReturn(customerEntity);
        when(customerMapper.toResponseDto(customerEntity)).thenReturn(customerResponse);

        CustomerResponseDTO result = customerService.createCustomer(customerRequest);

        assertNotNull(result);
        assertEquals(CUSTOMER_ID, result.getId());
        verify(customerRepository, times(1)).save(customerEntity);
    }

    @Test
    void createCustomer_ShouldThrowDuplicateResourceException_WhenEmailExists() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> customerService.createCustomer(customerRequest));
        verify(customerRepository, never()).save(any(Customer.class));
    }


    @Test
    void findAllCustomers_ShouldCallFindByEmail_WhenEmailFilterIsProvided() {
        String email = "test@test.com";
        Page<Customer> page = new PageImpl<>(List.of(customerEntity));
        when(customerRepository.findByEmailContainingIgnoreCase(email, pageable)).thenReturn(page);

        customerService.findAllCustomers(email, pageable);

        verify(customerRepository, times(1)).findByEmailContainingIgnoreCase(email, pageable);
        verify(customerRepository, never()).findAll(pageable);
    }

    @Test
    void findAllCustomers_ShouldCallFindAll_WhenNoFilterIsProvided() {
        Page<Customer> page = new PageImpl<>(List.of(customerEntity));
        when(customerRepository.findAll(pageable)).thenReturn(page);

        customerService.findAllCustomers(null, pageable);

        verify(customerRepository, times(1)).findAll(pageable);
        verify(customerRepository, never()).findByEmailContainingIgnoreCase(anyString(), any(Pageable.class));
    }

    @Test
    void getCustomerById_ShouldReturnCustomer_WhenFound() {
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customerEntity));
        when(customerMapper.toResponseDto(customerEntity)).thenReturn(customerResponse);

        CustomerResponseDTO result = customerService.getCustomerById(CUSTOMER_ID);

        assertNotNull(result);
        assertEquals(CUSTOMER_ID, result.getId());
    }

    @Test
    void getCustomerById_ShouldThrowNotFound_WhenCustomerDoesNotExist() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.getCustomerById(99L));
    }

    
    
    

    @Test
    void updateCustomer_ShouldSucceed_WhenFound() {
        CustomerRequestDTO updateRequest = new CustomerRequestDTO();
        updateRequest.setFullName("Juan Updated");

        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customerEntity));
        when(customerRepository.save(customerEntity)).thenReturn(customerEntity);
        when(customerMapper.toResponseDto(customerEntity)).thenReturn(customerResponse);

        CustomerResponseDTO result = customerService.updateCustomer(CUSTOMER_ID, updateRequest);

        verify(customerMapper, times(1)).updateEntityFromDto(updateRequest, customerEntity);
        verify(customerRepository, times(1)).save(customerEntity);
    }

    
    
    

    @Test
    void addAddressToCustomer_ShouldSetDefaultTrue_AndClearOthers() {
        
        Address existingAddress = Address.builder().id(11L).isDefault(true).customer(customerEntity).build();
        customerEntity.getAddresses().add(existingAddress);

        AddressRequestDTO newAddressRequest = new AddressRequestDTO();
        newAddressRequest.setLine1("Nueva Direccion");
        newAddressRequest.setDefault(true); 

        Address newAddressEntity = Address.builder().id(12L).line1("Nueva Direccion").isDefault(true).customer(customerEntity).build();

        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customerEntity));
        when(addressMapper.toEntity(newAddressRequest)).thenReturn(newAddressEntity);
        when(addressMapper.toResponseDto(newAddressEntity)).thenReturn(addressResponse);

        
        customerService.addAddressToCustomer(CUSTOMER_ID, newAddressRequest);

        
        assertTrue(newAddressEntity.isDefault()); 
        assertFalse(existingAddress.isDefault()); 
        verify(customerRepository, times(1)).save(customerEntity);
    }

    @Test
    void addAddressToCustomer_ShouldSetDefaultFalse_WhenRequestIsFalse() {
        
        AddressRequestDTO newAddressRequest = new AddressRequestDTO();
        newAddressRequest.setLine1("Otra Direccion");
        newAddressRequest.setDefault(false); 

        Address newAddressEntity = Address.builder().id(12L).line1("Otra Direccion").isDefault(false).customer(customerEntity).build();

        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customerEntity));
        when(addressMapper.toEntity(newAddressRequest)).thenReturn(newAddressEntity);
        when(addressMapper.toResponseDto(newAddressEntity)).thenReturn(addressResponse);

        
        customerService.addAddressToCustomer(CUSTOMER_ID, newAddressRequest);

        
        assertFalse(newAddressEntity.isDefault()); 
        verify(customerRepository, times(1)).save(customerEntity);
    }

    
    
    

    @Test
    void markAddressAsDefault_ShouldSucceed_AndMarkNewAddress() {
        
        Address oldDefault = Address.builder().id(11L).isDefault(true).customer(customerEntity).build();
        Address newDefault = Address.builder().id(ADDRESS_ID).isDefault(false).customer(customerEntity).build();
        customerEntity.getAddresses().add(oldDefault);
        customerEntity.getAddresses().add(newDefault);

        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customerEntity));
        when(addressMapper.toResponseDto(newDefault)).thenReturn(addressResponse);

        
        customerService.markAddressAsDefault(CUSTOMER_ID, ADDRESS_ID);

        
        assertTrue(newDefault.isDefault());
        assertFalse(oldDefault.isDefault());
    }

    @Test
    void markAddressAsDefault_ShouldThrowNotFound_WhenAddressIdIsNotInCustomer() {
        
        Address existingAddress = Address.builder().id(11L).isDefault(true).customer(customerEntity).build();
        customerEntity.getAddresses().add(existingAddress);

        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customerEntity));

        
        assertThrows(EntityNotFoundException.class, () -> customerService.markAddressAsDefault(CUSTOMER_ID, 99L));
    }

    @Test
    void markAddressAsDefault_ShouldThrowNotFound_WhenCustomerDoesNotExist() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.markAddressAsDefault(99L, ADDRESS_ID));
    }
}