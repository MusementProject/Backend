package com.musement.backend.services;

import com.musement.backend.dto.ConcertUpdateDTO;
import com.musement.backend.models.Artist;
import com.musement.backend.models.Concert;
import com.musement.backend.models.User;
import com.musement.backend.repositories.ArtistRepository;
import com.musement.backend.repositories.ConcertRepository;
import com.musement.backend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConcertServiceTest {
    private ConcertRepository concertRepository;
    private UserRepository userRepository;
    private ArtistRepository artistRepository;

    @InjectMocks
    private ConcertService concertService;

    private final List<Concert> concerts = new ArrayList<>();

    @BeforeEach
    void setUp() {
        concertRepository = Mockito.mock(ConcertRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        artistRepository = Mockito.mock(ArtistRepository.class);

        concertService = new ConcertService(concertRepository, userRepository, artistRepository);

        Concert concert1 = new Concert();
        concert1.setId(1L);
        concert1.setLocation("St. Petersburg");
        concert1.setDate(LocalDateTime.parse("2018-12-30T19:34:50.63"));
        concerts.add(concert1);

        Concert concert2 = new Concert();
        concert2.setId(2L);
        concert2.setLocation("Moscow");
        concert2.setDate(LocalDateTime.parse("2018-12-30T19:34:50.63"));
        concerts.add(concert2);
    }

    @Test
    void getAllConcerts() {
        when(concertRepository.findAll()).thenReturn(concerts);

        assertEquals(concerts, concertService.getAllConcerts());
        verify(concertRepository).findAll();
    }

    @Test
    void getConcertById() {
        when(concertRepository.findById(1L)).thenReturn(Optional.of(concerts.get(0)));

        Concert result = concertService.getConcertById(1L);
        assertEquals(concerts.get(0), result);
        verify(concertRepository).findById(1L);
    }

    @Test
    void getAttendees() {
        Concert concert = concerts.get(0);
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        Set<User> attendees = Set.of(user1, user2);
        concert.setAttendees(attendees);

        when(concertRepository.findById(1L)).thenReturn(Optional.of(concert));

        Set<User> result = concertService.getAttendees(1L);
        assertEquals(attendees, result);
    }

    @Test
    void isUserAttendingConcert() {
        Concert concert = concerts.get(0);
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        concert.setAttendees(Set.of(user1));

        when(concertRepository.findById(1L)).thenReturn(Optional.of(concert));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        boolean result1 = concertService.isUserAttendingConcert(1L, 1L);
        assertTrue(result1);
        boolean result2 = concertService.isUserAttendingConcert(1L, 2L);
        assertFalse(result2);

        // if invalid user id
        assertThrows(EntityNotFoundException.class, () -> concertService.isUserAttendingConcert(1L, 3L));
        // if invalid concert id
        assertThrows(EntityNotFoundException.class, () -> concertService.isUserAttendingConcert(3L, 1L));
    }

    @Test
    void getConcertsByAttendee() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        Concert concert1 = concerts.get(0);
        concert1.setAttendees(Set.of(user1));
        Concert concert2 = concerts.get(1);
        concert2.setAttendees(Set.of(user1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(concertRepository.findByAttendee(1L)).thenReturn(List.of(concert1, concert2));

        assertEquals(List.of(concert1, concert2), concertService.getConcertsByAttendee(1L));
        verify(concertRepository).findByAttendee(1L);
    }

    @Test
    void searchConcerts() {
        // TODO tests for searchConcerts ?
    }

    @Test
    void createConcert() {
        Concert concert = new Concert();
        concert.setId(3L);
        concert.setArtist(null);
        concert.setLocation("New York");

        when(concertRepository.save(concert)).thenReturn(concert);

        Concert result = concertService.createConcert(concert);
        assertEquals(concert, result);
        verify(concertRepository).save(concert);
    }

    @Test
    void deleteConcert() {
        Concert concert = concerts.get(0);
        User user1 = new User();
        user1.setId(100L);
        user1.setUsername("Anna");
        user1.setAttendingConcerts(Set.of(concert));
        concert.setAttendees(Set.of(user1));

        when(concertRepository.findById(1L)).thenReturn(Optional.of(concert));
        when(userRepository.findById(100L)).thenReturn(Optional.of(user1));

        concertService.deleteConcert(1L);
        verify(concertRepository).delete(concert);
        verify(userRepository).save(user1);
    }

    @Test
    void updateConcert() {
        Concert concert = new Concert();
        concert.setId(1L);
        concert.setTitle("Old Title");
        concert.setLocation("Old Location");
        LocalDateTime oldDate = LocalDateTime.now().minusDays(1);
        concert.setDate(oldDate);
        Artist oldArtist = new Artist();
        oldArtist.setId(10L);
        concert.setArtist(oldArtist);

        ConcertUpdateDTO dto = new ConcertUpdateDTO();
        dto.setTitle("New Title");
        dto.setLocation("New Location");
        LocalDateTime newDate = LocalDateTime.now().plusDays(1);
        dto.setDateTime(newDate);
        dto.setArtistId(20L);

        Artist newArtist = new Artist();
        newArtist.setId(20L);

        when(concertRepository.findById(1L)).thenReturn(Optional.of(concert));
        when(artistRepository.findById(20L)).thenReturn(Optional.of(newArtist));
        when(concertRepository.save(concert)).thenReturn(concert);

        Concert updatedConcert = concertService.updateConcert(1L, dto);
        assertEquals("New Title", updatedConcert.getTitle());
        assertEquals("New Location", updatedConcert.getLocation());
        assertEquals(newArtist, updatedConcert.getArtist());
        assertEquals(newDate, updatedConcert.getDate());
    }

    @Test
    void addAttendeeToConcert() {
        Concert concert = new Concert();
        concert.setId(1L);
        concert.setAttendees(new HashSet<>());

        User user = new User();
        user.setId(100L);
        user.setAttendingConcerts(new HashSet<>());

        when(concertRepository.findById(1L)).thenReturn(Optional.of(concert));
        when(userRepository.findById(100L)).thenReturn(Optional.of(user));
        when(concertRepository.save(concert)).thenReturn(concert);
        when(userRepository.save(user)).thenReturn(user);

        Concert result = concertService.addAttendeeToConcert(1L, 100L);
        assertTrue(result.getAttendees().contains(user));
        assertTrue(user.getAttendingConcerts().contains(concert));
        verify(userRepository).save(user);
        verify(concertRepository).save(concert);
    }

    @Test
    void removeAttendeeFromConcert() {
        Concert concert = new Concert();
        concert.setId(1L);
        Set<User> attendees = new HashSet<>();
        User user = new User();
        user.setId(100L);
        attendees.add(user);
        concert.setAttendees(attendees);

        Set<Concert> userConcerts = new HashSet<>();
        userConcerts.add(concert);
        user.setAttendingConcerts(userConcerts);

        when(concertRepository.findById(1L)).thenReturn(Optional.of(concert));
        when(userRepository.findById(100L)).thenReturn(Optional.of(user));
        when(concertRepository.save(concert)).thenReturn(concert);
        when(userRepository.save(user)).thenReturn(user);

        Concert result = concertService.removeAttendeeFromConcert(1L, 100L);
        assertFalse(result.getAttendees().contains(user));
        assertFalse(user.getAttendingConcerts().contains(concert));
        verify(userRepository).save(user);
        verify(concertRepository).save(concert);
    }

    @Test
    void testUpdateConcertNoChanges() {
        Concert concert = new Concert();
        concert.setId(1L);
        concert.setTitle("init");
        concert.setLocation("init");
        concert.setDate(LocalDateTime.of(2021, 1, 1, 12, 0));

        Artist artist = new Artist();
        artist.setId(3L);
        artist.setName("Anna");
        concert.setArtist(artist);

        ConcertUpdateDTO dto = new ConcertUpdateDTO();
        dto.setArtistId(null);
        dto.setTitle(null);
        dto.setLocation(null);
        dto.setDateTime(null);

        when(concertRepository.findById(1L)).thenReturn(Optional.of(concert));
        when(concertRepository.save(concert)).thenReturn(concert);

        Concert updatedConcert = concertService.updateConcert(1L, dto);
        assertEquals("init", updatedConcert.getTitle());
        assertEquals("init", updatedConcert.getLocation());
        assertEquals(LocalDateTime.of(2021, 1, 1, 12, 0), updatedConcert.getDate());
        assertEquals(artist, updatedConcert.getArtist());
    }

    @Test
    void testUpdateConcert() {
        Concert concert = new Concert();
        concert.setId(1L);
        concert.setTitle("init");
        concert.setLocation("init");
        concert.setDate(LocalDateTime.of(2021, 1, 1, 12, 0));

        Artist artist = new Artist();
        artist.setId(3L);
        artist.setName("Anna");
        concert.setArtist(artist);

        ConcertUpdateDTO dto = new ConcertUpdateDTO();
        dto.setTitle("new title");
        dto.setLocation("new location");
        // no change in date and artist
        dto.setDateTime(null);
        dto.setArtistId(null);

        when(concertRepository.findById(1L)).thenReturn(Optional.of(concert));
        when(concertRepository.save(concert)).thenReturn(concert);

        Concert updatedConcert = concertService.updateConcert(1L, dto);
        assertEquals("new title", updatedConcert.getTitle());
        assertEquals("new location", updatedConcert.getLocation());
        assertEquals(LocalDateTime.of(2021, 1, 1, 12, 0), updatedConcert.getDate());
        assertEquals(artist, updatedConcert.getArtist());

        verify(concertRepository).save(concert);
    }

    @Test
    void testAddAttendeeToConcert() {
        // TODO test addAttendeeToConcert
    }

    @Test
    void testRemoveAttendeeFromConcert() {
        // TODO test removeAttendeeFromConcert
    }
}