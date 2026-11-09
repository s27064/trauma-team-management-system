package com.olafnaleszkiewicz.traumateam.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.olafnaleszkiewicz.traumateam.view.common.ThemeToggleButton;
import com.olafnaleszkiewicz.traumateam.view.mission.MissionsPanel;
import com.olafnaleszkiewicz.traumateam.view.person.ClientsPanel;
import com.olafnaleszkiewicz.traumateam.view.person.CrewPanel;
import com.olafnaleszkiewicz.traumateam.view.person.ImplantsPanel;
import com.olafnaleszkiewicz.traumateam.view.person.PoliciesPanel;
import com.olafnaleszkiewicz.traumateam.view.team.TeamsPanel;
import com.olafnaleszkiewicz.traumateam.view.team.VehiclesPanel;
import org.springframework.context.ApplicationContext;

import com.olafnaleszkiewicz.traumateam.service.AVService;
import com.olafnaleszkiewicz.traumateam.service.ChromeService;
import com.olafnaleszkiewicz.traumateam.service.MissionService;
import com.olafnaleszkiewicz.traumateam.service.PersonService;
import com.olafnaleszkiewicz.traumateam.service.PolicyService;
import com.olafnaleszkiewicz.traumateam.service.RoleService;
import com.olafnaleszkiewicz.traumateam.service.TraumaTeamService;

import javax.swing.*;
import java.awt.BorderLayout;

public class MainFrame extends JFrame {
    public MainFrame(ApplicationContext ctx) {
        super("Trauma Team");

        JTabbedPane tabs = new JTabbedPane();

        PersonService personService = ctx.getBean(PersonService.class);
        MissionService missionService = ctx.getBean(MissionService.class);
        RoleService roleService = ctx.getBean(RoleService.class);
        TraumaTeamService teamService = ctx.getBean(TraumaTeamService.class);
        AVService vehicleService = ctx.getBean(AVService.class);

        tabs.addTab("Clients", new ClientsPanel(personService, missionService, roleService));
        tabs.addTab("Missions", new MissionsPanel(missionService, teamService));
        tabs.addTab("Teams", new TeamsPanel(teamService, personService, vehicleService));
        tabs.addTab("Vehicles", new VehiclesPanel(vehicleService));
        tabs.addTab("Crew", new CrewPanel(personService, roleService));
        tabs.addTab("Policies", new PoliciesPanel(ctx.getBean(PolicyService.class), personService));
        tabs.addTab("Implants", new ImplantsPanel(ctx.getBean(ChromeService.class), personService));

        JPanel trailing = new JPanel(new BorderLayout());
        trailing.setOpaque(false);
        trailing.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        trailing.add(ThemeToggleButton.create(), BorderLayout.EAST);
        tabs.putClientProperty(FlatClientProperties.TABBED_PANE_TRAILING_COMPONENT, trailing);

        add(tabs);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1150, 760);
        setLocationRelativeTo(null);
    }
}
