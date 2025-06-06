package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import com.appsdeveloperblog.tutorials.junit.service.UsersService;
import com.appsdeveloperblog.tutorials.junit.shared.UserDto;
import com.appsdeveloperblog.tutorials.junit.ui.request.UserDetailsRequestModel;
import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.h2.engine.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UsersController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class UsersControllerWebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UsersService usersService;

    @Test
    @DisplayName("User can be created")
    void testCreateUser_whenValidUserDetailsProvided_returnsCreatedUserDetails() throws Exception {
        //Arrange
        UserDetailsRequestModel userDetailsRequestModel = new UserDetailsRequestModel();
        userDetailsRequestModel.setFirstName("Sergey");
        userDetailsRequestModel.setLastName("Kargopolov");
        userDetailsRequestModel.setEmail("test@test.com");
        userDetailsRequestModel.setPassword("12345678");
        userDetailsRequestModel.setRepeatPassword("12345678");

      /*  UserDto userDto = new UserDto();
        userDto.setFirstName("Sergey");
        userDto.setLastName("Kargopolov");
        userDto.setEmail("test@test.com");
        userDto.setPassword("12345678");
        userDto.setUserId(UUID.randomUUID().toString());
      */

        UserDto userDto = new ModelMapper().map(userDetailsRequestModel, UserDto.class);
        userDto.setUserId(UUID.randomUUID().toString());
        when(usersService.createUser(any(UserDto.class))).thenReturn(userDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));
        //Act

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        UserRest createUser = new ObjectMapper().readValue(responseBodyAsString, UserRest.class);
        //Assert

        assertEquals(userDetailsRequestModel.getFirstName(), createUser.getFirstName(), "The returned user first name is most likely incorrect");
        assertEquals(userDetailsRequestModel.getLastName(), createUser.getLastName(), "The returned user last name incorrect");
        assertEquals(userDetailsRequestModel.getEmail(), createUser.getEmail(), "The returned user email is incorrect");
        assertFalse(createUser.getUserId().isEmpty(), "userId should not be empty");

    }

    @Test
    @DisplayName("First name is not empty")
    void testCreateUser_whenFirstNameIsNotProvided_returns400StatusCode() throws Exception {
        //Arrange
        UserDetailsRequestModel userDetailsRequestModel = new UserDetailsRequestModel();
        userDetailsRequestModel.setFirstName("");
        userDetailsRequestModel.setLastName("Kargopolov");
        userDetailsRequestModel.setEmail("test@test.com");
        userDetailsRequestModel.setPassword("12345678");
        userDetailsRequestModel.setRepeatPassword("12345678");
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));
        //Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();


        //Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus(), "Incorrect HTTP Status Code returned");

    }


    @Test
    @DisplayName("First name should be less than 2 characters")
    void testCreateUser_whenFirstNameIsLessThan2Characters_returns400StatusCode() throws Exception {
        //Arrange
        UserDetailsRequestModel userDetailsRequestModel = new UserDetailsRequestModel();
        userDetailsRequestModel.setFirstName("S");
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));
        //Act

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        //Assert

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus(), "HTTP Status code is not set to 400");
    }

}

