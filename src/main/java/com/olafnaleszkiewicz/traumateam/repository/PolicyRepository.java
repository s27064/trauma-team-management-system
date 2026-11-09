package com.olafnaleszkiewicz.traumateam.repository;

import com.olafnaleszkiewicz.traumateam.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    @Query("from Policy as p left join fetch p.people where p.id = :id")
    public Optional<Policy> findById(@Param("id") Long id);

    @Query("select p from Policy p")
    List<Policy> findAllNoDependencies();

    @Query("""
            select distinct p
            from Policy p
            left join fetch p.people
            where p.id = :id
            """)
    Optional<Policy> findByIdWithDependencies(@Param("id") Long id);
}
