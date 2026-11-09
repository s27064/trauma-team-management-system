package com.olafnaleszkiewicz.traumateam.repository;

import com.olafnaleszkiewicz.traumateam.model.TraumaTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TraumaTeamRepository extends JpaRepository<TraumaTeam, Long> {
    @Query("select t from TraumaTeam t")
    List<TraumaTeam> findAllNoDependencies();

    @Query("""
            select distinct t
            from TraumaTeam t
            left join fetch t.vehicles
            left join fetch t.medics   m
            left join fetch m.person
            left join fetch t.pilots   p
            left join fetch p.person
            left join fetch t.objectives o
            where t.id = :id
            """)
    Optional<TraumaTeam> findByIdWithDependencies(@Param("id") Long id);
}
