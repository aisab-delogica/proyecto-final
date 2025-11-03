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
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService{

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AddressMapper addressMapper;
    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        if (customerRepository.existsByEmail(customerRequestDTO.getEmail())) {
            throw new DuplicateResourceException("El email ya está en uso.");
        }

        Customer customer = customerMapper.toEntity(customerRequestDTO);
        Customer saved = customerRepository.save(customer);
        return customerMapper.toResponseDto(saved);
    }

    @Transactional
    public Page<CustomerResponseDTO> findAllCustomers(String email, Pageable pageable) {
        Page<Customer> customersPage;

        if (email != null && !email.trim().isEmpty()) {
            customersPage = customerRepository.findByEmailContainingIgnoreCase(email, pageable);
        } else {
            customersPage = customerRepository.findAll(pageable);
        }

        return customersPage.map(customerMapper::toResponseDto);
    }

    @Transactional
    public CustomerResponseDTO getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(customerMapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Cliente " + id + " no existe."));
    }

    // delete
    @Transactional
    public void deleteCustomerById(Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Cliente " + id + " no existe.");
        }
    }

    // put
    @Transactional
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO dto) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente " + id + " no existe."));

        if (!existing.getEmail().equals(dto.getEmail()) &&
                customerRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("El email introducido ya está en uso.");
        }


        customerMapper.updateEntityFromDto(dto, existing);
        Customer updated = customerRepository.save(existing);

        return customerMapper.toResponseDto(updated);
    }


    // DIRECCIONES

    @Transactional
    public AddressResponseDTO addAddressToCustomer(Long customerId, AddressRequestDTO dto) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente " + customerId + " no encontrado."));

        Address address = addressMapper.toEntity(dto);
        address.setCustomer(customer);

        if (dto.isDefaultAddress()) {
            // desmarcar anteriores
            customer.getAddresses().forEach(a -> a.setDefaultAddress(false));
        }

        customer.getAddresses().add(address);

        customerRepository.save(customer);

        return addressMapper.toResponseDto(address);
    }

    @Transactional
    public AddressResponseDTO markAddressAsDefault(Long customerId, Long addressId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente " + customerId + " no encontrado."));
        // busco la dirección entre las direcciones del cliente
        // si no está -> lanzo excepción
        // si está ->  la marco como default y desmarco las demas
        Address updated = null;
        for (Address address : customer.getAddresses()) {
            if (address.getId().equals(addressId)) {
                updated = address;
            }
            address.setDefaultAddress(false);
        }
        if (updated == null) {
            throw new EntityNotFoundException("Dirección con id " + addressId + " no encontrada para el cliente " + customerId + ".");
        }
        updated.setDefaultAddress(true);
        return addressMapper.toResponseDto(updated);
    }





}
