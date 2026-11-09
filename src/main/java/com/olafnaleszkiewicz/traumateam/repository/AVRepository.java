package com.olafnaleszkiewicz.traumateam.repository;

import com.olafnaleszkiewicz.traumateam.model.aerialvehicle.AerialVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AVRepository extends JpaRepository<AerialVehicle, Long> {
    @Query("select v from AerialVehicle v")
    List<AerialVehicle> findAllNoDependencies();

    @Query("""
            select distinct v
            from AerialVehicle v
            left join fetch v.combat
            left join fetch v.medical
            left join fetch v.traumaTeam
            where v.id = :id
            """)
    Optional<AerialVehicle> findByIdWithDependencies(@Param("id") Long id);

    @Query("""
            select distinct v
            from AerialVehicle v
            left join fetch v.combat
            left join fetch v.medical
            left join fetch v.traumaTeam
            order by v.id
            """)
    List<AerialVehicle> findAllWithDependencies();
}
