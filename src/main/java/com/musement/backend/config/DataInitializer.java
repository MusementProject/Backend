package com.musement.backend.config;

import com.musement.backend.models.User;
import com.musement.backend.repositories.UserRepository;
import com.musement.backend.services.RegistrationService;
import com.musement.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;
    private final RegistrationService service;

    private final List<String> usernames = Arrays.asList(
            "malena",
            "milena",
            "chepyr",
            "amiasleep",
            "guten_morgennn",
            "paretooptimality",
            "caandies",
            "voopsien",
            "poopsien",
            "loontik"
    );

    private final List<String> profilePictures = Arrays.asList(
            "https://static.wikia.nocookie.net/smesharikiarhives/images/b/ba/%D0%9D%D1%8E%D1%88%D0%B0_%D0%A2%D0%97.png/revision/latest?cb=20200929163906&path-prefix=ru",
            "https://static.wikia.nocookie.net/smesharikiarhives/images/2/24/%D0%A1%D0%BE%D0%B2%D1%83%D0%BD%D1%8C%D1%8F_%D1%82%D0%BE%D0%B2%D0%B0%D1%80%D0%BD%D1%8B%D0%B9_%D0%B7%D0%BD%D0%B0%D0%BA.png/revision/latest?cb=20200929163742&path-prefix=ru",
            "https://avatanplus.com/files/resources/original/574b22d6ef160154fd8017c3.png",
            "https://img-fotki.yandex.ru/get/4704/16969765.21d/0_8e65b_2743b2fb_orig.png",
            "https://foni.papik.pro/uploads/posts/2024-09/foni-papik-pro-vjgq-p-kartinki-smeshariki-krosh-na-prozrachnom-f-7.png",
            "https://img-fotki.yandex.ru/get/9325/16969765.1b7/0_87b5e_292d6ff2_orig.png",
            "https://i.pinimg.com/736x/7a/d8/f7/7ad8f77aa986437a102f057979a927aa.jpg",
            "https://pm1.aminoapps.com/6782/88ba87ddd4471ff954bd95103951f2a84ca0246fv2_00.jpg",
            "https://static.wikia.nocookie.net/luntikbackrooms/images/d/d0/2266200453.jpeg/revision/latest/thumbnail/width/360/height/450?cb=20240328091039&path-prefix=ru",
            "https://static.wikia.nocookie.net/luntik/images/9/92/%D0%9B%D1%83%D0%BD%D1%82%D0%B8%D0%BA_2D.png/revision/latest?cb=20240330214351&path-prefix=ru"
    );

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                for (int i = 0; i < 10; i++) {
                    User user = new User();
                    user.setUsername(usernames.get(i));
                    user.setPassword(passwordEncoder.encode("password" + (i + 1)));
                    user.setEmail("user" + (i + 1) + "@example.com");
                    user.setGoogleId(UUID.randomUUID().toString());
                    user.setNickname("nickname" + (i + 1));
                    user.setBio("Bio of user " + (i + 1));
                    user.setProfilePicture(profilePictures.get(i));
                    user.setTelegram("@" + usernames.get(i));

                    userRepository.save(user);
                    service.indexUser(user);
                }
            }
        };
    }
}
