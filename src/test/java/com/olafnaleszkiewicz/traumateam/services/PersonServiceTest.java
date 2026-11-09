package com.olafnaleszkiewicz.traumateam.services;

import com.olafnaleszkiewicz.traumateam.model.enums.RoleType;
import com.olafnaleszkiewicz.traumateam.model.person.*;
import com.olafnaleszkiewicz.traumateam.repository.*;
import com.olafnaleszkiewicz.traumateam.service.PersonService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

        @Mock
        PersonRepository personRepo;
        @Mock
        ClientRepository clientRepo;

        @InjectMocks
        PersonService service;

        @Test
        void addPilot_persistsPerson_andPilot() {
                when(personRepo.save(any(Person.class)))
                                .thenAnswer(a -> {
                                        Person p = a.getArgument(0);
                                        p.setId(1L);
                                        return p;
                                });

                Pilot result = service.addPilot(
                                "Tobin", "Ashgrove", 22,
                                new Address("PL", "WAW", "Letnia"),
                                "Skylark");

                verify(personRepo).save(any(Person.class));

                assertThat(result.getNickname()).isEqualTo("Skylark");
                assertThat(result.getPerson().getRole()).isEqualTo(RoleType.PILOT);
        }

        @Test
        void addMedic_persistsPerson_andMedic() {
                when(personRepo.save(any(Person.class)))
                                .thenAnswer(a -> {
                                        Person p = a.getArgument(0);
                                        p.setId(2L);
                                        return p;
                                });

                Medic m = service.addMedic("Vic", "Vector", 45,
                                new Address("PL", "POZ", "Zimowa"),
                                "Surgeon",
                                List.of("Pistol"));

                assertThat(m.getPerson().getRole()).isEqualTo(RoleType.MEDIC);
                assertThat(m.getWeaponCertifications()).containsExactly("Pistol");
        }

        @Test
        void addClient_persistsPerson_andClient() {
                when(personRepo.save(any(Person.class)))
                                .thenAnswer(a -> {
                                        Person p = a.getArgument(0);
                                        p.setId(3L);
                                        return p;
                                });

                Client c = service.addClient(
                                "Eve", "Parker", 30,
                                new Address("PL", "KRK", "Jesienna"),
                                "eve@net", LocalDate.now());

                assertThat(c.getPerson().getRole()).isEqualTo(RoleType.CLIENT);
                assertThat(c.getEmail()).isEqualTo("eve@net");
        }
}