package com.olafnaleszkiewicz.traumateam.service;

import com.olafnaleszkiewicz.traumateam.model.aerialvehicle.AerialVehicle;
import com.olafnaleszkiewicz.traumateam.model.enums.ObjectiveStatus;
import com.olafnaleszkiewicz.traumateam.model.enums.TeamRank;
import com.olafnaleszkiewicz.traumateam.model.enums.VehicleState;
import com.olafnaleszkiewicz.traumateam.model.person.Medic;
import com.olafnaleszkiewicz.traumateam.model.person.Pilot;
import com.olafnaleszkiewicz.traumateam.model.TraumaTeam;
import com.olafnaleszkiewicz.traumateam.repository.AVRepository;
import com.olafnaleszkiewicz.traumateam.repository.MedicRepository;
import com.olafnaleszkiewicz.traumateam.repository.PilotRepository;
import com.olafnaleszkiewicz.traumateam.repository.TraumaTeamRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TraumaTeamService {
    private final TraumaTeamRepository teamRepo;
    private final PilotRepository pilotRepo;
    private final MedicRepository medicRepo;
    private final AVRepository vehicleRepo;

    public List<TraumaTeam> findAll() {
        return teamRepo.findAllNoDependencies();
    }

    public TraumaTeam getById(Long id) {
        return teamRepo.findByIdWithDependencies(id)
                .orElseThrow();
    }

    public TraumaTeam create(String codeName, TeamRank rank) {
        return teamRepo.save(TraumaTeam.builder()
                .teamCodename(codeName)
                .rank(rank)
                .build());
    }

    public void addPilot(Long teamId, Long pilotId) {
        TraumaTeam t = getById(teamId);
        Pilot p = pilotRepo.findByIdWithDependencies(pilotId).orElseThrow();

        t.addPilot(p);
    }

    public void addMedic(Long teamId, Long medicId) {
        TraumaTeam t = getById(teamId);
        Medic m = medicRepo.findByIdWithDependencies(medicId).orElseThrow();

        t.addMedic(m);
    }

    public void assignVehicle(Long teamId, Long vehicleId) {
        TraumaTeam team = getById(teamId);
        AerialVehicle vehicle = vehicleRepo.findByIdWithDependencies(vehicleId)
                .orElseThrow();

        if (vehicle.getTraumaTeam() != null && vehicle.getTraumaTeam() != team)
            throw new IllegalStateException("Vehicle already assigned to another team");

        if (vehicle.getVehicleState() != VehicleState.AVAILABLE)
            throw new IllegalStateException(
                    "Vehicle must be AVAILABLE to be assigned; current state is "
                            + vehicle.getVehicleState());

        vehicle.setVehicleState(VehicleState.IN_SERVICE);
        team.getVehicles().add(vehicle);
        vehicle.setTraumaTeam(team);
    }

    public void removePilot(Long teamId, Long pilotId) {
        TraumaTeam t = getById(teamId);
        Pilot p = pilotRepo.findByIdWithDependencies(pilotId).orElseThrow();

        t.removePilot(p);
    }

    public void removeMedic(Long teamId, Long medicId) {
        TraumaTeam t = getById(teamId);
        Medic m = medicRepo.findByIdWithDependencies(medicId).orElseThrow();

        t.removeMedic(m);
    }

    public void removeVehicle(Long teamId, Long vehicleId) {
        TraumaTeam team = getById(teamId);
        AerialVehicle vehicle = vehicleRepo.findByIdWithDependencies(vehicleId)
                .orElseThrow();

        long inService = team.getVehicles().stream()
                .filter(v -> v.getVehicleState() == VehicleState.IN_SERVICE)
                .count();
        boolean hasUnfinishedObjectives = team.getObjectives().stream()
                .anyMatch(o -> o.getStatus() != ObjectiveStatus.COMPLETED && o.getStatus() != ObjectiveStatus.FAILED);

        if (vehicle.getVehicleState() == VehicleState.IN_SERVICE && inService == 1 && hasUnfinishedObjectives)
            throw new IllegalStateException(
                    "This is the team's last vehicle in service and the team still has unfinished objectives");

        team.getVehicles().remove(vehicle);
        vehicle.setTraumaTeam(null);
        if (vehicle.getVehicleState() == VehicleState.IN_SERVICE)
            vehicle.setVehicleState(VehicleState.AVAILABLE);
    }
}
