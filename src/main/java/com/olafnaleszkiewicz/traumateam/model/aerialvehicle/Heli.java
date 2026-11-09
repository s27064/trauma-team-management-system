package com.olafnaleszkiewicz.traumateam.model.aerialvehicle;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("HELI")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Heli extends AerialVehicle {
    @PositiveOrZero
    @Column(nullable = true)
    @NotNull
    private int horsePower;

    @PositiveOrZero
    @Column(nullable = true)
    @NotNull
    private double rangeKm;
}
