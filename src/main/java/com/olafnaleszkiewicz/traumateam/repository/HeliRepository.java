package com.olafnaleszkiewicz.traumateam.repository;

import com.olafnaleszkiewicz.traumateam.model.aerialvehicle.Heli;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HeliRepository extends JpaRepository<Heli, Long> {
    @Query("select h from Heli h")
    List<Heli> findAllNoDependencies();

    @Query("""
            select distinct h
            from Heli h
            left join fetch h.combat
            left join fetch h.medical
            left join fetch h.traumaTeam
            where h.id = :id
            """)
    Optional<Heli> findByIdWithDependencies(@Param("id") Long id);
}
