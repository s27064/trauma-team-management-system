package com.olafnaleszkiewicz.traumateam.model;

import com.olafnaleszkiewicz.traumateam.model.enums.PolicyTier;
import com.olafnaleszkiewicz.traumateam.model.person.Person;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public static final double BASE_MONTHLY_RATE = 1_000.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyTier policyTier;

    @Transient
    public double getCurrentPrice() {
        return BASE_MONTHLY_RATE * policyTier.getMultiplier();
    }

    @OneToMany(mappedBy = "policy", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Person> people = new HashSet<>();
}
