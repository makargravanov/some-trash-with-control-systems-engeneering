package ru.jetlabs.core.gui;

import ru.jetlabs.core.Player;
import ru.jetlabs.core.environment.Level;
import ru.jetlabs.core.objects.entities.ShipConfig;
import ru.jetlabs.core.objects.entities.ShipPresets;
import ru.jetlabs.core.objects.entities.SpaceShip;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class ShipCreationDialog extends JDialog {
    private ShipConfig selectedConfig = ShipPresets.LIGHT_FIGHTER;
    private Player selectedPlayer = Player.PLAYER_1;
    private final Level level;
    private final GroupSelection selection;
    private final Runnable onPlaceModeEnter;
    private final Consumer<Double> onPlaceShip;

    private JButton placeBtn;
    private boolean placementMode = false;
    private double scale = 1.0;
    private double offsetX = 0;
    private double offsetY = 0;

    public ShipCreationDialog(Frame owner, Level level, GroupSelection selection,
                             Runnable onPlaceModeEnter, Consumer<Double> onPlaceShip) {
        super(owner, "Create Ship", false);  // modeless = можно кликать на карту
        this.level = level;
        this.selection = selection;
        this.onPlaceModeEnter = onPlaceModeEnter;
        this.onPlaceShip = onPlaceShip;

        setupUI();
        setSize(300, 200);
        setLocationRelativeTo(owner);
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));

        // Панель выбора
        JPanel configPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        // Выбор класса корабля
        JComboBox<ShipConfig> classCombo = new JComboBox<>(new ShipConfig[]{
            ShipPresets.SCOUT, ShipPresets.LIGHT_FIGHTER, ShipPresets.HEAVY_FIGHTER,
            ShipPresets.FRIGATE, ShipPresets.DESTROYER, ShipPresets.CARGO_SHIP
        });
        classCombo.setSelectedItem(selectedConfig);
        classCombo.addActionListener(e -> selectedConfig = (ShipConfig) classCombo.getSelectedItem());

        // Выбор игрока
        JComboBox<Player> playerCombo = new JComboBox<>(new Player[]{
            Player.PLAYER_1, Player.PLAYER_2, Player.PLAYER_3
        });
        playerCombo.addActionListener(e -> selectedPlayer = (Player) playerCombo.getSelectedItem());

        configPanel.add(new JLabel("Class:"));
        configPanel.add(classCombo);
        configPanel.add(new JLabel("Player:"));
        configPanel.add(playerCombo);

        // Кнопка "Place on map"
        placeBtn = new JButton("Place on map");
        placeBtn.addActionListener(e -> enterPlacementMode());

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(placeBtn);
        buttonPanel.add(cancelBtn);

        add(configPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Инструкция
        JTextArea instructions = new JTextArea(2, 30);
        instructions.setText("Select class and player, then click 'Place on map' and click on the map.");
        instructions.setWrapStyleWord(true);
        instructions.setLineWrap(true);
        instructions.setEditable(false);
        instructions.setBackground(getBackground());
        add(instructions, BorderLayout.NORTH);
    }

    private void enterPlacementMode() {
        placementMode = true;
        placeBtn.setText("Click on map now...");
        placeBtn.setEnabled(false);
        onPlaceModeEnter.run();
    }

    public void setViewTransform(double scale, double offsetX, double offsetY) {
        this.scale = scale;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public boolean isInPlacementMode() {
        return placementMode;
    }

    public void placeShipAt(Point screenPoint) {
        if (!placementMode) return;

        double x = screenPoint.x / scale + offsetX;
        double y = screenPoint.y / scale + offsetY;

        SpaceShip ship = selectedConfig.createShip(x, y);
        ship.owner = selectedPlayer;
        ship.name = selectedPlayer.name + " " + selectedConfig.name();
        level.addActor(ship);

        onPlaceShip.accept(1.0);  // сигнал: корабль создан
        placementMode = false;
    }

    public void cancelPlacementMode() {
        placementMode = false;
        placeBtn.setText("Place on map");
        placeBtn.setEnabled(true);
    }
}
