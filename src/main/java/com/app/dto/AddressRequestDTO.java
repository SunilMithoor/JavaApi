package com.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static com.app.util.MessageConstants.*;

@Getter
@Setter
public class AddressRequestDTO {

    @NotNull(message = USER_ID_REQUIRED)
    @JsonProperty("user_id")
    private Long userId;

    @NotBlank(message = ADDRESS_LINE1_REQUIRED)
    @Size(max = 255, message = ADDRESS_LINE1_MAX_LENGTH)
    @JsonProperty("address_line1")
    private String addressLine1;

    @Size(max = 255, message = ADDRESS_LINE2_MAX_LENGTH)
    @JsonProperty("address_line2")
    private String addressLine2;

    @Size(max = 255, message = STREET_MAX_LENGTH)
    @JsonProperty("street")
    private String street;

    @Size(max = 255, message = VILLAGE_MAX_LENGTH)
    @JsonProperty("village")
    private String village;

    @Size(max = 255, message = TALUQ_MAX_LENGTH)
    @JsonProperty("taluq")
    private String taluq;

    @NotBlank(message = DISTRICT_CODE_REQUIRED)
    @Size(max = 10, message = DISTRICT_CODE_MAX_LENGTH)
    @JsonProperty("district_code")
    private String districtCode;

    @NotBlank(message = CITY_CODE_REQUIRED)
    @Size(max = 10, message = CITY_CODE_MAX_LENGTH)
    @JsonProperty("city_code")
    private String cityCode;

    @NotBlank(message = STATE_CODE_REQUIRED)
    @Size(max = 10, message = STATE_CODE_MAX_LENGTH)
    @JsonProperty("state_code")
    private String stateCode;

    @NotBlank(message = COUNTRY_CODE_REQUIRED)
    @Size(max = 10, message = COUNTRY_CODE_MAX_LENGTH_10)
    @JsonProperty("country_code")
    private String countryCode;

    @NotBlank(message = POSTAL_CODE_REQUIRED)
    @Size(max = 20, message = POSTAL_CODE_MAX_LENGTH)
    @JsonProperty("postal_code")
    private String postalCode;

    @JsonProperty("latitude")
    private BigDecimal latitude;

    @JsonProperty("longitude")
    private BigDecimal longitude;

    @JsonProperty("location_address")
    private String locationAddress;

    @NotNull(message = ADDRESS_TYPE_REQUIRED)
    @Pattern(regexp = "BILLING|SHIPPING", message = ADDRESS_TYPE_INVALID)
    @JsonProperty("address_type")
    private String addressType = "SHIPPING";

    @JsonProperty("is_default")
    private Boolean isDefault = false;
}
