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
import com.ais.proyecto_final.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService{

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AddressMapper addressMapper;
    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        log.info("Creando customer con email: {}", customerRequestDTO.getEmail());
        if (customerRepository.existsByEmail(customerRequestDTO.getEmail())) {
            log.warn("Fallo al crear customer. El email {} ya está en uso.", customerRequestDTO.getEmail());
            throw new DuplicateResourceException("El email ya está en uso.");
        }

        Customer customer = customerMapper.toEntity(customerRequestDTO);
        Customer saved = customerRepository.save(customer);
        log.info("Customer creado con ID: {}", saved.getId());
        return customerMapper.toResponseDto(saved);
    }

    @Transactional
    public Page<CustomerResponseDTO> findAllCustomers(String email, Pageable pageable) {
        log.info("Buscando clientes con filtro email: {}, page: {}", email, pageable.getPageNumber());
        Page<Customer> customersPage;

        if (email != null && !email.trim().isEmpty()) {
            customersPage = customerRepository.findByEmailContainingIgnoreCase(email, pageable);
        } else {
            customersPage = customerRepository.findAll(pageable);
        }
        log.info("Se encontraron {} clientes en page {}", customersPage.getNumberOfElements(), pageable.getPageNumber());
        return customersPage.map(customerMapper::toResponseDto);
    }

    @Transactional
    public CustomerResponseDTO getCustomerById(Long id) {
        log.info("Buscando cliente por ID: {}", id);
        return customerRepository.findById(id)
                .map(customer -> {
                    log.info("Encontrado cliente con id: {}", id);
                    return customerMapper.toResponseDto(customer);
                })
                .orElseThrow(() -> {
                    log.warn("No se ha encontrado a cliente con id: {}.", id);
                    return new EntityNotFoundException("Cliente " + id + " no existe.");
                });
    }

    // delete
    @Transactional
    public void deleteCustomerById(Long id) {
        log.info("Borrando cliente con ID: {}", id);
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            log.info("Se ha eliminado el cliente con id: {}.", id);
        } else {
            log.warn("Fallo al borrar, no se encuentra cliente con ID: {}.", id);
            throw new EntityNotFoundException("Cliente " + id + " no existe.");
        }
    }
    // put
    @Transactional
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO dto) {
        log.info("Actualizando cliente con id: {}", id);
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente " + id + " no existe."));
        log.warn("Fallo en update: no se encontró cliente con id: {}.", id);
        if (!existing.getEmail().equals(dto.getEmail()) &&
                customerRepository.existsByEmail(dto.getEmail())) {
            log.warn("Fallo al actualizar cliente: {}. El email: {} ya está en uso.", id, dto.getEmail());
            throw new DuplicateResourceException("El email introducido ya está en uso.");
        }


        customerMapper.updateEntityFromDto(dto, existing);
        Customer updated = customerRepository.save(existing);
        log.info("actualizado cliente con id: {}.", updated.getId());
        return customerMapper.toResponseDto(updated);
    }


    // DIRECCIONES

    @Transactional
    public AddressResponseDTO addAddressToCustomer(Long customerId, AddressRequestDTO dto) {
        log.info("Añadiendo dirección a cliente id: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.warn("Error al añadir dirección. Cliente con ID: {} no encontrado.", customerId);
                    return new EntityNotFoundException("Cliente " + customerId + " no encontrado.");
                });
        Address address = addressMapper.toEntity(dto);
        address.setCustomer(customer);

        if (dto.isDefaultAddress()) {
            log.info("Nueva dirección marcada como default. Desmarcando el resto para el cliente: {}", customerId);
            // desmarcar anteriores
            customer.getAddresses().forEach(a -> a.setDefaultAddress(false));
        }

        customer.getAddresses().add(address);

        customerRepository.save(customer);
        log.info("Dirección añadida con id: {} al cliente ID: {}", address.getId(), customerId);
        return addressMapper.toResponseDto(address);
    }

    @Transactional
    public AddressResponseDTO markAddressAsDefault(Long customerId, Long addressId) {
        log.info("Marcando dirección con id: {} por defecto para cliente con ID: {}", addressId, customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.warn("Fallo al marcar dirección por defecto, no se encuentra cliente con id {}", customerId);
                    return new EntityNotFoundException("Cliente " + customerId + " no encontrado.");
                });        // busco la dirección entre las direcciones del cliente
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
            log.warn("Fallo al marcar dirección con id {} por defecto, no se encuentra esa dirección en el cliente ID: {}", addressId, customerId);
            throw new EntityNotFoundException("Dirección con id " + addressId + " no encontrada para el cliente " + customerId + ".");
        }
        updated.setDefaultAddress(true);
        log.info("Marcada dirección id: {} por defecto para customer id: {}", addressId, customerId);
        return addressMapper.toResponseDto(updated);

    }





}
