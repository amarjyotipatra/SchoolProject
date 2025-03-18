package com.example.schoolproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@MappedSuperclass
@Getter
@Setter
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String userName;
    private String password;
    private Role role;

    public enum Role {
        PRINCIPAL, CLASS_TEACHER, CHILD
    }

    @Converter(autoApply = true)
    public static class RoleConverter implements AttributeConverter<Role, String> {
        @Override
        public String convertToDatabaseColumn(Role role) {
            if (role == null) {
                return null;
            }
            return role.name();
        }

        @Override
        public Role convertToEntityAttribute(String dbData) {
            if (dbData == null) {
                return null;
            }try {
                return Role.valueOf(dbData);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role value in database: " + dbData, e);
            }
        }

    }
}
