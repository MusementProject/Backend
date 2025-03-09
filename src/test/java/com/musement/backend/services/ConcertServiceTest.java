package com.musement.backend.services;

import com.musement.backend.models.Concert;
import com.musement.backend.repositories.ArtistRepository;
import com.musement.backend.repositories.ConcertRepository;
import com.musement.backend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConcertServiceTest {
    private ConcertRepository concertRepository;
    private UserRepository userRepository;
    private ArtistRepository artistRepository;

    @InjectMocks
    private ConcertService concertService;

    private List<Concert> concerts = new ArrayList<>();

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
        when(concertRepository.findById(1L)).thenReturn(Optional.ofNullable(concerts.get(0)));

        Concert result = concertService.getConcertById(1L);
        assertEquals(concerts.get(0), result);
        verify(concertRepository).findById(1L);
    }

    @Test
    void getAttendees() {
    }

    @Test
    void isUserAttendingConcert() {
    }

    @Test
    void getConcertsByAttendee() {
    }

    @Test
    void searchConcerts() {
    }

    @Test
    void createConcert() {
    }

    @Test
    void deleteConcert() {
    }

    @Test
    void updateConcert() {
    }

    @Test
    void addAttendeeToConcert() {
    }

    @Test
    void removeAttendeeFromConcert() {
    }

    @Test
    void testUpdateConcert() {
    }

    @Test
    void testAddAttendeeToConcert() {
    }

    @Test
    void testRemoveAttendeeFromConcert() {
    }
}