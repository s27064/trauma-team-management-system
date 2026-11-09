package com.olafnaleszkiewicz.traumateam.view.person;

import com.olafnaleszkiewicz.traumateam.model.person.Address;
import com.olafnaleszkiewicz.traumateam.service.PersonService;

import org.springframework.dao.DataIntegrityViolationException;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class NewClientDialog extends JDialog {
    private final JTextField nameField = new JTextField(15);
    private final JTextField surnameField = new JTextField(15);
    private final JTextField ageField = new JTextField(3);
    private final JTextField countryField = new JTextField(10);
    private final JTextField cityField = new JTextField(10);
    private final JTextField streetField = new JTextField(15);
    private final JTextField emailField = new JTextField(15);
    private boolean succeeded;
    private final PersonService ps;

    public NewClientDialog(Window owner, PersonService ps) {
        super(owner, "Register New Client", ModalityType.APPLICATION_MODAL);
        this.ps = ps;

        JPanel form = new JPanel(new GridLayout(7, 2, 5, 5));
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Surname:"));
        form.add(surnameField);
        form.add(new JLabel("Age:"));
        form.add(ageField);
        form.add(new JLabel("Country:"));
        form.add(countryField);
        form.add(new JLabel("City:"));
        form.add(cityField);
        form.add(new JLabel("Street:"));
        form.add(streetField);
        form.add(new JLabel("Email:"));
        form.add(emailField);

        JButton ok = new JButton("OK"), cancel = new JButton("Cancel");
        ok.addActionListener(e -> onOK());
        cancel.addActionListener(e -> dispose());

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(ok);
        btns.add(cancel);

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(btns, BorderLayout.SOUTH);
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        pack();
        setLocationRelativeTo(owner);
    }

    private void onOK() {
        try {
            String name = nameField.getText().trim();
            String surname = surnameField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());
            Address addr = Address.builder()
                    .country(countryField.getText().trim())
                    .city(cityField.getText().trim())
                    .street(streetField.getText().trim())
                    .build();
            String email = emailField.getText().trim();
            LocalDate join = LocalDate.now();

            if (name.isEmpty() || surname.isEmpty() || email.isEmpty()) {
                throw new IllegalArgumentException("Required fields missing");
            }
            if (age < 0) {
                throw new IllegalArgumentException("Age cannot be negative");
            }

            ps.addClient(name, surname, age, addr, email, join);
            succeeded = true;
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a number", "Validation", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.ERROR_MESSAGE);
        } catch (DataIntegrityViolationException ex) {
            JOptionPane.showMessageDialog(this, "A client with that email already exists", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}
