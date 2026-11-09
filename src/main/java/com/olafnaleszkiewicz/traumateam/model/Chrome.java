package com.olafnaleszkiewicz.traumateam.model;

import com.olafnaleszkiewicz.traumateam.model.enums.ImplantTier;
import com.olafnaleszkiewicz.traumateam.model.person.Person;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chrome {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImplantTier implantTier;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Person person;
}
