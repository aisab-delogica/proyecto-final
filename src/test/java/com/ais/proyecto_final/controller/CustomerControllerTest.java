package com.ais.proyecto_final.controller;

import com.ais.proyecto_final.dto.customer.AddressRequestDTO;
import com.ais.proyecto_final.dto.customer.AddressResponseDTO;
import com.ais.proyecto_final.dto.customer.CustomerRequestDTO;
import com.ais.proyecto_final.dto.customer.CustomerResponseDTO;
import com.ais.proyecto_final.service.customer.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser // <-- Autentica todas las peticiones
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    private CustomerRequestDTO customerRequest;
    private CustomerResponseDTO customerResponse;
    private AddressRequestDTO addressRequest;
    private AddressResponseDTO addressResponse;

    @BeforeEach
    void setUp() {
        customerResponse = new CustomerResponseDTO();
        customerResponse.setId(1L);
        customerResponse.setFullName("Test User");
        customerResponse.setEmail("test@user.com");

        customerRequest = new CustomerRequestDTO();
        customerRequest.setFullName("Test User");
        customerRequest.setEmail("test@user.com");
        customerRequest.setPhone("123456");

        addressResponse = AddressResponseDTO.builder()
                .id(10L)
                .line1("C/ Falsa 123")
                .city("Springfield")
                .country("USA")
                .defaultAddress(true)
                .build();

        addressRequest = new AddressRequestDTO();
        addressRequest.setLine1("C/ Falsa 123");
        addressRequest.setCity("Springfield");
        addressRequest.setCountry("USA");
        addressRequest.setPostalCode("12345");
    }

    @Test
    void createCustomer_ShouldReturn201() throws Exception {
        // Given
        when(customerService.createCustomer(any(CustomerRequestDTO.class))).thenReturn(customerResponse);

        // When & Then
        mockMvc.perform(post("/api/customers")
                        .with(csrf()) // <-- CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getAllCustomers_ShouldReturnPage() throws Exception {
        // Given
        Page<CustomerResponseDTO> responsePage = new PageImpl<>(List.of(customerResponse));
        when(customerService.findAllCustomers(any(), any(Pageable.class))).thenReturn(responsePage);

        // When & Then
        mockMvc.perform(get("/api/customers")
                        .param("page", "0")
                        .param("size", "5")
                        .param("email", "test@user.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    void getCustomerById_ShouldReturn200_WhenFound() throws Exception {
        // Given
        when(customerService.getCustomerById(1L)).thenReturn(customerResponse);

        // When & Then
        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getCustomerById_ShouldReturn404_WhenNotFound() throws Exception {
        // Given
        when(customerService.getCustomerById(99L)).thenThrow(new EntityNotFoundException("Cliente no encontrado"));

        // When & Then
        mockMvc.perform(get("/api/customers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCustomer_ShouldReturn200() throws Exception {
        // Given
        when(customerService.updateCustomer(eq(1L), any(CustomerRequestDTO.class))).thenReturn(customerResponse);

        // When & Then
        mockMvc.perform(put("/api/customers/1")
                        .with(csrf()) // <-- CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deleteCustomerById_ShouldReturn204() throws Exception {
        // Given
        doNothing().when(customerService).deleteCustomerById(1L);

        // When & Then
        mockMvc.perform(delete("/api/customers/1")
                        .with(csrf())) // <-- CSRF
                .andExpect(status().isNoContent());
    }

    @Test
    void addAddressToCustomer_ShouldReturn201() throws Exception {
        // Given
        when(customerService.addAddressToCustomer(eq(1L), any(AddressRequestDTO.class))).thenReturn(addressResponse);

        // When & Then
        mockMvc.perform(post("/api/customers/1/addresses")
                        .with(csrf()) // <-- CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    void setDefaultAddress_ShouldReturn200() throws Exception {
        // Given
        when(customerService.markAddressAsDefault(1L, 10L)).thenReturn(addressResponse);

        // When & Then
        mockMvc.perform(put("/api/customers/1/addresses/10/default")
                        .with(csrf())) // <-- CSRF
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }
}