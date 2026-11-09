package com.olafnaleszkiewicz.traumateam.model.person;

import com.olafnaleszkiewicz.traumateam.model.TraumaTeam;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medic {
    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id")
    @ToString.Exclude
    private Person person;

    @NotBlank(message = "Specialisation cannot be blank but can be null!")
    private String specialisation;

    @ElementCollection
    @CollectionTable(name = "medic_weapon_cert", joinColumns = @JoinColumn(name = "medic_id"))
    @Builder.Default
    private List<@NotBlank String> weaponCertifications = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TraumaTeam team;
}
