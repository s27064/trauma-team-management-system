package com.olafnaleszkiewicz.traumateam.model.person;

import com.olafnaleszkiewicz.traumateam.model.Mission;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {
    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id")
    @ToString.Exclude
    private Person person;

    @Column(nullable = false, updatable = false)
    private LocalDate joinDate;

    @NotBlank(message = "Email cannot be blank!")
    @Column(nullable = false, unique = true)
    private String email;

    @ManyToMany(mappedBy = "clientsByEmail")
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Mission> missions = new HashSet<>();

    @Transient
    public Period getTimeSinceJoined() {
        return Period.between(joinDate, LocalDate.now());
    }

    @Override
    public String toString() {
        return "Client{" +
                "email='" + email + '\'' +
                '}';
    }
}
