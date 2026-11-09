package com.olafnaleszkiewicz.traumateam.repository;

import com.olafnaleszkiewicz.traumateam.model.aerialvehicle.Medical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MedicalRepository extends JpaRepository<Medical, Long> {
    @Query("select m from Medical m")
    List<Medical> findAllNoDependencies();

    @Query("""
            select m
            from Medical m
            left join fetch m.vehicle
            where m.id = :id
            """)
    Optional<Medical> findByIdWithDependencies(@Param("id") Long id);
}
