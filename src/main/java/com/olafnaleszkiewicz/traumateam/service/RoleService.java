package com.olafnaleszkiewicz.traumateam.service;

import com.olafnaleszkiewicz.traumateam.model.Mission;
import com.olafnaleszkiewicz.traumateam.model.person.Client;
import com.olafnaleszkiewicz.traumateam.model.person.Medic;
import com.olafnaleszkiewicz.traumateam.model.person.Person;
import com.olafnaleszkiewicz.traumateam.model.person.Pilot;
import com.olafnaleszkiewicz.traumateam.repository.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final PersonRepository personRepo;

    @Transactional
    public Client switchToClient(Long personId, LocalDate joinDate, String email) {
        Person p = findPersonOrThrow(personId);

        Client client = Client.builder()
                .joinDate(joinDate)
                .email(email)
                .build();

        p.switchToClient(client);
        return p.getClient();
    }

    @Transactional
    public Medic switchToMedic(Long personId, String specialisation, List<String> weaponCertifications) {
        Person p = findPersonOrThrow(personId);
        removeFromMissions(p.getClient());

        Medic medic = Medic.builder()
                .specialisation(specialisation)
                .weaponCertifications(weaponCertifications)
                .build();

        p.switchToMedic(medic);
        return p.getMedic();
    }

    @Transactional
    public Pilot switchToPilot(Long personId, String nickname) {
        Person p = findPersonOrThrow(personId);
        removeFromMissions(p.getClient());

        Pilot pilot = Pilot.builder()
                .nickname(nickname)
                .build();

        p.switchToPilot(pilot);
        return p.getPilot();
    }

    private void removeFromMissions(Client client) {
        if (client == null)
            return;

        for (Mission m : client.getMissions()) {
            m.getClientsByEmail().remove(client.getEmail());
        }
        client.getMissions().clear();

        // Hibernate deletes the old Client before it updates mission links, so the links must be saved first.
        personRepo.flush();
    }

    private Person findPersonOrThrow(Long id) {
        return personRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person " + id + " not found"));
    }
}
