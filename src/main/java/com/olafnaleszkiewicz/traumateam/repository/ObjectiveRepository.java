package com.olafnaleszkiewicz.traumateam.repository;

import com.olafnaleszkiewicz.traumateam.model.Objective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ObjectiveRepository extends JpaRepository<Objective, Long> {
    @Query("select o from Objective o")
    List<Objective> findAllNoDependencies();

    @Query("""
            select o
            from Objective o
            left join fetch o.assignedMission
            left join fetch o.assignedTeam
            where o.id = :id
            """)
    Optional<Objective> findByIdWithDependencies(@Param("id") Long id);

    @Query("""
            select o
            from Objective o
            left join fetch o.assignedTeam
            where o.assignedMission.id = :missionId
            order by o.id
            """)
    List<Objective> findAllByMissionWithTeam(@Param("missionId") Long missionId);
}
