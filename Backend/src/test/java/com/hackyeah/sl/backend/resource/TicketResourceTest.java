package com.hackyeah.sl.backend.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackyeah.sl.backend.domain.Category;
import com.hackyeah.sl.backend.domain.Ticket;
import com.hackyeah.sl.backend.repository.CategoryRepository;
import com.hackyeah.sl.backend.repository.TicketRepository;
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
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
class TicketResourceTest {

    private static final String API_ROOT = "/ticket";

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;


    Ticket one, two;

    @BeforeEach
    void setUp() {

        ticketRepository.flush();

        one = Ticket.builder()
                .ticketId("ajshdaui1")
                .category("Pies")
                .aggressive(true)
                .count(3)
                .comment("agressiva as f..")
                .latitude(123123.0)
                .longitude(123123.0)
                .build();

        two = Ticket.builder()
                .ticketId("ajshdaui1")
                .category("Pies")
                .aggressive(true)
                .count(3)
                .comment("agressiva as f..")
                .latitude(123123.0)
                .longitude(123123.0)
                .build();

        ticketRepository.saveAll(Arrays.asList(one, two));
    }

    @Test
    void ticketList() throws Exception {
        mockMvc
                .perform(get(API_ROOT + "/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].ticketId", Matchers.is(one.getTicketId())));
    }

    @Test
    void deleteCategory() {
    }

    @Test
    void editCategory() {
    }
}