package com.hackyeah.sl.backend.service;

import com.hackyeah.sl.backend.domain.DTO.TicketDto;
import com.hackyeah.sl.backend.domain.Ticket;
import com.hackyeah.sl.backend.domain.User;
import com.hackyeah.sl.backend.domain.mappers.TicketMapper;
import com.hackyeah.sl.backend.repository.TicketRepository;
import com.hackyeah.sl.backend.repository.UserRepository;
import com.hackyeah.sl.backend.utilty.JwtTokenProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        user.addTicket(newtTicket);
        newtTicket.setUser(user);

        return ticketRepository.save(newtTicket);
    }
}
