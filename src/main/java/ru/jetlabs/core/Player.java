package ru.jetlabs.core;

import java.awt.Color;

public class Player {
    private static int nextId = 1;
    public final int id;
    public final String name;
    public final Color color;

    public Player(String name, Color color) {
        this.id = nextId++;
        this.name = name;
        this.color = color;
    }

    // Предустановленные игроки
    public static final Player NEUTRAL = new Player("Neutral", Color.GRAY);
    public static final Player PLAYER_1 = new Player("Player 1", Color.CYAN);
    public static final Player PLAYER_2 = new Player("Player 2", Color.ORANGE);
    public static final Player PLAYER_3 = new Player("Player 3", Color.MAGENTA);

    @Override
    public String toString() {
        return name;
    }
}
