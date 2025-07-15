package com.app.facade;

import com.app.config.LoggerService;
import com.app.dto.response.CustomerData;
import com.app.dto.response.CustomerPaginationData;
import com.app.entity.User;
import com.app.exception.custom.InvalidParamException;
import com.app.exception.custom.UserNotFoundException;
import com.app.service.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.app.util.MessageConstants.USER_ID_VALID_NUMBER;
import static com.app.util.Utils.tagMethodName;

@Service
public class CustomerFacade {

    private final CustomerServiceImpl customerService;
    private final LoggerService logger;
    private static final String TAG = "CustomerFacade";

    @Autowired
    public CustomerFacade(CustomerServiceImpl customerService, LoggerService logger) {
        this.customerService = customerService;
        this.logger = logger;
    }

    /**
     * Get all customers
     *
     * @return List of Customers (DTO)
     */
    public CustomerPaginationData getPaginatedCustomers(int page, int size) {
        String methodName = "getPaginatedCustomers";
        logger.request(tagMethodName(TAG, methodName), "Getting customers page");
        try {
            CustomerPaginationData response = customerService.getPaginatedCustomers(page, size);
            logger.response(tagMethodName(TAG, methodName), "Customers data: " + response);
            return response;
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Unable to fetch customer data", null);
            return null;
        }
    }


}


