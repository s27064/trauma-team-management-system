package com.olafnaleszkiewicz.traumateam.repository;

import com.olafnaleszkiewicz.traumateam.model.aerialvehicle.Combat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CombatRepository extends JpaRepository<Combat, Long> {
    @Query("select c from Combat c")
    List<Combat> findAllNoDependencies();

    @Query("""
            select c
            from Combat c
            left join fetch c.vehicle
            where c.id = :id
            """)
    Optional<Combat> findByIdWithDependencies(@Param("id") Long id);
}
