package com.musement.backend.services;

import com.musement.backend.models.User;
import com.musement.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;
import com.musement.backend.models.Concert;
import com.musement.backend.repositories.ConcertRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;

@Service
public class ConcertService {
    private final ConcertRepository concertRepository;
    private final UserRepository userRepository;

    public ConcertService(ConcertRepository concertRepository, UserRepository userRepository) {
        this.concertRepository = concertRepository;
        this.userRepository = userRepository;
    }

    public List<Concert> getAllConcerts() {
        return concertRepository.findAll();
    }

    public Optional<Concert> getConcertById(Long id) {
        return concertRepository.findById(id);
    }

    public Concert createConcert(Concert concert) {
        return concertRepository.save(concert);
    }

    public void deleteConcertById(Long id) {
        concertRepository.deleteById(id);
    }

    public Concert updateConcertTitle(Long id, String title) {
        return concertRepository.findById(id).map(concert -> {
            concert.setTitle(title);
            return concertRepository.save(concert);
        }).orElseThrow(() -> new RuntimeException("Concert with id " + id + " not found."));
    }

    public Concert updateConcertArtist(Long id, String artist) {
        return concertRepository.findById(id).map(concert -> {
            concert.setArtist(artist);
            return concertRepository.save(concert);
        }).orElseThrow(() -> new RuntimeException("Concert with id " + id + " not found."));
    }

    public Concert updateConcertDateTime(Long id, LocalDateTime dateTime) {
        return concertRepository.findById(id).map(concert -> {
            concert.setDate(dateTime);
            return concertRepository.save(concert);
        }).orElseThrow(() -> new RuntimeException("Concert with id " + id + " not found."));
    }

    public Concert updateConcertLocation(Long id, String location) {
        return concertRepository.findById(id).map(concert -> {
            concert.setLocation(location);
            return concertRepository.save(concert);
        }).orElseThrow(() -> new RuntimeException("Concert with id " + id + " not found."));
    }

    public Concert addAttendeeToConcert(Long concertId, Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Concert> concertOpt = concertRepository.findById(concertId);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User with id " + userId + " not found.");
        } else if (concertOpt.isEmpty()) {
            throw new RuntimeException("Concert with id " + concertId + " not found.");
        }
        User user = userOpt.get();
        Concert concert = concertOpt.get();

        user.getAttendingConcerts().add(concert);
        userRepository.save(user);
        concert.getAttendees().add(user);
        concertRepository.save(concert);

        return concert;
    }

    public Concert removeAttendeeFromConcert(Long concertId, Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Concert> concertOpt = concertRepository.findById(concertId);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User with id " + userId + " not found.");
        } else if (concertOpt.isEmpty()) {
            throw new RuntimeException("Concert with id " + concertId + " not found.");
        }
        User user = userOpt.get();
        Concert concert = concertOpt.get();

        user.getAttendingConcerts().remove(concert);
        userRepository.save(user);
        concert.getAttendees().remove(user);
        concertRepository.save(concert);

        return concert;
    }
}
