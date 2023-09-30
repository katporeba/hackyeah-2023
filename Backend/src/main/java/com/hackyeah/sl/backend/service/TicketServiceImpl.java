package com.hackyeah.sl.backend.service;

import com.hackyeah.sl.backend.domain.DTO.TicketDto;
import com.hackyeah.sl.backend.domain.Ticket;
import com.hackyeah.sl.backend.domain.User;
import com.hackyeah.sl.backend.domain.mappers.TicketMapper;
import com.hackyeah.sl.backend.exception.domain.NotAnImageFileException;
import com.hackyeah.sl.backend.repository.TicketRepository;
import com.hackyeah.sl.backend.repository.UserRepository;
import com.hackyeah.sl.backend.utilty.JwtTokenProvider;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.hackyeah.sl.backend.constant.FileConstant.*;
import static com.hackyeah.sl.backend.constant.SecurityConstant.TOKEN_PREFIX;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.http.MediaType.*;

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

    public List<Ticket> getTicketsWithinRange(Double targetLatitude, Double targetLongitude, Long radius) {
        return ticketRepository.findTicketsWithinRange(targetLatitude, targetLongitude, radius);
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

    @Override
    public Ticket getTicketsByTicketId(String ticketId) {
        return ticketRepository.findByTicketId(ticketId);
    }

    public void saveTicketImage(Ticket ticket, MultipartFile image)
            throws IOException, NotAnImageFileException {
        if (image != null) {
            if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE)
                    .contains(image.getContentType())) {
                throw new NotAnImageFileException(image.getOriginalFilename() + " is not an image.");
            }
            Path userFolder = Paths.get(USER_FOLDER + ticket.getTicketId()).toAbsolutePath().normalize();
            if (!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                log.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(USER_FOLDER + ticket.getTicketId() + DOT + JPG_EXTENSION));
            Files.copy(
                    image.getInputStream(),
                    userFolder.resolve(ticket.getTicketId() + DOT + JPG_EXTENSION),
                    REPLACE_EXISTING);

            ticket.setTicketImageUrl(setTicketImage(ticket.getTicketId()));

            ticketRepository.save(ticket);

            log.info(FILE_SAVED_IN_FILE_SYSTEM + image.getOriginalFilename());
        }
    }

    private String setTicketImage(String ticketId) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(USER_IMAGE_PATH + ticketId + FORWARD_SLASH + ticketId + DOT + JPG_EXTENSION)
                .toUriString();
    }
}
