package com.olafnaleszkiewicz.traumateam.repository;

import com.olafnaleszkiewicz.traumateam.model.aerialvehicle.Ion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IonRepository extends JpaRepository<Ion, Long> {
    @Query("select i from Ion i")
    List<Ion> findAllNoDependencies();

    @Query("""
            select distinct i
            from Ion i
            left join fetch i.combat
            left join fetch i.medical
            left join fetch i.traumaTeam
            where i.id = :id
            """)
    Optional<Ion> findByIdWithDependencies(@Param("id") Long id);
}
