package com.hackyeah.sl.backend.repository;

import com.hackyeah.sl.backend.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    void deleteByTicketId(String tickerId);

    List<Ticket> findAllByCategory(String category);


    @Query("SELECT t FROM Ticket t WHERE FUNCTION('earth_distance', FUNCTION('ll_to_earth', :latitude, :longitude), FUNCTION('ll_to_earth', t.latitude, t.longitude)) < :radius") // 1 km = 1000.0 meters
    List<Ticket> findTicketsWithinRange(@Param("latitude") Double latitude, @Param("longitude") Double longitude, @Param("radius") Long radius);
    List<Ticket> findByUserEmail(String email);

    Ticket findByTicketId(String ticketId);

}