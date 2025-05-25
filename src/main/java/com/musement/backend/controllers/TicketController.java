package com.musement.backend.controllers;

import com.musement.backend.dto.TicketDto;
import com.musement.backend.services.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @GetMapping
    public List<TicketDto> getMyTickets(Principal principal) {
        return ticketService.getMyTickets(principal.getName());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TicketDto uploadTicket(
            @RequestParam Long concertId,
            @RequestPart("file") MultipartFile file,
            Principal principal
    ) {
        return ticketService.uploadTicket(principal.getName(), concertId, file);
    }

    @DeleteMapping("/{id}")
    public void deleteTicket(
            @PathVariable Long id,
            Principal principal
    ) {
        ticketService.deleteTicket(principal.getName(), id);
    }

    // для замены можно либо вызывать delete+upload, либо реализовать метод replaceTicket
    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TicketDto replaceTicket(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file,
            Principal principal
    ) {
        return ticketService.replaceTicket(principal.getName(), id, file);
    }
}