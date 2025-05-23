package com.musement.backend.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.musement.backend.models.Image;
import com.musement.backend.repositories.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private final ImageRepository imageRepository;

    /**
     * Загружает картинку в Cloudinary, сохраняет URL в базе и возвращает сущность Image.
     *
     * @param data   — байты файла
     * @param format — расширение ("jpg", "png" и т.п.)
     * @return сохранённая сущность Image (с URL и timestamp)
     */
    public Image uploadAndSave(byte[] data, String format) throws IOException {
        // 1) загружаем в Cloudinary
        String publicId = "images/" + UUID.randomUUID();
        @SuppressWarnings("unchecked")
        Map<String, Object> result = cloudinary.uploader()
                .upload(data, ObjectUtils.asMap(
                        "public_id", publicId,
                        "format", format
                ));

        String url = (String) result.get("secure_url");

        // 2) сохраняем в БД
        Image image = Image.builder()
                .url(url)
                .build();
        return imageRepository.save(image);
    }
}
