package com.musement.backend.config;

import com.musement.backend.models.Artist;
import com.musement.backend.models.Concert;
import com.musement.backend.repositories.ArtistRepository;
import com.musement.backend.repositories.ConcertRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Configuration
public class TestConcertDataLoader {
    @Bean
    public CommandLineRunner loadTestConcerts(ConcertRepository concertRepository, ArtistRepository artistRepository) {
        return args -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm");

            Object[][] events = new Object[][]{
                {"28 июня 2025, 19:00", "Газпром Арена", "Leonid Agutin", "https://avatars.mds.yandex.net/get-entity_search/2102351/1132859594/SUx182_2x"},
                {"8 ноября 2025, 19:00", "A2", "Guf", "https://yastatic.net/naydex/yandex-search/SFH6hB400/2c630d3Sq/320yYPO8rn0CSlv8Puacw1SPcU33dm2EHJcofPdKYaO9TGlv8yz-oEGeL10oa-gYm4ljzQKy4oTsoBDr61Io07p9y3iXCB_BstLnm5JGOHf4ik1N8zMwoECS4JQ"},
                {"9 ноября 2025, 19:00", "A2", "Guf", "https://yastatic.net/naydex/yandex-search/SFH6hB400/2c630d3Sq/320yYPO8rn0CSlv8Puacw1SPcU33dm2EHJcofPdKYaO9TGlv8yz-oEGeL10oa-gYm4ljzQKy4oTsoBDr61Io07p9y3iXCB_BstLnm5JGOHf4ik1N8zMwoECS4JQ"},
                {"5 июля 2025, 19:00", "Roof Space", "Tatyana Bulanova", "https://avatars.mds.yandex.net/i?id=d74e36745db9977ce9b651f8f5c4725a0f683908-8407394-images-thumbs&n=13"},
                {"11 октября 2025, 19:00", "Ледовый дворец", "Sergey Lazarev", "https://go.zvuk.com/imgs/2023/01/12/05/5749228/81787e19163595189c9abb8acecc1a805093e96e.jpg"},
                {"1 августа 2025, 19:30", "Двор Гостинки", "Dolphin", "https://s09.stc.yc.kpcdn.net/share/i/12/9884894/wr-960.webp"},
                {"3 августа 2025, 19:30", "Двор Гостинки", "Zoloto", "https://avatars.dzeninfra.ru/get-zen_doc/10148438/pub_64cbabf0d8d81f49e67c0ea5_64cbabfed8d81f49e67c159e/scale_1200"},
                {"8 августа 2025, 19:30", "Двор Гостинки", "Husky", "https://avatars.yandex.net/get-music-content/118603/1e9af9d5.p.3095130/m1000x1000?webp=false"},
                {"20 июля 2025, 19:30", "Двор Гостинки", "Cream Soda", "https://s.ura.news/760/images/news/upload/news/768/773/1052768773/3d3eb673d69cb8cfe392b09c02d1ef5b_250x0_617.443.0.0.jpg"},
                {"5 августа 2025, 19:30", "Двор Гостинки", "Saluki", "https://avatars.mds.yandex.net/i?id=18a60cd39aef5eda72e0370d80c32206_l-4886334-images-thumbs&n=13"},
                {"6 декабря 2025, 19:00", "СКА Арена", "Ivanushki International", "https://avatars.mds.yandex.net/i?id=3e6a5bbf60d2f385dd95c7016ca6aadb_l-5241728-images-thumbs&n=13"},
                {"11 октября 2025, 20:00", "Газпром Арена", "Egor Kreed", "https://uznayvse.ru/images/content/2023/6/8/singer-egor-creed_22.jpg"},
                {"15 июля 2025, 22:00", "Ледовая Арена", "Grigory Leps", "https://i.ytimg.com/vi/rLFp99zD13Y/maxresdefault.jpg"},
                {"14 ноября 2025, 20:00", "A2", "Dmitry Malikov", "https://cdn.culture.ru/images/8d0d0e1d-e9a4-5912-ad9d-2df05a2caf8e"},
                {"14 декабря 2025, 19:00", "Ледовый дворец", "Markul", "https://avatars.mds.yandex.net/i?id=cd655ed4721223090feacf7a550c9dc1_l-10879920-images-thumbs&n=13"},
                {"11 октября 2025, 19:00", "A2", "L'One", "https://avatars.mds.yandex.net/i?id=807c4d439f4582ade7b2a86c8274f05e_l-5236511-images-thumbs&n=13"},
                {"18 октября 2025, 19:00", "Газпром Арена", "Ruki Vverh!", "https://avatars.mds.yandex.net/i?id=82b91ba8b5c5bf1f83af0ce32339bc24_l-4591401-images-thumbs&n=13"},
                {"23 ноября 2025, 20:00", "Рассвет (ex. Mod)", "RSAC", "https://the-flow.ru/uploads/images/origin/12/43/62/02/25/800ca98.png"},
                {"25 июля 2025, 20:00", "Roof Space", "Lolita", "https://go.zvuk.com/imgs/2023/11/17/12/6230174/77f6e63b207ab04e0771312bf8795e96a208fda7.jpg"}
            };

            for (Object[] event : events) {
                String dateStr = (String) event[0];
                String location = (String) event[1];
                String artistName = (String) event[2];
                String imageUrl = (String) event[3];

                LocalDateTime date;
                try {
                    date = LocalDateTime.parse(dateStr, formatter);
                } catch (Exception e) {
                    date = LocalDateTime.parse(dateStr + ", 20:00", formatter);
                }

                // ищем или создаём артиста
                Optional<Artist> artistOpt = artistRepository.findAll().stream()
                        .filter(a -> a.getName().equalsIgnoreCase(artistName))
                        .findFirst();
                Artist artist = artistOpt.orElseGet(() -> {
                    Artist newArtist = new Artist();
                    newArtist.setName(artistName);
                    return artistRepository.save(newArtist);
                });

                
                Concert concert = new Concert(artistName, artist, location, date, imageUrl);
                concertRepository.save(concert);
            }
        };
    }
} 