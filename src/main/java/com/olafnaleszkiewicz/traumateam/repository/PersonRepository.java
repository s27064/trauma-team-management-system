package com.olafnaleszkiewicz.traumateam.repository;

import com.olafnaleszkiewicz.traumateam.model.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query("select p from Person p")
    List<Person> findAllNoDependencies();

    @Query("""
            select distinct p
            from Person p
            left join fetch p.policy
            left join fetch p.chromes
            where p.id = :id
            """)
    Optional<Person> findByIdWithDependencies(@Param("id") Long id);

    @Query("""
            select distinct p
            from Person p
            left join fetch p.pilot pilot
            left join fetch pilot.team
            left join fetch p.medic medic
            left join fetch medic.team
            left join fetch medic.weaponCertifications
            where pilot.id is not null or medic.id is not null
            order by p.id
            """)
    List<Person> findAllCrew();
}
