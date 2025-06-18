package com.musement.backend.services;

import com.musement.backend.models.Artist;
import com.musement.backend.repositories.ArtistRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArtistService {
    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    public Optional<Artist> getArtistById(Long id) {
        return artistRepository.findById(id);
    }

    public Artist createArtist(Artist artist) {
        if (artistRepository.findArtistByName(artist.getName()).isPresent()) {
            throw new RuntimeException("Artist with name " + artist.getName() + " already exists.");
        }
        return artistRepository.save(artist);
    }

    public void deleteArtistById(Long id) {
        if (artistRepository.findById(id).isEmpty()) {
            throw new RuntimeException("Artist with id " + id + " not found.");
        }
        artistRepository.deleteById(id);
    }

    public Artist findOrCreateArtist(String name) {
        Optional<Object> artistOpt = artistRepository.findArtistByName(name);
        if (artistOpt.isPresent()) {
            return (Artist) artistOpt.get();
        }
        Artist artist = new Artist();
        artist.setName(name);
        return artistRepository.save(artist);
    }

    public Artist updateArtist(Artist artist) {
        return artistRepository.save(artist);
    }
}
