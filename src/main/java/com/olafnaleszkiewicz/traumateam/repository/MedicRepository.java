package com.olafnaleszkiewicz.traumateam.repository;

import com.olafnaleszkiewicz.traumateam.model.person.Medic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MedicRepository extends JpaRepository<Medic, Long> {
    @Query("select m from Medic m")
    List<Medic> findAllNoDependencies();

    @Query("""
            select distinct m
            from Medic m
            left join fetch m.person
            left join fetch m.team
            left join fetch m.weaponCertifications
            where m.id = :id
            """)
    Optional<Medic> findByIdWithDependencies(@Param("id") Long id);
}
