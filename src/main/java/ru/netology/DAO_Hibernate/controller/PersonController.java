package ru.netology.DAO_Hibernate.controller;

import org.springframework.web.bind.annotation.*;
import ru.netology.DAO_Hibernate.entity.Person;
import ru.netology.DAO_Hibernate.repository.PersonRepository;

import java.util.List;

@RestController
public class PersonController {

    private final PersonRepository personRepository;

    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    // Было: GET /api/persons
    @GetMapping("/api/persons")
    public List<Person> getAll() {
        return personRepository.findAll();
    }

    // Новое задание: GET /persons/by-city?city=Moscow
    @GetMapping("/persons/by-city")
    public List<Person> getByCity(@RequestParam("city") String city) {
        return personRepository.findByCityOfLiving(city);
    }

    // POST /api/persons
    @PostMapping("/api/persons")
    public Person create(@RequestBody Person person) {
        return personRepository.save(person);
    }
}