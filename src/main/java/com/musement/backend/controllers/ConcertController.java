package com.musement.backend.controllers;

import com.musement.backend.models.Concert;
import com.musement.backend.services.ConcertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
        return concertService.getConcertById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Concert> createConcert(@RequestBody Concert concert) {
        return ResponseEntity.ok(concertService.createConcert(concert));
    }

    @PutMapping("/{id}/title")
    public ResponseEntity<Concert> updateConcertTitle(@PathVariable Long id, @RequestBody String title) {
        return ResponseEntity.ok(concertService.updateConcertTitle(id, title));
    }

    @PutMapping("/{id}/artist")
    public ResponseEntity<Concert> updateConcertArtist(@PathVariable Long id, @RequestBody String artist) {
        return ResponseEntity.ok(concertService.updateConcertArtist(id, artist));
    }

    @PutMapping("/{id}/datetime")
    public ResponseEntity<Concert> updateConcertDateTime(@PathVariable Long id, @RequestBody LocalDateTime dateTime) {
        return ResponseEntity.ok(concertService.updateConcertDateTime(id, dateTime));
    }

    @PutMapping("/{id}/location")
    public ResponseEntity<Concert> updateConcertLocation(@PathVariable Long id, @RequestBody String location) {
        return ResponseEntity.ok(concertService.updateConcertLocation(id, location));
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
        concertService.deleteConcertById(id);
        return ResponseEntity.noContent().build();
    }
}
