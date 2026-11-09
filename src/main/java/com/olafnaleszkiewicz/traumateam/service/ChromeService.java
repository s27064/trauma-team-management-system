package com.olafnaleszkiewicz.traumateam.service;

import com.olafnaleszkiewicz.traumateam.model.Chrome;
import com.olafnaleszkiewicz.traumateam.model.enums.ImplantTier;
import com.olafnaleszkiewicz.traumateam.model.person.Person;
import com.olafnaleszkiewicz.traumateam.repository.ChromeRepository;
import com.olafnaleszkiewicz.traumateam.repository.PersonRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChromeService {
    private final ChromeRepository chromeRepo;
    private final PersonRepository personRepo;

    public List<Chrome> findAll() {
        return chromeRepo.findAllWithOwner();
    }

    public Chrome getById(Long id) {
        return chromeRepo.findByIdWithDependencies(id).orElseThrow();
    }

    public Chrome addChrome(Long personId, String name, ImplantTier tier) {
        Person p = personRepo.findById(personId)
                .orElseThrow(() -> new IllegalArgumentException("Person not found"));

        Chrome c = chromeRepo.save(Chrome.builder()
                .person(p)
                .name(name)
                .implantTier(tier)
                .build());

        p.getChromes().add(c);

        return c;
    }

    public void deleteChrome(Long id) {
        chromeRepo.deleteById(id);
    }
}
