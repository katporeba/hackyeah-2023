package com.hackyeah.sl.backend.repository;

import com.hackyeah.sl.backend.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    void deleteByTicketId(String tickerId);

    List<Ticket> findAllByCategory(String category);

}