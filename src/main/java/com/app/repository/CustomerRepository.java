package com.app.repository;


import com.app.entity.Customers;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CustomerRepository extends PagingAndSortingRepository<Customers, Long> {

}
