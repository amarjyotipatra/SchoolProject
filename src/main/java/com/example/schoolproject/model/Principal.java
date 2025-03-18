package com.example.schoolproject.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Entity
@Getter
@Setter
@ToString
public class Principal extends User implements Serializable {

    public Principal() {
        setRole(Role.PRINCIPAL);
    }
}
