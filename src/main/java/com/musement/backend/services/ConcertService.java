package com.musement.backend.services;

import com.musement.backend.dto.ConcertUpdateDTO;
import com.musement.backend.models.Artist;
import com.musement.backend.models.Concert;
import com.musement.backend.models.User;
import com.musement.backend.repositories.ArtistRepository;
import com.musement.backend.repositories.ConcertRepository;
import com.musement.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConcertService {
    private final ConcertRepository concertRepository;
    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;

    public ConcertService(ConcertRepository concertRepository, UserRepository userRepository, ArtistRepository artistRepository) {
        this.concertRepository = concertRepository;
        this.userRepository = userRepository;
        this.artistRepository = artistRepository;
    }

    public List<Concert> getAllConcerts() {
        return concertRepository.findAll();
    }

    public Concert getConcertById(Long id) {
        return getConcertOrThrow(id);
    }

    public Concert createConcert(Concert concert) {
        return concertRepository.save(concert);
    }

    public void deleteConcert(Long id) {
        concertRepository.deleteById(id);
    }

    public Concert updateConcert(Long id, ConcertUpdateDTO dto) {
        Concert concert = getConcertOrThrow(id);

        if (dto.getTitle() != null) concert.setTitle(dto.getTitle());
        if (dto.getLocation() != null) concert.setLocation(dto.getLocation());
        if (dto.getDateTime() != null) concert.setDate(dto.getDateTime());
        if (dto.getArtistId() != null) concert.setArtist(getArtistOrThrow(dto.getArtistId()));

        return concertRepository.save(concert);
    }

    @Transactional
    public Concert addAttendeeToConcert(Long concertId, Long userId) {
        User user = getUserOrThrow(userId);
        Concert concert = getConcertOrThrow(concertId);
        user.getAttendingConcerts().add(concert);
        concert.getAttendees().add(user);
        return concert;
    }

    @Transactional
    public Concert removeAttendeeFromConcert(Long concertId, Long userId) {
        User user = getUserOrThrow(userId);
        Concert concert = getConcertOrThrow(concertId);
        user.getAttendingConcerts().remove(concert);
        concert.getAttendees().remove(user);
        return concert;
    }

    private Concert getConcertOrThrow(Long id) {
        return concertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Concert with id " + id + " not found."));
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " not found."));
    }

    private Artist getArtistOrThrow(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artist with id " + id + " not found."));
    }
}

