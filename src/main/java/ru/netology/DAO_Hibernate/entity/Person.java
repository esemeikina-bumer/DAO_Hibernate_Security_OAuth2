package ru.netology.DAO_Hibernate.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "PERSONS")
@IdClass(PersonId.class)
public class Person {

    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @Id
    @Column(name = "surname", nullable = false)
    private String surname;

    @Id
    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "city_of_living")
    private String cityOfLiving;

     public Person() {
    }

    public Person(String name, String surname, Integer age,
                  String phoneNumber, String cityOfLiving) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.cityOfLiving = cityOfLiving;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getCityOfLiving() { return cityOfLiving; }
    public void setCityOfLiving(String cityOfLiving) { this.cityOfLiving = cityOfLiving; }
}