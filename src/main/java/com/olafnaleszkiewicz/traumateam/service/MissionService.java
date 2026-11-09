package com.olafnaleszkiewicz.traumateam.service;

import com.olafnaleszkiewicz.traumateam.model.enums.MissionStatus;
import com.olafnaleszkiewicz.traumateam.model.enums.ObjectiveStatus;
import com.olafnaleszkiewicz.traumateam.model.Mission;
import com.olafnaleszkiewicz.traumateam.model.Objective;
import com.olafnaleszkiewicz.traumateam.model.person.Client;
import com.olafnaleszkiewicz.traumateam.model.TraumaTeam;
import com.olafnaleszkiewicz.traumateam.repository.ClientRepository;
import com.olafnaleszkiewicz.traumateam.repository.MissionRepository;
import com.olafnaleszkiewicz.traumateam.repository.ObjectiveRepository;
import com.olafnaleszkiewicz.traumateam.repository.TraumaTeamRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MissionService {
    private final MissionRepository missionRepo;
    private final ObjectiveRepository objRepo;
    private final ClientRepository clientRepo;
    private final TraumaTeamRepository teamRepo;

    public List<Mission> findAll() {
        return missionRepo.findAllNoDependencies();
    }

    public Mission getById(Long id) {
        return missionRepo.findByIdWithDependencies(id)
                .orElseThrow(() -> new IllegalArgumentException("Mission not found"));
    }

    @Transactional()
    public List<Client> findAllClients() {
        return clientRepo.findAllNoDependencies();
    }

    public Mission create(String name, String desc) {
        Mission m = Mission.builder()
                .name(name)
                .description(desc)
                .missionStatus(MissionStatus.UNSPECIFIED)
                .build();

        return missionRepo.save(m);
    }

    public List<Objective> findObjectives(Long missionId) {
        return objRepo.findAllByMissionWithTeam(missionId);
    }

    public Objective addObjective(Long missionId, String desc) {
        Mission m = getById(missionId);

        Objective o = objRepo.save(Objective.builder()
                .assignedMission(m)
                .description(desc)
                .status(ObjectiveStatus.UNASSIGNED)
                .build());

        m.getAssignedObjectives().add(o);
        recalcMissionStatus(m);
        return o;
    }

    public void assignTeam(Long missionId, Long objectiveId, Long teamId) {
        Objective o = objRepo.findByIdWithDependencies(objectiveId)
                .orElseThrow(() -> new IllegalArgumentException("Objective not found"));
        if (!o.getAssignedMission().getId().equals(missionId))
            throw new IllegalArgumentException("Objective does not belong to this mission");

        TraumaTeam t = teamRepo.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        if (!t.hasInServiceVehicle())
            throw new IllegalStateException("Team \"" + t.getTeamCodename()
                    + "\" must have at least one vehicle in state IN_SERVICE before it can be assigned to a mission");

        o.setStatus(ObjectiveStatus.READY);
        o.setAssignedTeam(t);

        recalcMissionStatus(o.getAssignedMission());
    }

    public void launchMission(Long missionId) {
        Mission m = getById(missionId);
        if (m.getAssignedObjectives().isEmpty() || m.getAssignedObjectives()
                .stream()
                .anyMatch(o -> o.getAssignedTeam() == null)) {
            throw new IllegalStateException("Cannot launch: not all objectives are staffed");
        }

        m.getAssignedObjectives().forEach(o -> o.setStatus(ObjectiveStatus.IN_PROGRESS));
        m.setMissionStatus(MissionStatus.IN_PROGRESS);
    }

    public void updateObjectiveStatus(Long objectiveId, ObjectiveStatus newStatus) {
        Objective o = objRepo.findByIdWithDependencies(objectiveId)
                .orElseThrow(() -> new IllegalArgumentException("Objective not found"));

        o.setStatus(newStatus);
        recalcMissionStatus(o.getAssignedMission());
    }

    private void recalcMissionStatus(Mission m) {
        if (!m.getAssignedObjectives().isEmpty() && m.getMissionStatus() == MissionStatus.UNSPECIFIED) {
            m.setMissionStatus(MissionStatus.UNASSIGNED);
            return;
        }

        boolean allStaffed = m.getAssignedObjectives().stream().allMatch(o -> o.getAssignedTeam() != null);
        if (allStaffed && m.getMissionStatus() == MissionStatus.UNASSIGNED) {
            m.setMissionStatus(MissionStatus.READY);
            return;
        }

        if (!allStaffed && m.getMissionStatus() == MissionStatus.READY) {
            m.setMissionStatus(MissionStatus.UNASSIGNED);
            return;
        }

        if (m.getMissionStatus() == MissionStatus.IN_PROGRESS) {
            boolean allDone = m.getAssignedObjectives()
                    .stream()
                    .allMatch(
                            o -> o.getStatus() == ObjectiveStatus.COMPLETED || o.getStatus() == ObjectiveStatus.FAILED);

            if (allDone) {
                long countFailed = m.getAssignedObjectives()
                        .stream()
                        .filter(o -> o.getStatus() == ObjectiveStatus.FAILED)
                        .count();

                m.setMissionStatus(countFailed > 0 ? MissionStatus.FAILED : MissionStatus.COMPLETED);
            }
        }
    }

    public void addClient(Long missionId, Long clientId) {
        Mission m = getById(missionId);
        Client cl = clientRepo.findByIdWithDependencies(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        if (m.getClientsByEmail().get(cl.getEmail()) != null)
            throw new IllegalStateException("This Client already assigned to this mission!");

        m.getClientsByEmail().put(cl.getEmail(), cl);
    }

    public void removeClient(Long missionId, Long clientId) {
        Mission m = getById(missionId);
        Client cl = clientRepo.findByIdWithDependencies(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));
        m.getClientsByEmail().remove(cl.getEmail(), cl);
    }

    public List<Mission> findAllReferencingClient(Long id) {
        return missionRepo.findAllReferencingClient(id);
    }

    public void deleteMission(Long missionId) {
        missionRepo.deleteById(missionId);
    }
}
