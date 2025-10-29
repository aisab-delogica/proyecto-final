package com.ais.proyecto_final.controller;

import com.ais.proyecto_final.dto.customer.AddressRequestDTO;
import com.ais.proyecto_final.dto.customer.AddressResponseDTO;
import com.ais.proyecto_final.dto.customer.CustomerRequestDTO;
import com.ais.proyecto_final.dto.customer.CustomerResponseDTO;
import com.ais.proyecto_final.service.CustomerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@AllArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@RequestBody CustomerRequestDTO customerRequest) {
        CustomerResponseDTO createdCustomer = customerService.createCustomer(customerRequest);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        List<CustomerResponseDTO> customers = customerService.findAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // put
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable Long id,
            @RequestBody @Valid CustomerRequestDTO dto) {
        CustomerResponseDTO updated = customerService.updateCustomer(id, dto);
        return ResponseEntity.ok(updated);
    }


    // delete
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> deleteCustomerById(@PathVariable Long id) {
        customerService.deleteCustomerById(id);
        return ResponseEntity.noContent().build();
    }

    // post customers/{id}/addresses
    @PostMapping("/{id}/addresses")
    public ResponseEntity<AddressResponseDTO> addAddressToCustomer(
            @PathVariable Long id,
            @RequestBody @Valid AddressRequestDTO dto) {
        AddressResponseDTO created = customerService.addAddressToCustomer(id, dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }


    //PUT /api/customers/{id}/addresses/{addressId}/default marca como predeterminada
    @PutMapping("/{id}/addresses/{addressId}/default")
    public ResponseEntity<AddressResponseDTO> setDefaultAddress(
            @PathVariable Long id,
            @PathVariable Long addressId) {
        AddressResponseDTO updated = customerService.markAddressAsDefault(id, addressId);
        return ResponseEntity.ok(updated);

    }
}
