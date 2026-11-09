package com.olafnaleszkiewicz.traumateam.repository;

import com.olafnaleszkiewicz.traumateam.model.person.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
        Optional<Client> findByEmailIgnoreCase(String email);

        boolean existsByEmail(String email);

        @Query("select c from Client c")
        List<Client> findAllNoDependencies();

        @Query("""
                        select distinct c
                        from Client c
                        left join fetch c.person
                        left join fetch c.missions
                        where c.id = :id
                        """)
        Optional<Client> findByIdWithDependencies(@Param("id") Long id);

        @Query("""
                        select c
                          from Client c
                          left join fetch c.person
                        order by c.id
                        """)
        List<Client> findAllWithPerson();
}
