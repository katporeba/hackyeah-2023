package com.hackyeah.sl.backend.service;

import com.hackyeah.sl.backend.domain.DTO.TicketDto;
import com.hackyeah.sl.backend.domain.Ticket;

import java.util.List;

public interface TicketService {
    List<Ticket> getTickets();

    List<Ticket> getTicketsByCategory(String category);

    void deleteTicker(String ticketId);

    Ticket addTicket(TicketDto ticket, String authorization);

    List<Ticket> getTicketsWithinOneKilometer(Double targetLatitude, Double targetLongitude);
}
