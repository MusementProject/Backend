package com.musement.backend.services;

import com.musement.backend.dto.ConcertUpdateDTO;
import com.musement.backend.models.Artist;
import com.musement.backend.models.Concert;
import com.musement.backend.models.User;
import com.musement.backend.repositories.ArtistRepository;
import com.musement.backend.repositories.ConcertRepository;
import com.musement.backend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public Set<User> getAttendees(Long id) {
        return getConcertOrThrow(id).getAttendees();
    }

    public boolean isUserAttendingConcert(Long concertId, Long userId) {
        Concert concert = getConcertOrThrow(concertId);
        User user = getUserOrThrow(userId);

        return concert.getAttendees().contains(user);
    }

    public List<Concert> getConcertsByAttendee(Long userId) {
        return concertRepository.findByAttendee(userId);
    }

    /**
     * Search concerts by location and date range
     */
    public List<Concert> searchConcerts(String location, LocalDate dateFrom, LocalDate dateTo) {
        return concertRepository.findByFilters(location, dateFrom, dateTo);
    }

    public Concert createConcert(Concert concert) {
        return concertRepository.save(concert);
    }

    public void deleteConcert(Long id) {
        Concert concert = getConcertOrThrow(id);

        for (User attendee : concert.getAttendees()) {
            Set<Concert> attendingConcerts = new HashSet<>(attendee.getAttendingConcerts());
            attendingConcerts.remove(concert);
            attendee.setAttendingConcerts(attendingConcerts);
            userRepository.save(attendee);
        }
        concertRepository.delete(concert);
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

        userRepository.save(user);
        concertRepository.save(concert);
        updateConcertFeedForUser(user);
        return concert;
    }

    @Transactional
    public Concert removeAttendeeFromConcert(Long concertId, Long userId) {
        User user = getUserOrThrow(userId);
        Concert concert = getConcertOrThrow(concertId);

        user.getAttendingConcerts().remove(concert);
        concert.getAttendees().remove(user);

        userRepository.save(user);
        concertRepository.save(concert);
        updateConcertFeedForUser(user);
        return concert;
    }

    public void initializeDefaultConcertFeed(User user) {
        List<Concert> upcoming = concertRepository.findAll().stream()
                .filter(c -> c.getDate().isAfter(java.time.LocalDateTime.now()))
                .filter(c -> user.getProfileConcerts() == null || !user.getProfileConcerts().contains(c))
                .filter(c -> user.getWishlistConcerts() == null || !user.getWishlistConcerts().contains(c))
                .sorted(java.util.Comparator.comparing(Concert::getDate))
                .limit(10)
                .toList();
        user.getConcertFeed().clear();
        user.getConcertFeed().addAll(upcoming);
        userRepository.save(user);
        updateConcertFeedForUser(user);
    }

    @Transactional
    public void attendConcertAndUpdateFeed(Long userId, Long concertId) {
        User user = getUserOrThrow(userId);
        Concert concert = getConcertOrThrow(concertId);
        user.getProfileConcerts().add(concert);
        user.getConcertFeed().remove(concert);
        user.getAttendingConcerts().add(concert);
        concert.getAttendees().add(user);

        userRepository.save(user);
        concertRepository.save(concert);
        updateConcertFeedForUser(user);
    }

    @Transactional
    public void addToWishlist(Long userId, Long concertId) {
        User user = getUserOrThrow(userId);
        Concert concert = getConcertOrThrow(concertId);
        user.getWishlistConcerts().add(concert);
        user.getConcertFeed().remove(concert);
        userRepository.save(user);
        updateConcertFeedForUser(user);
    }

    @Transactional
    public void removeFromWishlist(Long userId, Long concertId) {
        User user = getUserOrThrow(userId);
        Concert concert = getConcertOrThrow(concertId);
        user.getWishlistConcerts().remove(concert);
        userRepository.save(user);
    }

    public boolean isUserWishlistingConcert(Long concertId, Long userId) {
        User user = getUserOrThrow(userId);
        Concert concert = getConcertOrThrow(concertId);
        return user.getWishlistConcerts().contains(concert);
    }

    @Transactional
    public void moveFromWishlistToAttending(Long userId, Long concertId) {
        User user = getUserOrThrow(userId);
        Concert concert = getConcertOrThrow(concertId);
        user.getWishlistConcerts().remove(concert);
        user.getProfileConcerts().add(concert);
        user.getAttendingConcerts().add(concert);
        concert.getAttendees().add(user);
        userRepository.save(user);
        concertRepository.save(concert);
    }

    public void updateConcertFeedForUser(User user) {
        Set<Concert> profileConcerts = user.getProfileConcerts();
        Set<Concert> wishlistConcerts = user.getWishlistConcerts();
        List<Concert> feed = user.getConcertFeed().stream()
                .filter(c -> !profileConcerts.contains(c))
                .filter(c -> !wishlistConcerts.contains(c))
                .toList();
        if (feed.isEmpty()) {
            initializeDefaultConcertFeed(user);
            return;
        }
        Map<Long, Integer> artistMetric = user.getPlaylists().stream()
                .flatMap(p -> p.getArtistStats().stream())
                .collect(java.util.stream.Collectors.groupingBy(
                        stat -> stat.getArtist().getId(),
                        java.util.stream.Collectors.summingInt(stat -> stat.getTrackCount())
                ));

        Map<Long, Integer> artistStats = new java.util.HashMap<>();
        for (var stat : user.getPlaylists().stream().flatMap(p -> p.getArtistStats().stream()).toList()) {
            Long artistId = stat.getArtist().getId();
            artistStats.put(artistId, artistMetric.getOrDefault(artistId, 0));
        }

        List<Concert> sorted = feed.stream()
                .sorted((a, b) -> {
                    int mA = artistStats.getOrDefault(a.getArtist().getId(), 0);
                    int mB = artistStats.getOrDefault(b.getArtist().getId(), 0);
                    return Integer.compare(mB, mA);
                })
                .toList();
        user.getConcertFeed().clear();
        user.getConcertFeed().addAll(sorted);
        userRepository.save(user);
    }

    private Concert getConcertOrThrow(Long id) {
        return concertRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Concert with id " + id + " not found."));
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found."));
    }

    private Artist getArtistOrThrow(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artist with id " + id + " not found."));
    }
}

