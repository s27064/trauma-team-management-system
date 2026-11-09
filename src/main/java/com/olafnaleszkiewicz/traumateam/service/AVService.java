package com.olafnaleszkiewicz.traumateam.service;

import com.olafnaleszkiewicz.traumateam.model.aerialvehicle.*;
import com.olafnaleszkiewicz.traumateam.model.enums.VehicleState;
import com.olafnaleszkiewicz.traumateam.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AVService {
    private final HeliRepository heliRepo;
    private final IonRepository ionRepo;
    private final CombatRepository combatRepo;
    private final MedicalRepository medicalRepo;
    private final AVRepository vehicleRepo;

    public List<AerialVehicle> findAll() {
        return vehicleRepo.findAllWithDependencies();
    }

    public Heli createHeli(String sn, String base, int hp, double rangeKm) {
        return heliRepo.save(Heli.builder()
                .serialNumber(sn)
                .homeBase(base)
                .horsePower(hp)
                .rangeKm(rangeKm)
                .build());
    }

    public Ion createIon(String sn, String base, double maxAlt, double maxSpd) {
        return ionRepo.save(Ion.builder()
                .serialNumber(sn)
                .homeBase(base)
                .maxAltitudeM(maxAlt)
                .maxSpeedKmh(maxSpd)
                .build());
    }

    public Combat armVehicle(Long vehicleId, List<String> weapons) {
        AerialVehicle v = vehicleRepo.findByIdWithDependencies(vehicleId)
                .orElseThrow();

        if (v.isCombat()) {
            throw new IllegalStateException("Vehicle " + vehicleId + " already has a combat module");
        }

        Combat c = combatRepo.save(Combat.builder()
                .vehicle(v)
                .armaments(weapons)
                .build());
        v.setCombat(c);
        return c;
    }

    public Medical addMedicalModule(Long vehicleId, int capacity) {
        AerialVehicle v = vehicleRepo.findByIdWithDependencies(vehicleId)
                .orElseThrow();

        if (v.isMedical()) {
            throw new IllegalStateException("Vehicle " + vehicleId + " already has a medical module");
        }

        Medical m = medicalRepo.save(
                Medical.builder()
                        .vehicle(v)
                        .clientCapacity(capacity)
                        .build());
        v.setMedical(m);
        return m;
    }

    public void updateVehicleState(Long vehicleID, VehicleState state) {
        AerialVehicle v = vehicleRepo.findByIdWithDependencies(vehicleID)
                .orElseThrow();

        if (v.getVehicleState() == state)
            throw new IllegalArgumentException("This vehicle already has that State!");

        v.setVehicleState(state);
    }

    public void updateWeapons(Long vehicleId, List<String> weapons) {
        AerialVehicle v = vehicleRepo.findByIdWithDependencies(vehicleId)
                .orElseThrow();

        v.getCombat().setArmaments(weapons);
    }

    public void removeWeapons(Long vehicleId) {
        AerialVehicle v = vehicleRepo.findByIdWithDependencies(vehicleId)
                .orElseThrow();

        v.setCombat(null);
    }

    public void removeMedicalModule(Long vehicleId) {
        AerialVehicle v = vehicleRepo.findByIdWithDependencies(vehicleId)
                .orElseThrow();

        v.setMedical(null);
    }
}
