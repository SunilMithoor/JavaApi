package com.app.controller;

import com.app.config.LoggerService;
import com.app.dto.response.CustomerData;
import com.app.dto.response.CustomerPaginationData;
import com.app.exception.custom.InvalidParamException;
import com.app.facade.CustomerFacade;
import com.app.model.common.ResponseHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static com.app.util.Utils.tagMethodName;


@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "/api/v1/customers", description = "Customer APIs")
@Validated
@SecurityRequirement(name = "Authorization")
public class CustomerController {

    private final CustomerFacade facade;
    private final LoggerService logger;
    private static final String TAG = "UserController";


    @Autowired
    public CustomerController(CustomerFacade facade, LoggerService logger) {
        this.facade = facade;
        this.logger = logger;
    }


    @GetMapping
    public ResponseEntity<?> getCustomers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        String methodName = "getCustomers";
        try {
            CustomerPaginationData data = facade.getPaginatedCustomers(page, size);

            Map<String, Object> response = new HashMap<>();
            Map<String, Object> inner = new HashMap<>();
            inner.put("data", data.getData());
            inner.put("meta", data.getMeta());
            response.put("response", inner);

            return ResponseHandler.success(HttpStatus.OK, response, "Customers list");
        } catch (InvalidParamException e) {
            return ResponseHandler.failure(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Unexpected error occurred while fetching customers", e);
            return ResponseHandler.failure(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }
}