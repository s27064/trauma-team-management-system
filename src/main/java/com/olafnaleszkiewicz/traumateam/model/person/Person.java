package com.olafnaleszkiewicz.traumateam.model.person;

import com.olafnaleszkiewicz.traumateam.model.Chrome;
import com.olafnaleszkiewicz.traumateam.model.enums.RoleType;
import com.olafnaleszkiewicz.traumateam.model.Policy;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "persons")
@Getter
@Setter
@NoArgsConstructor()
@AllArgsConstructor()
@Builder
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Name cannot be blank!")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Surname cannot be blank!")
    @Column(nullable = false)
    private String surname;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private int age;

    @Embedded
    @NotNull
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Policy policy;

    @OneToMany(mappedBy = "person", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Chrome> chromes = new HashSet<>();

    // ====================================== DYNAMIC INHERITANCE
    // =====================================================

    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Client client;

    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Medic medic;

    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Pilot pilot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    public static Person asClient(String name, String surname, int age, Address address, Client clientPart) {
        Person p = Person.builder()
                .name(name).surname(surname).age(age)
                .address(address)
                .build();
        p.attachClient(clientPart);
        return p;
    }

    public static Person asMedic(String name, String surname, int age, Address address, Medic medicPart) {
        Person p = Person.builder()
                .name(name).surname(surname).age(age)
                .address(address)
                .build();
        p.attachMedic(medicPart);
        return p;
    }

    public static Person asPilot(String name, String surname, int age, Address address, Pilot pilotPart) {
        Person p = Person.builder()
                .name(name).surname(surname).age(age)
                .address(address)
                .build();
        p.attachPilot(pilotPart);
        return p;
    }

    public void switchToClient(Client newClient) {
        detachRole();
        attachClient(newClient);
    }

    public void switchToMedic(Medic newMedic) {
        detachRole();
        attachMedic(newMedic);
    }

    public void switchToPilot(Pilot newPilot) {
        detachRole();
        attachPilot(newPilot);
    }

    private void attachClient(Client c) {
        this.client = c;
        this.role = RoleType.CLIENT;
        if (c != null)
            c.setPerson(this);
    }

    private void attachMedic(Medic m) {
        this.medic = m;
        this.role = RoleType.MEDIC;
        if (m != null)
            m.setPerson(this);
    }

    private void attachPilot(Pilot p) {
        this.pilot = p;
        this.role = RoleType.PILOT;
        if (p != null)
            p.setPerson(this);
    }

    private void detachRole() {
        if (client != null) {
            client.setPerson(null);
            client = null;
        }
        if (medic != null) {
            medic.setPerson(null);
            medic = null;
        }
        if (pilot != null) {
            pilot.setPerson(null);
            pilot = null;
        }
    }

    @PrePersist
    @PreUpdate
    private void hasOneRole() {
        int parts = 0;
        if (client != null)
            parts++;
        if (medic != null)
            parts++;
        if (pilot != null)
            parts++;

        if (parts != 1 || role == null) {
            throw new IllegalStateException(
                    "Person must have exactly one role");
        }
    }

}
