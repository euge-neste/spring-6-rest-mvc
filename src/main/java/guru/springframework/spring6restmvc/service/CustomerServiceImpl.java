package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.model.Customer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private Map<UUID, Customer> customers;

    public CustomerServiceImpl() {

        this.customers = new HashMap<>();

        Customer customer1 = Customer.builder()
                .id(UUID.randomUUID())
                .name("John")
                .version(1)
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        Customer customer2 = Customer.builder()
                .id(UUID.randomUUID())
                .name("Steve")
                .version(2)
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        customers.put(customer1.getId(), customer1);
        customers.put(customer2.getId(), customer2);

    }

    @Override
    public List<Customer> listCustomers() {
        return new ArrayList<>(customers.values());
    }

    @Override
    public Customer getCustomerById(UUID id) {
        return customers.get(id);
    }

    @Override
    public Customer saveNewCustomer(Customer customer) {

        Customer savedCustomer = Customer.builder()
                .id(UUID.randomUUID())
                .name(customer.getName())
                .version(customer.getVersion())
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        customers.put(savedCustomer.getId(), savedCustomer);

        return savedCustomer;
    }

    @Override
    public void updateCustomerById(UUID id, Customer customer) {
        Customer savedCustomer = customers.get(id);
        savedCustomer.setName(customer.getName());
        savedCustomer.setVersion(customer.getVersion());
        customers.put(savedCustomer.getId(), savedCustomer);
    }

    @Override
    public void deleteCustomerByID(UUID id) {
        customers.remove(id);
    }
}
