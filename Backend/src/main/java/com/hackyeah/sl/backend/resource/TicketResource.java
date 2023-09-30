package com.hackyeah.sl.backend.resource;

import com.hackyeah.sl.backend.domain.DTO.TicketDto;
import com.hackyeah.sl.backend.domain.DTO.TicketInOneKilometer;
import com.hackyeah.sl.backend.domain.Ticket;
import com.hackyeah.sl.backend.domain.mappers.TicketMapper;
import com.hackyeah.sl.backend.service.TicketService;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.OK;

@RestController
@AllArgsConstructor
@RequestMapping(path = {"/ticket"})
public class TicketResource {

    private TicketService ticketService;
    private final TicketMapper ticketMapper;

//    @GetMapping("/list")
//    public ResponseEntity<List<Ticket>> ticketList() {
//        return new ResponseEntity<>(tickets, OK);
//    }

    @GetMapping("/list/{category}")
    public ResponseEntity<List<Ticket>> ticketListByCategory(@NotBlank @PathVariable String category) {
        List<Ticket> tickets = ticketService.getTicketsByCategory(category);
        return new ResponseEntity<>(tickets, OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Ticket>> ticketByEmail(@RequestParam(name = "email", required = false) String email) {
        List<Ticket> tickets;
        if (email == null) {
            tickets = ticketService.getTickets();

        } else {
            tickets = ticketService.getTicketsByEmail(email);
        }
        return new ResponseEntity<>(tickets, OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN','ROLE_USER')")
    @PostMapping("/add")
    public ResponseEntity<Ticket> add(@RequestBody TicketDto ticket, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        Ticket newTicket = ticketService.addTicket(ticket, authorization);

        return new ResponseEntity<>(newTicket, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN','ROLE_USER')")
    @PostMapping("/list/nearby")
    public ResponseEntity<List<TicketDto>> findTicketsInOneKilometer(@RequestBody TicketInOneKilometer ticket) {

        List<Ticket> tickets = ticketService.getTicketsWithinOneKilometer(ticket.getTargetLatitude(), ticket.getTargetLongitude());

        List<TicketDto> ticketDtos = tickets.stream().map(ticketMapper::toDto).collect(Collectors.toList());

        return new ResponseEntity<>(ticketDtos, HttpStatus.CREATED);
    }


}
