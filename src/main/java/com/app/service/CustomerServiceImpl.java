package com.app.service;

import com.app.config.LoggerService;
import com.app.dto.response.CustomerData;
import com.app.dto.response.CustomerPaginationData;
import com.app.dto.response.MetaData;
import com.app.entity.Customers;
import com.app.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.app.util.Utils.tagMethodName;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final LoggerService logger;
    private static final String TAG = "CustomerServiceImpl";


    @Autowired
    public CustomerServiceImpl(CustomerRepository repository, LoggerService logger) {
        this.repository = repository;
        this.logger = logger;
    }

    @Override
    public CustomerPaginationData getPaginatedCustomers(int page, int size) {
        String methodName = "getPaginatedCustomers";
        try {
            logger.request(tagMethodName(TAG, methodName), "Page Request : " + page + "," + size);
            PageRequest pageRequest = PageRequest.of(page - 1, size);
            Page<Customers> customersPage = repository.findAll(pageRequest);

            List<CustomerData> customerDataList = customersPage.getContent()
                    .stream()
                    .map(this::toCustomerData)
                    .toList();

            MetaData meta = new MetaData();
            meta.setPage(page);
            meta.setTake(size);
            meta.setItemCount((int) customersPage.getTotalElements());
            meta.setPageCount(customersPage.getTotalPages());
            meta.setHasPreviousPage(customersPage.hasPrevious());
            meta.setHasNextPage(customersPage.hasNext());

            return new CustomerPaginationData(customerDataList, meta);
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Unable to get customer data ", e);
            return null;
        }
    }


    private CustomerData toCustomerData(Customers customer) {
        CustomerData data = new CustomerData();
        data.setId(customer.getId());
        data.setCustomerId(customer.getCustomerId());
        data.setFirstName(customer.getFirstName());
        data.setLastName(customer.getLastName());
        return data;
    }
}


