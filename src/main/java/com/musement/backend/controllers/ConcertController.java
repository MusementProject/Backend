package com.musement.backend.controllers;

import com.musement.backend.dto.ConcertUpdateDTO;
import com.musement.backend.models.Concert;
import com.musement.backend.models.User;
import com.musement.backend.services.ConcertService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

// TODO: write tests for this

@RestController
@RequestMapping("/api/concerts")
public class ConcertController {
    private final ConcertService concertService;

    public ConcertController(ConcertService concertService) {
        this.concertService = concertService;
    }

    @GetMapping
    public List<Concert> getAllConcerts() {
        return concertService.getAllConcerts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Concert> getConcertById(@PathVariable Long id) {
        return ResponseEntity.ok(concertService.getConcertById(id));
    }

    @GetMapping("/{id}")
    public Set<User> getAttendees(@PathVariable Long id) {
        return concertService.getAttendees(id);
    }

    @GetMapping("/{concertId}/attend_user/{userId}")
    public boolean isUserAttendingConcert(@PathVariable Long concertId, @PathVariable Long userId) {
        return concertService.isUserAttendingConcert(concertId, userId);
    }

    @GetMapping("/attend_user/{userId}")
    public List<Concert> getConcertsByAttendee(@PathVariable Long userId) {
        return concertService.getConcertsByAttendee(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Concert>> searchConcerts(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        List<Concert> concerts = concertService.searchConcerts(location, fromDate, toDate);
        return ResponseEntity.ok(concerts);
    }

    @PostMapping
    public ResponseEntity<Concert> createConcert(@RequestBody Concert concert) {
        return ResponseEntity.ok(concertService.createConcert(concert));
    }

    @PutMapping("/{concertId}/attend_user/{userId}")
    public ResponseEntity<Concert> addAttendeeToConcert(@PathVariable Long concertId, @PathVariable Long userId) {
        return ResponseEntity.ok(concertService.addAttendeeToConcert(concertId, userId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Concert> updateConcert(@PathVariable Long id, @RequestBody ConcertUpdateDTO dto) {
        return ResponseEntity.ok(concertService.updateConcert(id, dto));
    }

    @DeleteMapping("/{concertId}/attend_user/{userId}")
    public ResponseEntity<Concert> removeAttendeeFromConcert(@PathVariable Long concertId, @PathVariable Long userId) {
        return ResponseEntity.ok(concertService.removeAttendeeFromConcert(concertId, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConcertById(@PathVariable Long id) {
        concertService.deleteConcert(id);
        return ResponseEntity.noContent().build();
    }
}
