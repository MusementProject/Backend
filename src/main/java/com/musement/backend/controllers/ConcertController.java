package com.musement.backend.controllers;

import com.musement.backend.dto.ConcertUpdateDTO;
import com.musement.backend.dto.ConcertDTO;
import com.musement.backend.models.Concert;
import com.musement.backend.models.User;
import com.musement.backend.models.ArtistStatistics;
import com.musement.backend.repositories.ConcertRepository;
import com.musement.backend.repositories.UserRepository;
import com.musement.backend.repositories.ArtistStatisticsRepository;
import com.musement.backend.services.ConcertService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

// TODO: write tests for this

@RestController
@RequestMapping("/api/concerts")
public class ConcertController {
    private final ConcertService concertService;
    private final ConcertRepository concertRepository;
    private final UserRepository userRepository;
    private final ArtistStatisticsRepository artistStatisticsRepository;

    public ConcertController(ConcertService concertService, ConcertRepository concertRepository, UserRepository userRepository, ArtistStatisticsRepository artistStatisticsRepository) {
        this.concertService = concertService;
        this.concertRepository = concertRepository;
        this.userRepository = userRepository;
        this.artistStatisticsRepository = artistStatisticsRepository;
    }

    @GetMapping
    public List<Concert> getAllConcerts() {
        return concertService.getAllConcerts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Concert> getConcertById(@PathVariable Long id) {
        return ResponseEntity.ok(concertService.getConcertById(id));
    }

    @GetMapping("/{id}/attendees")
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

    // хотим хотя бы 10 концертов в ленте --> если нет столько "знакомых", докидываем ближайшие концерты по дате
    @GetMapping("/feed/{userId}")
    public List<ConcertDTO> getConcertFeed(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow();

        // все концерты, на которые не идем
        Set<Concert> profileConcerts = user.getProfileConcerts();
        List<Concert> allAvailable = concertRepository.findAll().stream()
            .filter(c -> !profileConcerts.contains(c))
            .toList();

        // для сорта
        Map<Long, Integer> artistMetric = user.getPlaylists().stream()
            .flatMap(p -> p.getArtistStats().stream())
            .collect(java.util.stream.Collectors.groupingBy(
                stat -> stat.getArtist().getId(),
                java.util.stream.Collectors.summingInt(stat -> stat.getTrackCount())
            ));

        List<Concert> familiar = allAvailable.stream()
            .filter(c -> artistMetric.getOrDefault(c.getArtist().getId(), 0) > 0)
            .sorted((a, b) -> Integer.compare(
                artistMetric.getOrDefault(b.getArtist().getId(), 0),
                artistMetric.getOrDefault(a.getArtist().getId(), 0)))
            .toList();

        List<Concert> unfamiliar = allAvailable.stream()
            .filter(c -> artistMetric.getOrDefault(c.getArtist().getId(), 0) == 0)
            .sorted(java.util.Comparator.comparing(Concert::getDate))
            .toList();

        List<Concert> result = new java.util.ArrayList<>();
        result.addAll(familiar);
        if (familiar.size() < 10) {
            int need = 10 - familiar.size();
            result.addAll(unfamiliar.stream().limit(need).toList());
        }

        return result.stream()
            .map(this::toDTO)
            .toList();
    }

    @GetMapping("/attending/{userId}")
    public List<ConcertDTO> getAttendingConcerts(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return user.getProfileConcerts().stream()
                .sorted(Comparator.comparing(Concert::getDate))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/attend")
    public void attendConcert(@RequestParam Long userId, @RequestParam Long concertId) {
        concertService.attendConcertAndUpdateFeed(userId, concertId);
    }

    private ConcertDTO toDTO(Concert concert) {
        ConcertDTO dto = new ConcertDTO();
        dto.setId(concert.getId());
        dto.setArtistId(concert.getArtist().getId());
        dto.setArtistName(concert.getArtist().getName());
        dto.setImageUrl(concert.getImageUrl());
        dto.setLocation(concert.getLocation());
        dto.setDate(concert.getDate());
        return dto;
    }
}
