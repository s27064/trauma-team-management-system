package com.olafnaleszkiewicz.traumateam.services;

import com.olafnaleszkiewicz.traumateam.model.person.*;
import com.olafnaleszkiewicz.traumateam.model.enums.TeamRank;
import com.olafnaleszkiewicz.traumateam.model.TraumaTeam;
import com.olafnaleszkiewicz.traumateam.repository.*;
import com.olafnaleszkiewicz.traumateam.service.TraumaTeamService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraumaTeamServiceTest {

    @Mock
    TraumaTeamRepository teamRepo;
    @Mock
    PilotRepository pilotRepo;
    @Mock
    MedicRepository medicRepo;
    @Mock
    AVRepository vehicleRepo;

    @InjectMocks
    TraumaTeamService service;

    @Test
    void create_savesNewTeam_withNoVehicles() {
        when(teamRepo.save(any(TraumaTeam.class))).thenAnswer(a -> a.getArgument(0));

        TraumaTeam t = service.create("TT-1", TeamRank.BASIC);

        verify(teamRepo).save(any(TraumaTeam.class));
        assertThat(t.getTeamCodename()).isEqualTo("TT-1");
        assertThat(t.getRank()).isEqualTo(TeamRank.BASIC);
        assertThat(t.getVehicles()).isEmpty();
    }

    @Test
    void addPilot_rejectsThirdPilot() {
        TraumaTeam tt = TraumaTeam.builder().id(1L).build();
        Pilot p1 = Pilot.builder().id(10L).build();
        Pilot p2 = Pilot.builder().id(11L).build();
        tt.addPilot(p1);
        tt.addPilot(p2);

        when(teamRepo.findByIdWithDependencies(1L)).thenReturn(Optional.of(tt));
        when(pilotRepo.findByIdWithDependencies(12L))
                .thenReturn(Optional.of(Pilot.builder().id(12L).build()));

        assertThatThrownBy(() -> service.addPilot(1L, 12L))
                .isInstanceOf(IllegalStateException.class);
    }
}
