package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerControllerIT {
    
    @Autowired
    CustomerController customerController;
    
    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerMapper customerMapper;

    @Test
    void testListCustomers() {

        List<CustomerDTO> customers = customerController.getCustomers();

        assertThat(customers.size()).isEqualTo(2);
    }

    @Test
    void testGetCustomerById() {
        Customer testCustomer = customerRepository.findAll().getFirst();

        CustomerDTO customerDTO = customerController.getCustomerById(testCustomer.getId());

        assertThat(customerDTO.getId()).isEqualTo(testCustomer.getId());
    }

    @Test
    void testCustomerIdNotFound() {
        assertThrows(NotFoundException.class, () -> {
            customerController.getCustomerById(UUID.randomUUID());
        });
    }

    @Rollback
    @Transactional
    @Test
    void testEmptyList() {

        customerRepository.deleteAll();
        List<CustomerDTO> customers = customerController.getCustomers();

        assertThat(customers.size()).isEqualTo(0);

    }

    @Rollback
    @Transactional
    @Test
    void testSaveCustomer() {

        CustomerDTO customerDTO = CustomerDTO.builder().name("Test").build();

        ResponseEntity responseEntity = customerController.handlePost(customerDTO);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(responseEntity.getHeaders().get("Location")).isNotNull();

        String[] location = responseEntity.getHeaders().getLocation().getPath().split("/");

        UUID id = UUID.fromString(location[4]);

        Customer testCustomer = customerRepository.findById(id).get();

        assertNotNull(testCustomer);

    }

    @Rollback
    @Transactional
    @Test
    void testUpdateCustomer() {
        Customer existingCustomer = customerRepository.findAll().getFirst();
        CustomerDTO customerDTO = customerMapper.customerToCustomerDTO(existingCustomer);

        customerDTO.setId(null);
        customerDTO.setVersion(null);

        final String updatedName = "Updated Name";

        customerDTO.setName(updatedName);

        ResponseEntity responseEntity = customerController.handlePut(customerDTO, existingCustomer.getId());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        Customer updatedCustomer = customerRepository.findById(existingCustomer.getId()).get();

        assertThat(updatedCustomer.getName()).isEqualTo(updatedName);

    }

    @Test
    void testUpdateCustomerNotFound() {
        assertThrows(NotFoundException.class, () -> {customerController
                .handlePut(CustomerDTO.builder().id(UUID.randomUUID()).build(), UUID.randomUUID());});
    }

    @Rollback
    @Transactional
    @Test
    void testDeleteCustomer() {
        Customer existingCustomer = customerRepository.findAll().getFirst();

        ResponseEntity responseEntity = customerController.handleDelete(existingCustomer.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        assertThat(customerRepository.findById(existingCustomer.getId()).isEmpty());
    }

    @Test
    void testDeleteCustomerNotFound() {
        assertThrows(NotFoundException.class, () -> {customerController.handleDelete(UUID.randomUUID());});
    }
}