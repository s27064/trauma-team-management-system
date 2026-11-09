package com.olafnaleszkiewicz.traumateam.service;

import com.olafnaleszkiewicz.traumateam.model.Mission;
import com.olafnaleszkiewicz.traumateam.model.person.*;
import com.olafnaleszkiewicz.traumateam.repository.ClientRepository;
import com.olafnaleszkiewicz.traumateam.repository.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonService {
    private final PersonRepository personRepo;
    private final ClientRepository clientRepo;

    public List<Client> findAllWithPerson() {
        return clientRepo.findAllWithPerson();
    }

    public List<Person> findAllPeople() {
        return personRepo.findAllNoDependencies();
    }

    public List<Person> findAllCrew() {
        return personRepo.findAllCrew();
    }

    public Pilot addPilot(String name, String surname, int age, Address address, String nicknameOrNull) {
        Pilot pilotPart = Pilot.builder()
                .nickname(nicknameOrNull)
                .build();

        return personRepo.save(
                Person.asPilot(name, surname, age, address, pilotPart)).getPilot();
    }

    public Client addClient(String name, String surname, int age, Address address, String email, LocalDate joinDate) {
        Client clientPart = Client.builder()
                .email(email)
                .joinDate(joinDate)
                .build();

        return personRepo.save(
                Person.asClient(name, surname, age, address, clientPart)).getClient();
    }

    public Medic addMedic(String name, String surname, int age, Address address, String specialisation,
            List<String> weaponCerts) {
        Medic medicPart = Medic.builder()
                .specialisation(specialisation)
                .weaponCertifications(weaponCerts)
                .build();

        return personRepo.save(
                Person.asMedic(name, surname, age, address, medicPart)).getMedic();
    }

    @Transactional
    public void deleteClient(Long clientId) {
        Client c = clientRepo.findByIdWithDependencies(clientId)
                .orElseThrow(() -> new EntityNotFoundException("client " + clientId));

        for (Mission m : c.getMissions()) {
            m.getClientsByEmail().remove(c.getEmail());
        }
        c.getMissions().clear();

        personRepo.deleteById(clientId);
    }

    public void deletePilot(Long pilotId) {
        personRepo.deleteById(pilotId);
    }

    public void deleteMedic(Long medicId) {
        personRepo.deleteById(medicId);
    }
}
