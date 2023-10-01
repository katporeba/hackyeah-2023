package com.hackyeah.sl.backend.resource;

import com.hackyeah.sl.backend.domain.DTO.TicketDto;
import com.hackyeah.sl.backend.domain.DTO.TicketInRange;
import com.hackyeah.sl.backend.domain.Ticket;
import com.hackyeah.sl.backend.service.TicketService;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.hackyeah.sl.backend.constant.FileConstant.FORWARD_SLASH;
import static com.hackyeah.sl.backend.constant.FileConstant.USER_FOLDER;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(path = {"/ticket"})
public class TicketResource {

    private TicketService ticketService;

    @GetMapping("/list/{category}")
    public ResponseEntity<List<Ticket>> ticketListByCategory(@NotBlank @PathVariable String category) {
        List<Ticket> tickets = ticketService.getTicketsByCategory(category);
        return new ResponseEntity<>(tickets, OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN','ROLE_USER')")
    @PostMapping("/extendTime/{ticketId}")
    public ResponseEntity<Ticket> extendTimeBy1Hour(@NotBlank @PathVariable String ticketId) {

        Ticket ticket = ticketService.extendTicketTime(ticketId);
        return new ResponseEntity<>(ticket, OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN','ROLE_USER')")
    @DeleteMapping("/delete/{ticketId}")
    public ResponseEntity<Ticket> deleteTicketByTicketId(@NotBlank @PathVariable String ticketId) {

        ticketService.deleteTicket(ticketId);

        return new ResponseEntity<>(OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN','ROLE_USER')")
    @PutMapping("/update/{ticketId}")
    public ResponseEntity<Ticket> updateTicket(@NotBlank @PathVariable String ticketId, @RequestBody Ticket ticket) {

        Ticket newTicket = ticketService.updateTicket(ticketId, ticket);
        return new ResponseEntity<>(newTicket, OK);
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
    @PostMapping("/add/image")
    public ResponseEntity<Ticket> add(@RequestParam(name = "tickedId") String ticketId, @RequestParam(name = "file") MultipartFile file) throws IOException {

        Ticket ticket = ticketService.getTicketsByTicketId(ticketId);

        ticketService.saveTicketImage(ticket, file);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(path = "/image/{ticketId}/{filename}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(
            @NotBlank @PathVariable String ticketId,
            @NotBlank @PathVariable String filename)
            throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + ticketId + FORWARD_SLASH + filename));
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN','ROLE_USER')")
    @PostMapping("/list/nearby")
    public ResponseEntity<List<Ticket>> findTicketsInOneKilometer(@RequestBody TicketInRange ticket) {

        List<Ticket> tickets = ticketService.getTicketsWithinRange(ticket.getLatitude(), ticket.getLongitude(), ticket.getRadius());
        return new ResponseEntity<>(tickets, HttpStatus.CREATED);
    }


}
