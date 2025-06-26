package com.musement.backend.documents;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String username;

    @Field(type = FieldType.Text)
    private String nickname;

    @Field(type = FieldType.Text)
    private String profilePicture;
}
