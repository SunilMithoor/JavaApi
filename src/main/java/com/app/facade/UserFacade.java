package com.app.facade;


import com.app.dto.UserRequestDTO;
import com.app.entity.User;
import com.app.exception.custom.InvalidParamException;
import com.app.exception.custom.UserAlreadyExistException;
import com.app.exception.custom.UserNotFoundException;
import com.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.app.util.MessageConstants.*;

@Service
@RequiredArgsConstructor
public class UserFacade {

    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;


    /**
     * Get user by id
     */
    public UserRequestDTO getUser(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidParamException(USER_ID_REQUIRED);
        }
        try {
            Long userId = Long.parseLong(id);
            User user = userService.getUserById(userId);

            if (user == null) {
                throw new UserNotFoundException();
            }
            return modelMapper.map(user, UserRequestDTO.class);
        } catch (NumberFormatException e) {
            throw new InvalidParamException(USER_ID_VALID_NUMBER);
        }
    }


    /**
     * Get all users
     *
     * @return List of UserModel (DTO)
     */
    public List<UserRequestDTO> getUsers() {
        List<User> users = userService.getAllUsers();
        if (users == null || users.isEmpty()) {
            throw new UserNotFoundException();
        }

        return users.stream()
                .map(user -> modelMapper.map(user, UserRequestDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Save user
     *
     * @param userRequestDTO as UserRequestDTO
     * @return UserRequestDTO (DTO)
     */

    public UserRequestDTO saveUser(UserRequestDTO userRequestDTO) {
        if (userRequestDTO == null) {
            throw new InvalidParamException("User data cannot be null");
        }

        if (userRequestDTO.getId() != null) {
            User existingUser = userService.getUserById(userRequestDTO.getId());
            if (existingUser != null) {
                throw new UserAlreadyExistException();
            }
        }

        if (userRequestDTO.getEmailId() == null || userRequestDTO.getEmailId().trim().isEmpty()) {
            throw new InvalidParamException(EMAIL_ID_VALID);
        }

        // Check if an email already exists
        if (userRequestDTO.getEmailId() != null) {
            boolean exists = userService.isEmailExists(userRequestDTO.getEmailId());
            if (exists) {
                throw new InvalidParamException(EMAIL_ID_ALREADY_REGISTERED);
            }
        }

        if (userRequestDTO.getMobileNo() == null || userRequestDTO.getMobileNo().trim().isEmpty()) {
            throw new InvalidParamException(MOBILE_NUMBER_REQUIRED);
        }

        // Check if an mobile no already exists
        if (userRequestDTO.getMobileNo() != null) {
            boolean exists = userService.isMobileExists(userRequestDTO.getMobileNo());
            if (exists) {
                throw new InvalidParamException(MOBILE_NUMBER_ALREADY_REGISTERED);
            }
        }

        User user = modelMapper.map(userRequestDTO, User.class);
        user = userService.saveUser(user);

        return modelMapper.map(user, UserRequestDTO.class);
    }
}

