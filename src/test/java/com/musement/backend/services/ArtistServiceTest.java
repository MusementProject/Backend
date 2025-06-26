package com.musement.backend.services;

import com.musement.backend.models.Artist;
import com.musement.backend.repositories.ArtistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ArtistService artistService;

    private Artist existingArtist;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        existingArtist = new Artist();
        existingArtist.setId(1L);
        existingArtist.setName("The Beatles");
    }

    @Test
    void getAllArtists_ReturnsListFromRepository() {
        List<Artist> artists = List.of(existingArtist);
        when(artistRepository.findAll()).thenReturn(artists);

        List<Artist> result = artistService.getAllArtists();

        assertThat(result).isSameAs(artists);
        verify(artistRepository).findAll();
    }

    @Test
    void getArtistById_Found_ReturnsOptionalArtist() {
        when(artistRepository.findById(1L)).thenReturn(Optional.of(existingArtist));

        Optional<Artist> result = artistService.getArtistById(1L);

        assertThat(result).contains(existingArtist);
        verify(artistRepository).findById(1L);
    }

    @Test
    void getArtistById_NotFound_ReturnsEmptyOptional() {
        when(artistRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Artist> result = artistService.getArtistById(2L);

        assertThat(result).isEmpty();
        verify(artistRepository).findById(2L);
    }

    @Test
    void createArtist_NewName_SavesAndReturnsArtist() {
        Artist toCreate = new Artist();
        toCreate.setName("Queen");

        when(artistRepository.findArtistByName("Queen")).thenReturn(Optional.empty());
        when(artistRepository.save(toCreate)).thenAnswer(inv -> {
            Artist saved = inv.getArgument(0);
            saved.setId(99L);
            return saved;
        });

        Artist result = artistService.createArtist(toCreate);

        assertThat(result.getId()).isEqualTo(99L);
        assertThat(result.getName()).isEqualTo("Queen");
        verify(artistRepository).findArtistByName("Queen");
        verify(artistRepository).save(toCreate);
    }

    @Test
    void createArtist_DuplicateName_ThrowsException() {
        Artist dup = new Artist();
        dup.setName("The Beatles");
        when(artistRepository.findArtistByName("The Beatles"))
                .thenReturn(Optional.of(existingArtist));

        assertThatThrownBy(() -> artistService.createArtist(dup))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already exists");

        verify(artistRepository).findArtistByName("The Beatles");
        verify(artistRepository, never()).save(any());
    }

    @Test
    void deleteArtistById_ExistingId_Deletes() {
        when(artistRepository.findById(1L)).thenReturn(Optional.of(existingArtist));

        artistService.deleteArtistById(1L);

        verify(artistRepository).findById(1L);
        verify(artistRepository).deleteById(1L);
    }

    @Test
    void deleteArtistById_NotFound_ThrowsException() {
        when(artistRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> artistService.deleteArtistById(2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");

        verify(artistRepository).findById(2L);
        verify(artistRepository, never()).deleteById(any());
    }

    @Test
    void findOrCreateArtist_ExistingName_ReturnsIt() {
        when(artistRepository.findArtistByName("The Beatles"))
                .thenReturn(Optional.of(existingArtist));

        Artist result = artistService.findOrCreateArtist("The Beatles");

        assertThat(result).isSameAs(existingArtist);
        verify(artistRepository).findArtistByName("The Beatles");
        verify(artistRepository, never()).save(any());
    }

    @Test
    void findOrCreateArtist_NewName_CreatesAndSaves() {
        when(artistRepository.findArtistByName("Nirvana")).thenReturn(Optional.empty());
        Artist newArtist = new Artist();
        newArtist.setName("Nirvana");
        when(artistRepository.save(any(Artist.class))).thenAnswer(inv -> {
            Artist a = inv.getArgument(0);
            a.setId(123L);
            return a;
        });

        Artist result = artistService.findOrCreateArtist("Nirvana");

        assertThat(result.getId()).isEqualTo(123L);
        assertThat(result.getName()).isEqualTo("Nirvana");
        verify(artistRepository).findArtistByName("Nirvana");
        verify(artistRepository).save(any(Artist.class));
    }

    @Test
    void updateArtist_SavesAndReturns() {
        Artist toUpdate = new Artist();
        toUpdate.setId(5L);
        toUpdate.setName("New Name");
        when(artistRepository.save(toUpdate)).thenReturn(toUpdate);

        Artist result = artistService.updateArtist(toUpdate);

        assertThat(result).isSameAs(toUpdate);
        verify(artistRepository).save(toUpdate);
    }
}
