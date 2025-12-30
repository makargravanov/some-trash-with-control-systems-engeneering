package ru.jetlabs.core.gui;

import ru.jetlabs.core.environment.Level;
import ru.jetlabs.core.objects.Actor;
import ru.jetlabs.core.objects.entities.SpaceShip;
import ru.jetlabs.core.objects.sensors.Contact;
import ru.jetlabs.core.objects.sensors.ContactType;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class RadarContactPanel extends JPanel {
    private final GroupSelection selection;
    private final Level level;
    private SpaceShip referenceShip;  // чей радар показывать
    private final JList<ContactEntry> contactList;

    public RadarContactPanel(GroupSelection selection, Level level) {
        this.selection = selection;
        this.level = level;
        this.contactList = new JList<>();
        this.referenceShip = null;

        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, 200));

        contactList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                        boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ContactEntry entry) {
                    setText(entry.toString());
                    // Цвет по типу контакта
                    if (entry.contact().type() == ContactType.ACTIVE) {
                        setForeground(new Color(100, 255, 100));  // зеленый
                    } else {
                        setForeground(new Color(255, 62, 2));
                    }
                }
                return this;
            }
        });

        // Двойной клик - атаковать контакт
        contactList.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    ContactEntry entry = contactList.getSelectedValue();
                    if (entry != null) {
                        selection.attackContact(entry.contact(), level);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(contactList);
        add(scrollPane, BorderLayout.CENTER);

        JLabel label = new JLabel("Radar Contacts (double-click to attack)");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);
    }

    public void setReferenceShip(SpaceShip ship) {
        this.referenceShip = ship;
    }

    public void updateContacts() {
        if (referenceShip == null || referenceShip.sensorSystem == null) {
            contactList.setModel(new DefaultListModel<>());
            return;
        }

        List<Contact> contacts = referenceShip.sensorSystem.scan(
            level.getActors(),
            referenceShip.coord,
            referenceShip.heading
        );

        DefaultListModel<ContactEntry> model = new DefaultListModel<>();
        for (Contact c : contacts) {
            model.addElement(new ContactEntry(c));
        }
        contactList.setModel(model);
    }

    public record ContactEntry(Contact contact) {
        @Override
        public String toString() {
            if (contact.distance() == null) {
                return String.format("[PASSIVE] Br: %.0f°", Math.toDegrees(contact.bearing()));
            } else {
                return String.format("[ACTIVE] D:%.0f Br:%.0f°",
                    contact.distance(), Math.toDegrees(contact.bearing()));
            }
        }
    }
}
