package com.olafnaleszkiewicz.traumateam.model.aerialvehicle;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Combat {
    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id")
    private AerialVehicle vehicle;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "combat_armament", joinColumns = @JoinColumn(name = "vehicle_id"))
    @Column(name = "armament")
    @Builder.Default
    @Size(min = 1, message = "must have at least one weapon")
    private List<@NotBlank String> armaments = new ArrayList<>();
}