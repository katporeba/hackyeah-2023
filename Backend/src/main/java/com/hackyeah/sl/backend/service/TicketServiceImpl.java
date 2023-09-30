package com.hackyeah.sl.backend.service;

import com.hackyeah.sl.backend.domain.DTO.TicketDto;
import com.hackyeah.sl.backend.domain.Ticket;
import com.hackyeah.sl.backend.domain.User;
import com.hackyeah.sl.backend.domain.mappers.TicketMapper;
import com.hackyeah.sl.backend.repository.TicketRepository;
import com.hackyeah.sl.backend.repository.UserRepository;
import com.hackyeah.sl.backend.utilty.JwtTokenProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.hackyeah.sl.backend.constant.SecurityConstant.TOKEN_PREFIX;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class TicketServiceImpl implements TicketService {

    private TicketRepository ticketRepository;
    private UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private TicketMapper ticketMapper;
    private final EntityManager entityManager;


    @Override
    public List<Ticket> getTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public List<Ticket> getTicketsByCategory(String category) {
        return ticketRepository.findAllByCategory(category);
    }

    @Override
    public void deleteTicker(String ticketId) {
        ticketRepository.deleteByTicketId(ticketId);

    }

    @Override
    public Ticket addTicket(TicketDto ticketDTO, String authorization) {

        String token = authorization.substring(TOKEN_PREFIX.length());

        String email = jwtTokenProvider.getSubject(token);

        User user = userRepository.findUserByEmail(email);

        Ticket newtTicket = ticketMapper.toEntity(ticketDTO);

        generateRandomTicketId(newtTicket);
        extendExpirationLocalDateTime(newtTicket);

        user.addTicket(newtTicket);
        newtTicket.setUser(user);

        return ticketRepository.save(newtTicket);
    }

    private void generateRandomTicketId(Ticket newtTicket) {
        newtTicket.setTicketId(RandomStringUtils.randomAlphanumeric(15));
    }

    private void extendExpirationLocalDateTime(Ticket newtTicket) {
        newtTicket.setExpirationDate(LocalDateTime.now().plusHours(1));
    }

    public List<Ticket> getTicketsWithinOneKilometer(Double targetLatitude, Double targetLongitude) {
        double radius = 1.0; // 1 kilometer
        String hql = "SELECT t FROM Ticket t WHERE " +
                "6371 * 2 * ASIN(SQRT(POWER(SIN((:targetLatitude - t.latitude) * PI() / 180 / 2), 2) + " +
                "COS(:targetLatitude * PI() / 180) * COS(t.latitude * PI() / 180) * " +
                "POWER(SIN((:targetLongitude - t.longitude) * PI() / 180 / 2), 2))) <= :radius";

        Query query = entityManager.createQuery(hql);
        query.setParameter("targetLatitude", targetLatitude);
        query.setParameter("targetLongitude", targetLongitude);
        query.setParameter("radius", radius);

        return query.getResultList();
    }

    @Override
    public List<Ticket> getTicketsByEmail(String email) {
        return ticketRepository.findByUserEmail(email);
    }

    @Override
    public Ticket extendTicketTime(String ticketId) {
        Ticket ticket = ticketRepository.findByTicketId(ticketId);
        extendExpirationLocalDateTime(ticket);
        return ticket;
    }

}
