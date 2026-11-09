package com.olafnaleszkiewicz.traumateam.repository;

import com.olafnaleszkiewicz.traumateam.model.Chrome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChromeRepository extends JpaRepository<Chrome, Long> {
    @Query("select c from Chrome c")
    List<Chrome> findAllNoDependencies();

    @Query("""
            select distinct c
            from Chrome c
            left join fetch c.person
            where c.id = :id
            """)
    Optional<Chrome> findByIdWithDependencies(@Param("id") Long id);

    @Query("""
            select c
            from Chrome c
            left join fetch c.person
            order by c.id
            """)
    List<Chrome> findAllWithOwner();
}
