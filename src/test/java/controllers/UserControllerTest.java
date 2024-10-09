package controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import root.StartApplication;
import root.entities.User;
import root.repositories.UserRepo;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = StartApplication.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    /* Functions to test:
    * createUser
    * loginAndGetToken
    * getAllUsers
    * getUserById
    * updateUser
    * updatePassword
    * deleteUser
    * initialisePhase */

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepo userRepo;

    private final String BASE_URL = "/users";
    private final User TEST_USER = new User("Test User", "password");

    @Test
    public void createUserTest() throws Exception {
        String userJson = "{\"username\": \"New User\", \"password\": \"password\"}";
        when(userRepo.findByUsername(TEST_USER.getUsername())).thenReturn(Optional.empty());
        mockMvc.perform(post(BASE_URL + "/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(userJson))
                .andExpect(status().isOk());
    }

    @Test
    public void createUserDuplicateUsername() throws Exception {
        String userJson = "{\"username\": \"Test User\", \"password\": \"password\"}";
        when(userRepo.findByUsername(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));

        mockMvc.perform(post(BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isConflict());
    }

    @Test
    public void createUserInvalidUser() throws Exception {
        String userJson = "{\"username\": \"Test User\", \"password\": }";

        mockMvc.perform(post(BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest());

    }
}
