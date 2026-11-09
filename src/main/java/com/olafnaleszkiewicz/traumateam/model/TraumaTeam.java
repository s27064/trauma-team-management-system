package com.olafnaleszkiewicz.traumateam.model;

import com.olafnaleszkiewicz.traumateam.model.aerialvehicle.AerialVehicle;
import com.olafnaleszkiewicz.traumateam.model.enums.TeamRank;
import com.olafnaleszkiewicz.traumateam.model.enums.VehicleState;
import com.olafnaleszkiewicz.traumateam.model.person.Medic;
import com.olafnaleszkiewicz.traumateam.model.person.Pilot;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraumaTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    @NotBlank(message = "Team Codename cannot be blank!")
    private String teamCodename;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TeamRank rank;

    @OneToMany(mappedBy = "traumaTeam", fetch = FetchType.LAZY)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<AerialVehicle> vehicles = new HashSet<>();

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @Size(max = 4, message = "A TraumaTeam can have at most 4 medics")
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Medic> medics = new HashSet<>();

    public void addMedic(Medic m) {
        if (medics.size() >= 4)
            throw new IllegalStateException("Team already has 4 medics");
        medics.add(m);
        m.setTeam(this);
    }

    public void removeMedic(Medic m) {
        medics.remove(m);
        m.setTeam(null);
    }

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @Size(max = 2, message = "A TraumaTeam can have at most 2 pilots")
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Pilot> pilots = new HashSet<>();

    public void addPilot(Pilot p) {
        if (pilots.size() >= 2)
            throw new IllegalStateException("Team already has 2 pilots");
        pilots.add(p);
        p.setTeam(this);
    }

    public void removePilot(Pilot p) {
        pilots.remove(p);
        p.setTeam(null);
    }

    @OneToMany(mappedBy = "assignedTeam", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Objective> objectives = new HashSet<>();

    @Transient
    public boolean hasInServiceVehicle() {
        return vehicles != null && vehicles.stream().anyMatch(v -> v.getVehicleState() == VehicleState.IN_SERVICE);
    }
}
