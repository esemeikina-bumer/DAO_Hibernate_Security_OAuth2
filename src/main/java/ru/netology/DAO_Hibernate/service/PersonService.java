package ru.netology.DAO_Hibernate.service;

import ru.netology.DAO_Hibernate.entity.Person;
import ru.netology.DAO_Hibernate.entity.PersonId;
import ru.netology.DAO_Hibernate.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person addPerson(Person person) {
        return personRepository.save(person);
    }

    public Person getById(String name, String surname, Integer age) {
        return personRepository.findById(name, surname, age)
                .orElseThrow(() -> new RuntimeException("Person not found"));
    }

    public List<Person> getByCity(String city) {
        return personRepository.findByCityOfLiving(city);
    }

    public List<Person> getAll() {
        return personRepository.findAll();
    }

    public Person update(Person person) {
        return personRepository.update(person);
    }

    public boolean delete(String name, String surname, Integer age) {
        return personRepository.deleteById(new PersonId(name, surname, age));
    }

    public long count() {
        return personRepository.count();
    }
}