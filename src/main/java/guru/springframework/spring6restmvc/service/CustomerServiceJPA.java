package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class CustomerServiceJPA implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;


    @Override
    public List<CustomerDTO> listCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::customerToCustomerDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CustomerDTO> getCustomerById(UUID id) {

        return Optional.ofNullable(customerMapper.customerToCustomerDTO(customerRepository.findById(id)
                .orElse(null)));
    }

    @Override
    public CustomerDTO saveNewCustomer(CustomerDTO customer) {

        return customerMapper.customerToCustomerDTO(customerRepository
                .save(customerMapper.customerDTOToCustomer(customer)));

    }

    @Override
    public Optional<CustomerDTO> updateCustomerById(UUID id, CustomerDTO customer) {

        AtomicReference<Optional<CustomerDTO>> atomicCustomerDTO = new AtomicReference<>();

        customerRepository.findById(id).ifPresentOrElse(foundCustomer -> {
            foundCustomer.setName(customer.getName());

            atomicCustomerDTO.set(Optional.of(customerMapper.
                    customerToCustomerDTO(customerRepository.save(foundCustomer))));


        }, () -> {
            atomicCustomerDTO.set(Optional.empty());
        });

        return atomicCustomerDTO.get();

    }

    @Override
    public Boolean deleteCustomerByID(UUID id) {

        if  (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }

        return false;
    }

    @Override
    public Optional<CustomerDTO> patchCustomerByID(UUID id, CustomerDTO customer) {
        AtomicReference<Optional<CustomerDTO>> atomicCustomerDTO = new AtomicReference<>();

        customerRepository.findById(id).ifPresentOrElse(foundCustomer -> {
            if(StringUtils.hasText(foundCustomer.getName())) {
                customer.setName(foundCustomer.getName());
            }

            atomicCustomerDTO.set(Optional.of(customerMapper
                    .customerToCustomerDTO(customerRepository.save(foundCustomer))));

        }, () -> atomicCustomerDTO.set(Optional.empty()));

        return atomicCustomerDTO.get();
    }
}
