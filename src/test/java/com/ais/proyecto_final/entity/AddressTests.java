package com.ais.proyecto_final.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressTests {

    private final Customer mockCustomer = Customer.builder().id(1L).fullName("Mock").email("mock@test.com").build();


    @Test
    void lombokAllArgsConstructorWorks() {
        Address address = new Address(1L, mockCustomer, "Line 1", "Line 2", "City", "12345", "Country", true);

        assertEquals(1L, address.getId());
        assertEquals(mockCustomer, address.getCustomer());
        assertEquals("Line 1", address.getLine1());
        assertEquals("Line 2", address.getLine2());
        assertEquals("City", address.getCity());
        assertEquals("12345", address.getPostalCode());
        assertEquals("Country", address.getCountry());
        assertTrue(address.isDefaultAddress());
    }

    @Test
    void lombokNoArgsConstructorAndSettersWorks() {
        Address address = new Address();
        address.setId(2L);
        address.setCustomer(mockCustomer);
        address.setLine1("Line 1 Set");
        address.setLine2("Line 2 Set");
        address.setCity("City Set");
        address.setPostalCode("54321");
        address.setCountry("Country Set");
        address.setDefaultAddress(false);

        assertEquals(2L, address.getId());
        assertEquals(mockCustomer, address.getCustomer());
        assertEquals("Line 1 Set", address.getLine1());
        assertEquals("Line 2 Set", address.getLine2());
        assertEquals("City Set", address.getCity());
        assertEquals("54321", address.getPostalCode());
        assertEquals("Country Set", address.getCountry());
        assertFalse(address.isDefaultAddress());
    }

    @Test
    void lombokBuilderWorks() {
        Address address = Address.builder()
                .id(3L)
                .line1("Builder Line 1")
                .city("Builder City")
                .defaultAddress(true)
                .build();

        assertEquals(3L, address.getId());
        assertEquals("Builder Line 1", address.getLine1());
        assertTrue(address.isDefaultAddress());
    }

    @Test
    void lombokEqualsAndHashCodeWorks_BasedOnId() {
        Address address1 = Address.builder().id(1L).line1("A").build();
        Address address2 = Address.builder().id(1L).line1("B").build();
        Address address3 = Address.builder().id(2L).line1("A").build();

        assertEquals(address1, address2);
        assertEquals(address1.hashCode(), address2.hashCode());

        assertNotEquals(address1, address3);
        assertNotEquals(address1.hashCode(), address3.hashCode());

        assertTrue(address1.equals(address1));
        assertFalse(address1.equals(null));
        assertFalse(address1.equals(new Object()));
    }

    @Test
    void toStringWorks() {
        Address address = Address.builder().id(1L).line1("Test").build();
        assertNotNull(address.toString());
    }

    @Test
    void builderToString() {
        assertNotNull(Address.builder().toString());
    }
}