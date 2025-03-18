package com.example.schoolproject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
public class Principal extends User implements Serializable {

    @Override
    public String toString() {
        return "Principal{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
