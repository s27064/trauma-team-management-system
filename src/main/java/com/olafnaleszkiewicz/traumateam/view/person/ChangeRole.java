package com.olafnaleszkiewicz.traumateam.view.person;

import com.olafnaleszkiewicz.traumateam.model.enums.RoleType;
import com.olafnaleszkiewicz.traumateam.model.person.Person;
import com.olafnaleszkiewicz.traumateam.service.RoleService;
import com.olafnaleszkiewicz.traumateam.view.common.Ui;

import java.awt.Component;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

final class ChangeRole {

    static void show(Component parent, RoleService roleService, Person person, Runnable done) {
        List<RoleType> roles = Arrays.stream(RoleType.values()).filter(r -> r != person.getRole()).toList();
        RoleType role = Ui.choose(parent, "Change Role", roles, Enum::name);
        if (role == null)
            return;

        String[] values = switch (role) {
            case PILOT -> Ui.form(parent, "New Pilot Role", "Nickname");
            case MEDIC -> Ui.form(parent, "New Medic Role", "Specialisation", "Weapon certs (comma separated)");
            case CLIENT -> Ui.form(parent, "New Client Role", "Email");
        };
        if (values == null)
            return;

        Ui.run(parent, () -> {
            switch (role) {
                case PILOT -> roleService.switchToPilot(person.getId(), values[0]);
                case MEDIC -> roleService.switchToMedic(person.getId(), values[0], Ui.commaList(values[1]));
                case CLIENT -> roleService.switchToClient(person.getId(), LocalDate.now(), values[0]);
            }
            done.run();
        });
    }
}
