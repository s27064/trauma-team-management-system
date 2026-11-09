package com.olafnaleszkiewicz.traumateam.model.enums;

import jakarta.validation.constraints.PositiveOrZero;

public enum PolicyTier {

    BRONZE(1.0), // ×1
    SILVER(2.0), // ×2
    GOLD(5.0), // ×5
    PLATINUM(10.0), // ×10
    EMPLOYEE(0.5); // ×0.5

    @PositiveOrZero
    private final double multiplier;

    PolicyTier(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
