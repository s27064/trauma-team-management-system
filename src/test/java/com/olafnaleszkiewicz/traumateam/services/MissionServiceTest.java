package com.olafnaleszkiewicz.traumateam.services;

import com.olafnaleszkiewicz.traumateam.model.Mission;
import com.olafnaleszkiewicz.traumateam.model.Objective;
import com.olafnaleszkiewicz.traumateam.model.TraumaTeam;
import com.olafnaleszkiewicz.traumateam.model.enums.MissionStatus;
import com.olafnaleszkiewicz.traumateam.model.enums.ObjectiveStatus;
import com.olafnaleszkiewicz.traumateam.repository.ClientRepository;
import com.olafnaleszkiewicz.traumateam.repository.MissionRepository;
import com.olafnaleszkiewicz.traumateam.repository.ObjectiveRepository;
import com.olafnaleszkiewicz.traumateam.repository.TraumaTeamRepository;
import com.olafnaleszkiewicz.traumateam.service.MissionService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MissionServiceTest {

    @Mock
    MissionRepository missionRepo;
    @Mock
    ObjectiveRepository objRepo;
    @Mock
    ClientRepository clientRepo;
    @Mock
    TraumaTeamRepository teamRepo;

    @InjectMocks
    MissionService service;

    @Test
    void addObjective_movesReadyMissionBackToUnassigned() {
        // Arrange
        TraumaTeam team = TraumaTeam.builder().id(1L).build();
        Mission mission = Mission.builder().id(1L).missionStatus(MissionStatus.READY).build();
        Objective staffed = Objective.builder()
                .id(1L)
                .status(ObjectiveStatus.READY)
                .assignedTeam(team)
                .assignedMission(mission)
                .build();
        mission.getAssignedObjectives().add(staffed);

        when(missionRepo.findByIdWithDependencies(1L)).thenReturn(Optional.of(mission));
        when(objRepo.save(any(Objective.class))).thenAnswer(call -> call.getArgument(0));

        // Act
        service.addObjective(1L, "Secure the landing zone");

        // Assert
        assertThat(mission.getMissionStatus()).isEqualTo(MissionStatus.UNASSIGNED);
    }

    @Test
    void launchMission_rejectsObjectiveWithoutTeam() {
        // Arrange
        Mission mission = Mission.builder().id(1L).missionStatus(MissionStatus.UNASSIGNED).build();
        Objective objective = Objective.builder()
                .id(1L)
                .status(ObjectiveStatus.UNASSIGNED)
                .assignedMission(mission)
                .build();
        mission.getAssignedObjectives().add(objective);

        when(missionRepo.findByIdWithDependencies(1L)).thenReturn(Optional.of(mission));

        // Act & Assert
        assertThatThrownBy(() -> service.launchMission(1L))
                .isInstanceOf(IllegalStateException.class);
        assertThat(mission.getMissionStatus()).isEqualTo(MissionStatus.UNASSIGNED);
    }

    @Test
    void launchMission_setsMissionAndObjectivesInProgress() {
        // Arrange
        TraumaTeam team = TraumaTeam.builder().id(1L).build();
        Mission mission = Mission.builder().id(1L).missionStatus(MissionStatus.READY).build();
        Objective objective = Objective.builder()
                .id(1L)
                .status(ObjectiveStatus.READY)
                .assignedTeam(team)
                .assignedMission(mission)
                .build();
        mission.getAssignedObjectives().add(objective);

        when(missionRepo.findByIdWithDependencies(1L)).thenReturn(Optional.of(mission));

        // Act
        service.launchMission(1L);

        // Assert
        assertThat(mission.getMissionStatus()).isEqualTo(MissionStatus.IN_PROGRESS);
        assertThat(objective.getStatus()).isEqualTo(ObjectiveStatus.IN_PROGRESS);
    }

    @Test
    void updateObjectiveStatus_completesMissionWhenAllObjectivesComplete() {
        // Arrange
        TraumaTeam team = TraumaTeam.builder().id(1L).build();
        Mission mission = Mission.builder().id(1L).missionStatus(MissionStatus.IN_PROGRESS).build();
        Objective finished = Objective.builder()
                .id(1L)
                .status(ObjectiveStatus.COMPLETED)
                .assignedTeam(team)
                .assignedMission(mission)
                .build();
        Objective running = Objective.builder()
                .id(2L)
                .status(ObjectiveStatus.IN_PROGRESS)
                .assignedTeam(team)
                .assignedMission(mission)
                .build();
        mission.getAssignedObjectives().add(finished);
        mission.getAssignedObjectives().add(running);

        when(objRepo.findByIdWithDependencies(2L)).thenReturn(Optional.of(running));

        // Act
        service.updateObjectiveStatus(2L, ObjectiveStatus.COMPLETED);

        // Assert
        assertThat(mission.getMissionStatus()).isEqualTo(MissionStatus.COMPLETED);
    }

    @Test
    void updateObjectiveStatus_failsMissionWhenOneObjectiveFails() {
        // Arrange
        TraumaTeam team = TraumaTeam.builder().id(1L).build();
        Mission mission = Mission.builder().id(1L).missionStatus(MissionStatus.IN_PROGRESS).build();
        Objective finished = Objective.builder()
                .id(1L)
                .status(ObjectiveStatus.COMPLETED)
                .assignedTeam(team)
                .assignedMission(mission)
                .build();
        Objective running = Objective.builder()
                .id(2L)
                .status(ObjectiveStatus.IN_PROGRESS)
                .assignedTeam(team)
                .assignedMission(mission)
                .build();
        mission.getAssignedObjectives().add(finished);
        mission.getAssignedObjectives().add(running);

        when(objRepo.findByIdWithDependencies(2L)).thenReturn(Optional.of(running));

        // Act
        service.updateObjectiveStatus(2L, ObjectiveStatus.FAILED);

        // Assert
        assertThat(mission.getMissionStatus()).isEqualTo(MissionStatus.FAILED);
    }
}
