package com.olafnaleszkiewicz.traumateam.repository;

import com.olafnaleszkiewicz.traumateam.model.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MissionRepository extends JpaRepository<Mission, Long> {
        @Query("""
                        select m
                          from Mission m
                          join m.clientsByEmail c
                         where value(c).id = :clientId
                        """)
        List<Mission> findAllReferencingClient(@Param("clientId") Long id);

        @Query("select m from Mission m")
        List<Mission> findAllNoDependencies();

        @Query("""
                        select distinct m
                        from Mission m
                        left join fetch m.clientsByEmail c
                        left join fetch c.person
                        where m.id = :id
                        """)
        Optional<Mission> findByIdWithDependencies(@Param("id") Long id);
}
