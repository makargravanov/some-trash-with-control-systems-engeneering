package ru.jetlabs.core.gui;

import ru.jetlabs.core.Player;

import javax.swing.*;
import java.awt.*;

public class PlayerSwitcherPanel extends JPanel {
    private final GroupSelection selection;
    private final JComboBox<Player> playerCombo;

    public PlayerSwitcherPanel(GroupSelection selection) {
        this.selection = selection;
        this.playerCombo = new JComboBox<>(new Player[]{
            Player.PLAYER_1, Player.PLAYER_2, Player.PLAYER_3, Player.NEUTRAL
        });

        playerCombo.setSelectedItem(Player.PLAYER_1);
        playerCombo.addActionListener(e -> {
            selection.setCurrentPlayer((Player) playerCombo.getSelectedItem());
        });

        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(new JLabel("Player:"));
        add(playerCombo);
    }

    public void updatePlayer() {
        Player current = selection.getCurrentPlayer();
        playerCombo.setSelectedItem(current);
    }
}
