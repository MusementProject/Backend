package com.musement.backend.controllers;

import com.musement.backend.models.Concert;
import com.musement.backend.services.ConcertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping
    public ResponseEntity<Concert> createConcert(@RequestBody Concert concert) {
        return ResponseEntity.ok(concertService.createConcert(concert));
    }

    @PutMapping("/{concertId}/attend_user/{userId}")
    public ResponseEntity<Concert> addAttendeeToConcert(@PathVariable Long concertId, @PathVariable Long userId) {
        return ResponseEntity.ok(concertService.addAttendeeToConcert(concertId, userId));
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
