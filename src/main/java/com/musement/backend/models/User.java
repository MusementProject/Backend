package com.musement.backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {
    @JsonProperty("name")
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Генерация идентификатора автоматически (если это необходимо)
    private Long id;

    public User(){
        this.name = "unknown user";
    }

    public User(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setName(String newName){
        name = newName;
    }
}
