package com.hackyeah.sl.backend.repository;

import com.hackyeah.sl.backend.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    void deleteByTicketId(String tickerId);

    List<Ticket> findAllByCategory(String category);

    @Query("SELECT t FROM Ticket t " +
                  "WHERE (6371 * 2 * ASIN(SQRT(POWER(SIN(RADIANS(:latitude - t.latitude) / 2), 2) + " +
                  "COS(RADIANS(:latitude)) * COS(RADIANS(t.latitude)) * " +
                  "POWER(SIN(RADIANS(:longitude - t.longitude) / 2), 2)))) <= 1.0")
    List<Ticket> findTicketsInOneKilometerRange(@Param("latitude") Double latitude, @Param("longitude") Double longitude);

}