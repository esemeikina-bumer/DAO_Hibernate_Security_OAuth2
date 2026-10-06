package ru.netology.DAO_Hibernate.entity;


import java.io.Serializable;
import java.util.Objects;

public class PersonId implements Serializable {

    private String name;
    private String surname;
    private Integer age;

    public PersonId() {
    }

    public PersonId(String name, String surname, Integer age) {
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonId other)) return false;
        return Objects.equals(name, other.name)
                && Objects.equals(surname, other.surname)
                && Objects.equals(age, other.age);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, age);
    }
}