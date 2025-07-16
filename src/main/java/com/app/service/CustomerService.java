package com.app.service;

import com.app.dto.response.CustomerPaginationData;


public interface CustomerService {
    CustomerPaginationData getPaginatedCustomers(int page, int size);
}
