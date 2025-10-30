package com.ais.proyecto_final.service.customer;


import com.ais.proyecto_final.dto.customer.AddressRequestDTO;
import com.ais.proyecto_final.dto.customer.AddressResponseDTO;
import com.ais.proyecto_final.dto.customer.CustomerRequestDTO;
import com.ais.proyecto_final.dto.customer.CustomerResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CustomerService{


    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO);


    Page<CustomerResponseDTO> findAllCustomers(String email, Pageable pageable);
    public CustomerResponseDTO getCustomerById(Long id);

    // delete

    public void deleteCustomerById(Long id);


    // put
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO dto);


    // DIRECCIONES

    public AddressResponseDTO addAddressToCustomer(Long customerId, AddressRequestDTO dto);


    public AddressResponseDTO markAddressAsDefault(Long customerId, Long addressId);

}
