package com.olafnaleszkiewicz.traumateam.repository;

import com.olafnaleszkiewicz.traumateam.model.person.Pilot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PilotRepository extends JpaRepository<Pilot, Long> {
    @Query("select p from Pilot p")
    List<Pilot> findAllNoDependencies();

    @Query("""
            select distinct p
            from Pilot p
            left join fetch p.person
            left join fetch p.team
            where p.id = :id
            """)
    Optional<Pilot> findByIdWithDependencies(@Param("id") Long id);
}