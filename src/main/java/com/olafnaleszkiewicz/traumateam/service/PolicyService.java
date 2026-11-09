package com.olafnaleszkiewicz.traumateam.service;

import com.olafnaleszkiewicz.traumateam.model.enums.PolicyTier;
import com.olafnaleszkiewicz.traumateam.model.person.Person;
import com.olafnaleszkiewicz.traumateam.model.Policy;
import com.olafnaleszkiewicz.traumateam.repository.PersonRepository;
import com.olafnaleszkiewicz.traumateam.repository.PolicyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PolicyService {
    private final PolicyRepository policyRepo;
    private final PersonRepository personRepo;

    public List<Policy> findAll() {
        return policyRepo.findAllNoDependencies();
    }

    public Policy getById(Long id) {
        return policyRepo.findByIdWithDependencies(id).orElseThrow();
    }

    public Policy create(PolicyTier tier) {
        return policyRepo.save(Policy.builder().policyTier(tier).build());
    }

    public Policy assignPolicy(Long personId, Long policyId) {
        Person p = personRepo.findById(personId)
                .orElseThrow(() -> new IllegalArgumentException("Person not found: " + personId));

        Policy pol = policyRepo.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("Policy not found: " + policyId));

        p.setPolicy(pol);
        pol.getPeople().add(p);

        return pol;
    }

    public void removeFromPolicy(Long personId) {
        Person p = personRepo.findById(personId)
                .orElseThrow(() -> new IllegalArgumentException("Person not found: " + personId));

        p.setPolicy(null);
    }

    public void deletePolicy(Long policyId) {
        Policy pol = policyRepo.findByIdWithDependencies(policyId).orElseThrow();

        for (Person p : pol.getPeople()) {
            p.setPolicy(null);
        }
        policyRepo.delete(pol);
    }
}
