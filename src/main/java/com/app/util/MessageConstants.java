package com.app.util;

public class MessageConstants {

    private MessageConstants() {
    }

    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_FETCHED_SUCCESS = "User data fetched successfully";
    public static final String INTERNAL_SERVER_ERROR = "An internal server error occurred";
    public static final String USER_ID_REQUIRED = "User ID is required";
    public static final String USER_DATA_NULL = "User data cannot be null";
    public static final String USER_EMAIL_ID_REQUIRED = "User email id is required";
    public static final String USER_ID_VALID_NUMBER = "User ID must be a valid number";
    public static final String USER_SAVED_SUCCESS = "User saved successfully";
    public static final String INTERNAL_ERROR = "An unexpected error occurred";
    public static final String INVALID_USER_ID = "Invalid user ID provided";
    public static final String USER_FETCH_SUCCESS = "User data fetched successfully";
    public static final String NO_USERS_FOUND = "No users found";
    public static final String USERS_FETCH_SUCCESS = "Users retrieved successfully";
    public static final String USERS_ALREADY_EXIST = "User already exists";
    public static final String EMAIL_ID_VALID = "Email ID cannot be null or empty";
    public static final String EMAIL_ID_ALREADY_REGISTERED = "Email ID is already registered";
    public static final String PASSWORD_NULL = "Password cannot be null or empty";

    public static final String ADDRESS_LINE1_REQUIRED = "Address Line 1 is required";
    public static final String ADDRESS_LINE1_MAX_LENGTH = "Address Line 1 cannot exceed 255 characters";
    public static final String ADDRESS_LINE2_MAX_LENGTH = "Address Line 2 cannot exceed 255 characters";
    public static final String STREET_MAX_LENGTH = "Street cannot exceed 255 characters";
    public static final String VILLAGE_MAX_LENGTH = "Village cannot exceed 255 characters";
    public static final String TALUQ_MAX_LENGTH = "Taluq cannot exceed 255 characters";
    public static final String DISTRICT_CODE_REQUIRED = "District code is required";
    public static final String DISTRICT_CODE_MAX_LENGTH = "District code cannot exceed 10 characters";
    public static final String CITY_CODE_REQUIRED = "City code is required";
    public static final String CITY_CODE_MAX_LENGTH = "City code cannot exceed 10 characters";
    public static final String STATE_CODE_REQUIRED = "State code is required";
    public static final String STATE_CODE_MAX_LENGTH = "State code cannot exceed 10 characters";
    public static final String COUNTRY_CODE_REQUIRED = "Country code is required";
    public static final String COUNTRY_CODE_MAX_LENGTH_10 = "Country code cannot exceed 10 characters";
    public static final String POSTAL_CODE_REQUIRED = "Postal code is required";
    public static final String POSTAL_CODE_MAX_LENGTH = "Postal code cannot exceed 20 characters";
    public static final String ADDRESS_TYPE_REQUIRED = "Address type is required";
    public static final String ADDRESS_TYPE_INVALID = "Address type must be either BILLING or SHIPPING";


    public static final String FIRST_NAME_REQUIRED = "First name is required";
    public static final String FIRST_NAME_LENGTH = "First Name must be between 3 and 50 characters";
    public static final String USER_NAME_LENGTH = "User Name must be between 3 and 50 characters";
    public static final String LAST_NAME_MAX_LENGTH = "Last name cannot exceed 50 characters";
    public static final String EMAIL_INVALID_FORMAT = "Invalid email format";
    public static final String EMAIL_MAX_LENGTH = "Email cannot exceed 50 characters";
    public static final String COUNTRY_CODE_MAX_LENGTH_5 = "Country code cannot exceed 5 characters";
    public static final String MOBILE_NUMBER_REQUIRED = "Mobile number is required";
    public static final String MOBILE_NUMBER_ALREADY_REGISTERED = "Mobile No is already registered";
    public static final String MOBILE_NUMBER_MAX_LENGTH = "Mobile number cannot exceed 15 characters";
    public static final String MOBILE_NUMBER_INVALID_FORMAT = "Invalid mobile number format";
    public static final String PASSWORD_HASH_MAX_LENGTH = "Password hash cannot exceed 255 characters";
    public static final String PASSWORD_MAX_LENGTH = "Password cannot exceed 50 characters";
    public static final String PASSWORD_REGEX = "Password must contain at least one letter, one number, and one special character.";
    public static final String OTP_LENGTH = "Otp length should be 6 characters";
    public static final String DATE_OF_BIRTH_INVALID = "Date of birth must be in YYYY-MM-DD format";
    public static final String ROLE_REQUIRED = "Role is required";
    public static final String ROLE_INVALID = "Role must be either USER, SUPER_ADMIN, ADMIN or SELLER";

    public static final String DATABASE_CONNECTION_ERROR = "Failed to connect to the database";

}
