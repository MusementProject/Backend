package com.musement.backend.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.musement.backend.dto.TicketDto;
import com.musement.backend.exceptions.TicketNotFoundException;
import com.musement.backend.models.Concert;
import com.musement.backend.models.Ticket;
import com.musement.backend.models.User;
import com.musement.backend.repositories.ConcertRepository;
import com.musement.backend.repositories.TicketRepository;
import com.musement.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepo;
    private final UserRepository userRepo;
    private final ConcertRepository concertRepo;
    private final Cloudinary cloudinary;

    public List<TicketDto> getMyTickets(String username) {
        User user = userRepo.findUserByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "User not found"));
        return ticketRepo.findAllByUserId(user.getId())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TicketDto uploadTicket(String username, Long concertId, MultipartFile file) {
        User user = userRepo.findUserByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "User not found"));
        Concert concert = concertRepo.findById(concertId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Concert not found"));

        String format = file.getContentType().equals(MediaType.APPLICATION_PDF_VALUE) ? "pdf" : "jpg";
        Map<?, ?> result;
        try {
            result = cloudinary.uploader()
                    .upload(file.getBytes(),
                            ObjectUtils.asMap(
                                    "resource_type", format.equals("pdf") ? "raw" : "image",
                                    "folder", "tickets",
                                    "format", format
                            ));
        } catch (IOException e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Cloudinary upload failed", e);
        }

        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setConcert(concert);
        ticket.setPublicId((String) result.get("public_id"));
        ticket.setFileUrl((String) result.get("secure_url"));
        ticket.setFileFormat(format);
        ticket.setUploadedAt(Instant.now());

        return toDto(ticketRepo.save(ticket));
    }

    @Transactional
    public void deleteTicket(String username, Long ticketId) {
        User user = userRepo.findUserByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "User not found"));
        Ticket ticket = ticketRepo.findById(ticketId)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        try {
            cloudinary.uploader().destroy(ticket.getPublicId(), ObjectUtils.asMap("resource_type", "raw"));
        } catch (Exception ignored) {
        }

        ticketRepo.delete(ticket);
    }

    @Transactional
    public TicketDto replaceTicket(String username, Long ticketId, MultipartFile file) {
        // удаляем старый + загружаем новый
        deleteTicket(username, ticketId);
        // а затем делаем uploadTicket, но сохраним связь на тот же концерт
        // можно оптимизировать, но для простоты:
        // получаем концертId из удалённого
        // ... либо разбить логику на два шага; здесь для примера просто заново
        throw new UnsupportedOperationException("Use uploadTicket() with new file");
    }

    private TicketDto toDto(Ticket t) {
        return new TicketDto(
                t.getId(),
                t.getConcert().getId(),
                t.getConcert().getArtist().getName(),
                t.getConcert().getLocation(),
                t.getConcert().getDate().toInstant(java.time.ZoneOffset.UTC),
                t.getFileUrl(),
                t.getFileFormat(),
                t.getUploadedAt()
        );
    }
}