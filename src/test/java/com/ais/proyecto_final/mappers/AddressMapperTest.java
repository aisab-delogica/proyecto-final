package com.ais.proyecto_final.mappers;

import com.ais.proyecto_final.dto.customer.AddressRequestDTO;
import com.ais.proyecto_final.dto.customer.AddressResponseDTO;
import com.ais.proyecto_final.entity.Address;
import com.ais.proyecto_final.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AddressMapperTest {

    @Autowired
    private AddressMapper addressMapper;

    @Test
    void shouldMapRequestDtoToEntity() {
        AddressRequestDTO dto = new AddressRequestDTO();
        dto.setLine1("C/ Falsa 123");
        dto.setCity("Springfield");
        dto.setPostalCode("12345");
        dto.setCountry("USA");
        dto.setDefaultAddress(true);

        Address entity = addressMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getLine1()).isEqualTo("C/ Falsa 123");
        assertThat(entity.getCity()).isEqualTo("Springfield");
        assertThat(entity.isDefaultAddress()).isTrue();
        assertThat(entity.getId()).isNull();
        assertThat(entity.getCustomer()).isNull();
    }

    @Test
    void shouldMapEntityToResponseDto() {
        Address entity = Address.builder()
                .id(10L)
                .line1("Av. Siempre Viva 742")
                .city("Springfield")
                .postalCode("12345")
                .country("USA")
                .defaultAddress(false)
                .build();

        AddressResponseDTO dto = addressMapper.toResponseDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getLine1()).isEqualTo("Av. Siempre Viva 742");
        assertThat(dto.isDefaultAddress()).isFalse();
    }

    @Test
    void shouldUpdateEntityFromDto() {
        Customer mockCustomer = Customer.builder().id(1L).build();
        Address existingEntity = Address.builder()
                .id(5L)
                .customer(mockCustomer)
                .line1("Old Line 1")
                .city("Old City")
                .defaultAddress(true)
                .build();

        AddressRequestDTO updateDto = new AddressRequestDTO();
        updateDto.setLine1("New Line 1");
        updateDto.setCity("New City");
        updateDto.setDefaultAddress(false);

        addressMapper.updateEntityFromDto(updateDto, existingEntity);

        assertThat(existingEntity.getLine1()).isEqualTo("New Line 1");
        assertThat(existingEntity.getCity()).isEqualTo("New City");
        assertThat(existingEntity.isDefaultAddress()).isFalse();
        assertThat(existingEntity.getId()).isEqualTo(5L);
        assertThat(existingEntity.getCustomer()).isEqualTo(mockCustomer);
    }
}