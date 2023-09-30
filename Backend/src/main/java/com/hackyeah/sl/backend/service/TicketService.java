package com.hackyeah.sl.backend.service;

import com.hackyeah.sl.backend.domain.DTO.TicketDto;
import com.hackyeah.sl.backend.domain.Ticket;
import com.hackyeah.sl.backend.exception.domain.NotAnImageFileException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TicketService {
    List<Ticket> getTickets();

    List<Ticket> getTicketsByCategory(String category);

    void deleteTicket(String ticketId);

    Ticket addTicket(TicketDto ticket, String authorization);

    List<Ticket> getTicketsWithinRange(Double targetLatitude, Double targetLongitude, Long radius);

    List<Ticket> getTicketsByEmail(String email);

    Ticket extendTicketTime(String ticketId);

    Ticket getTicketsByTicketId(String ticketId);

    void saveTicketImage(Ticket ticket, MultipartFile image) throws IOException, NotAnImageFileException;

    Ticket updateTicket(String ticketId, Ticket newTicket);


}
