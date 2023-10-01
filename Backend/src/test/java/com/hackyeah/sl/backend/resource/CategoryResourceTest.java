package com.hackyeah.sl.backend.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackyeah.sl.backend.domain.Category;
import com.hackyeah.sl.backend.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
class CategoryResourceTest {

    private static final String API_ROOT = "/category";

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    Category one, two;

    @BeforeEach
    void setUp() {

        categoryRepository.flush();

        one = Category.builder()
                .id(123123L)
                .name("One")
                .description("One")
                .build();

        two = Category.builder()
                .id(123121L)
                .name("Two")
                .description("Two")
                .build();

        categoryRepository.saveAll(Arrays.asList(one, two));
    }

    @Test
    @WithMockUser(
            username = "admin",
            roles = {"ADMIN"})
    void addCategory() throws Exception {
        mockMvc
                .perform(
                        post(API_ROOT + "/add")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(one)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is(one.getName())));
    }

    @Test
    void deleteUserNoAuthentication() throws Exception {
        mockMvc
                .perform(delete(API_ROOT + "/delete/" + one.getName()))
                .andExpect(status().isForbidden());
    }
}