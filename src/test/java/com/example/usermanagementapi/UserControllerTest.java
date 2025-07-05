package com.example.usermanagementapi;

import com.example.usermanagementapi.model.User;
import com.example.usermanagementapi.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private List<User> manyUsers = new ArrayList<>();

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        objectMapper.registerModule(new JavaTimeModule());

        User initialUser1 = new User();
        initialUser1.setName("John Doe");
        initialUser1.setEmail("john.doe@example.com");

        User initialUser2 = new User();
        initialUser2.setName("Jane Smith");
        initialUser2.setEmail("jane.smith@example.com");

        List<User> savedUsers = userRepository.saveAll(List.of(initialUser1, initialUser2));
        this.user1 = savedUsers.get(0);
        this.user2 = savedUsers.get(1);

        manyUsers = IntStream.rangeClosed(1, 25)
                .mapToObj(i -> {
                    User user = new User();
                    user.setName("User " + i);
                    user.setEmail("user" + i + "@example.com");
                    return user;
                })
                .collect(Collectors.toList());
        userRepository.saveAll(manyUsers);
    }

    @Test
    void testGetUserByIdFound() throws Exception {
        mockMvc.perform(get("/api/users/" + user1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user1.getName())))
                .andExpect(jsonPath("$.email", is(user1.getEmail())));
    }

    @Test
    void testGetUserByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/users/999999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateUsers() throws Exception {
        User newUser1 = new User();
        newUser1.setName("Alice Brown");
        newUser1.setEmail("alice.brown@example.com");

        User newUser2 = new User();
        newUser2.setName("Bob Green");
        newUser2.setEmail("bob.green@example.com");

        List<User> newUsers = Arrays.asList(newUser1, newUser2);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUsers)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].name", is("Alice Brown")))
                .andExpect(jsonPath("$[0].email", is("alice.brown@example.com")))
                .andExpect(jsonPath("$[1].id", notNullValue()))
                .andExpect(jsonPath("$[1].name", is("Bob Green")))
                .andExpect(jsonPath("$[1].email", is("bob.green@example.com")));
    }

    @Test
    void testUpdateUser() throws Exception {
        User updatedUser = new User();
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("updated.email@example.com");

        mockMvc.perform(put("/api/users/" + user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user1.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.email", is("updated.email@example.com")));
    }

    @Test
    void testDeleteUserFound() throws Exception {

        mockMvc.perform(delete("/api/users/" + user1.getId()))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.existsById(user1.getId()));
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        Long nonExistentId = 999999L;

        mockMvc.perform(delete("/api/users/" + nonExistentId))
                .andExpect(status().isNotFound());
    }


}