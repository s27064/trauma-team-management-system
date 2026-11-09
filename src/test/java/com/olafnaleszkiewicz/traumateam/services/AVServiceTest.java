package com.olafnaleszkiewicz.traumateam.services;

import com.olafnaleszkiewicz.traumateam.model.aerialvehicle.Combat;
import com.olafnaleszkiewicz.traumateam.model.aerialvehicle.Heli;
import com.olafnaleszkiewicz.traumateam.repository.*;
import com.olafnaleszkiewicz.traumateam.service.AVService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AVServiceTest {

    @Mock
    HeliRepository heliRepo;
    @Mock
    IonRepository ionRepo;
    @Mock
    CombatRepository combatRepo;
    @Mock
    MedicalRepository medicalRepo;
    @Mock
    AVRepository vehicleRepo;

    @InjectMocks
    AVService service;

    @Test
    void createHeli_savesHeli() {
        Heli h = Heli.builder().id(1L).build();
        when(heliRepo.save(any(Heli.class))).thenReturn(h);

        Heli result = service.createHeli("HX", "Base", 1000, 400);

        verify(heliRepo).save(any(Heli.class));
        assertThat(result).isSameAs(h);
    }

    @Test
    void armVehicle_linksCombatToVehicle() {
        Heli h = Heli.builder().id(5L).build();

        when(vehicleRepo.findByIdWithDependencies(5L))
                .thenReturn(Optional.of(h));
        when(combatRepo.save(any(Combat.class)))
                .thenAnswer(a -> a.getArgument(0));

        Combat c = service.armVehicle(5L, List.of("MG"));

        assertThat(c.getVehicle()).isSameAs(h);
        assertThat(h.getCombat()).isSameAs(c);
    }
}