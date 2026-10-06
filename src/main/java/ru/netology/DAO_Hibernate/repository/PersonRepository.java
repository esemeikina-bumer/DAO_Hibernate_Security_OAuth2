package ru.netology.DAO_Hibernate.repository;

import ru.netology.DAO_Hibernate.entity.Person;
import ru.netology.DAO_Hibernate.entity.PersonId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class PersonRepository {

    @PersistenceContext
    private EntityManager entityManager;

    // ---------- CREATE ----------

    public Person save(Person person) {
        PersonId id = new PersonId(person.getName(), person.getSurname(), person.getAge());
        Person existing = entityManager.find(Person.class, id);
        if (existing == null) {
            entityManager.persist(person);
            return person;
        } else {
            return entityManager.merge(person);
        }
    }

    // ---------- READ ----------

    @Transactional(readOnly = true)
    public Optional<Person> findById(PersonId id) {
        return Optional.ofNullable(entityManager.find(Person.class, id));
    }

    @Transactional(readOnly = true)
    public Optional<Person> findById(String name, String surname, Integer age) {
        return findById(new PersonId(name, surname, age));
    }

    @Transactional(readOnly = true)
    public List<Person> findAll() {
        return entityManager
                .createQuery("SELECT p FROM Person p", Person.class)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Person> findBySurname(String surname) {
        return entityManager
                .createQuery("SELECT p FROM Person p WHERE p.surname = :surname", Person.class)
                .setParameter("surname", surname)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Person> findByCityOfLiving(String city) {
        return entityManager
                .createQuery("SELECT p FROM Person p WHERE p.cityOfLiving = :city", Person.class)
                .setParameter("city", city)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Person> findByAgeGreaterThan(int age) {
        return entityManager
                .createQuery("SELECT p FROM Person p WHERE p.age > :age ORDER BY p.age", Person.class)
                .setParameter("age", age)
                .getResultList();
    }

    // ---------- UPDATE ----------

    public Person update(Person person) {
        return entityManager.merge(person);
    }

    // ---------- DELETE ----------

    public boolean deleteById(PersonId id) {
        Person person = entityManager.find(Person.class, id);
        if (person != null) {
            entityManager.remove(person);
            return true;
        }
        return false;
    }

    public int deleteByCity(String city) {
        return entityManager
                .createQuery("DELETE FROM Person p WHERE p.cityOfLiving = :city")
                .setParameter("city", city)
                .executeUpdate();
    }

    // ---------- СЛУЖЕБНОЕ ----------

    public long count() {
        return entityManager
                .createQuery("SELECT COUNT(p) FROM Person p", Long.class)
                .getSingleResult();
    }

    public boolean existsById(PersonId id) {
        return entityManager.find(Person.class, id) != null;
    }
}