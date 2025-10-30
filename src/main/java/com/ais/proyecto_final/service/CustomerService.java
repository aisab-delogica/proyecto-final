package com.ais.proyecto_final.service;


import com.ais.proyecto_final.dto.customer.AddressRequestDTO;
import com.ais.proyecto_final.dto.customer.AddressResponseDTO;
import com.ais.proyecto_final.dto.customer.CustomerRequestDTO;
import com.ais.proyecto_final.dto.customer.CustomerResponseDTO;

import java.util.List;
import java.util.Optional;


public interface CustomerService{


    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO);


    public List<CustomerResponseDTO> findAllCustomers();

    public CustomerResponseDTO getCustomerById(Long id);

    // delete

    public void deleteCustomerById(Long id);


    // put
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO dto);


    // DIRECCIONES

    public AddressResponseDTO addAddressToCustomer(Long customerId, AddressRequestDTO dto);


    public AddressResponseDTO markAddressAsDefault(Long customerId, Long addressId);



}
