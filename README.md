# Proyecto Final - API de Gestión de Tienda

Esta es una API REST desarrollada con Spring Boot para la gestión de un sistema de ventas de una tienda online. Permite administrar el ciclo de vida de Clientes, Productos y Pedidos, aplicando buenas prácticas de desarrollo, principios SOLID y una arquitectura por capas desacoplada.

Este proyecto ha sido desarrollado como una demostración de la aplicación de conceptos de arquitectura de software, patrones de diseño y desarrollo robusto en Java.

## Características Principales

* **Gestión de Productos:** CRUD completo para productos, incluyendo paginación y filtros por nombre y estado (activo/inactivo).
* **Gestión de Clientes:** CRUD para clientes y gestión de múltiples direcciones por cliente, con la capacidad de marcar una como predeterminada.
* **Gestión de Pedidos:** Creación de pedidos transaccional, con validación de stock en tiempo real y cálculo de totales.
* **Lógica de Negocio (Pedidos):**
    * Los pedidos descuentan el stock al crearse.
    * Los pedidos cancelados (desde estado `CREATED` o `PAID`) devuelven el stock al inventario.
    * Implementa una máquina de estados para transiciones válidas (ej. `CREATED` -> `PAID`, `PAID` -> `SHIPPED`).
* **Seguridad:** Autenticación basada en JWT (Bearer Token) para proteger los endpoints de modificación (POST, PUT, DELETE).
* **Documentación:** API documentada con OpenAPI 3 (Swagger).
* **Gestión de Errores:** Manejo centralizado de excepciones (`@ControllerAdvice`) con un contrato de error JSON uniforme.
* **Pruebas:** Alta cobertura de pruebas unitarias (servicios, mappers) y de integración (controllers, repositorios).

## Tech Stack

* **Framework:** Spring Boot 3.x
* **Lenguaje:** Java 17
* **Datos:** Spring Data JPA (Hibernate)
* **Base de Datos:** MySQL
* **Seguridad:** Spring Security (Autenticación JWT)
* **Mapeo:** MapStruct (para DTOs)
* **Validación:** Spring Validation (Jakarta Validation)
* **Testing:** JUnit 5, Mockito, Spring Boot Test (MockMvc)
* **Documentación:** SpringDoc (OpenAPI 3 / Swagger-UI)
* **Build:** Maven
* **Cobertura:** JaCoCo
* **Utilidades:** Lombok

---

## Documentación de la API (Swagger)
La API está auto-documentada usando OpenAPI. Una vez que la aplicación esté corriendo, puedes acceder a la UI de Swagger en:

http://localhost:8080/swagger-ui/index.html