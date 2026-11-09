package com.olafnaleszkiewicz.traumateam.model;

import com.olafnaleszkiewicz.traumateam.model.enums.ObjectiveStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Objective {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    @NotBlank(message = "Description cannot be blank!")
    private String description;

    @Column(nullable = false)
    @NotNull(message = "Objective status cannot be blank!")
    @Enumerated(EnumType.STRING)
    private ObjectiveStatus status;

    @ManyToOne
    @JoinColumn(name = "team_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private TraumaTeam assignedTeam;

    @ManyToOne
    @JoinColumn(name = "mission_id", nullable = false)
    @NotNull(message = "Mission cannot be null!")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Mission assignedMission;
}
