package com.olafnaleszkiewicz.traumateam.model;

import com.olafnaleszkiewicz.traumateam.model.enums.MissionStatus;
import com.olafnaleszkiewicz.traumateam.model.person.Client;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "Description cannot be blank")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionStatus missionStatus;

    @OneToMany(mappedBy = "assignedMission", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Objective> assignedObjectives = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "mission_client", joinColumns = @JoinColumn(name = "mission_id"), inverseJoinColumns = @JoinColumn(name = "client_id"))
    @MapKey(name = "email")
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Map<String, Client> clientsByEmail = new HashMap<>();
}
