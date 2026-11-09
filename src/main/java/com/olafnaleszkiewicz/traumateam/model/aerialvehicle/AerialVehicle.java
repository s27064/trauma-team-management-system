package com.olafnaleszkiewicz.traumateam.model.aerialvehicle;

import com.olafnaleszkiewicz.traumateam.model.enums.VehicleState;
import com.olafnaleszkiewicz.traumateam.model.TraumaTeam;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "aerial_vehicle")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "vehicle_type", discriminatorType = DiscriminatorType.STRING)
public abstract class AerialVehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank
    private String serialNumber;

    @Column(nullable = false)
    @NotBlank
    private String homeBase;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private VehicleState vehicleState = VehicleState.AVAILABLE;

    @Transient
    public boolean isCombat() {
        return combat != null;
    }

    @Transient
    public boolean isMedical() {
        return medical != null;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trauma_team_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private TraumaTeam traumaTeam;

    @OneToOne(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Combat combat;

    @OneToOne(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Medical medical;
}
