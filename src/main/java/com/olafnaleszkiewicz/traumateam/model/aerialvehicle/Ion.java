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
@DiscriminatorValue("ION")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Ion extends AerialVehicle {
    @PositiveOrZero
    @Column(nullable = true)
    @NotNull
    private double maxAltitudeM;

    @PositiveOrZero
    @Column(nullable = true)
    @NotNull
    private double maxSpeedKmh;
}
