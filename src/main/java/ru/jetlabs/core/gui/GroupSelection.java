package ru.jetlabs.core.gui;

import ru.jetlabs.core.Player;
import ru.jetlabs.core.environment.Level;
import ru.jetlabs.core.objects.Actor;
import ru.jetlabs.core.objects.entities.SpaceShip;
import ru.jetlabs.core.objects.sensors.Contact;

import java.util.*;

public class GroupSelection {
    private final Set<Actor> selected = new HashSet<>();
    private Player currentPlayer = Player.PLAYER_1;

    public void select(Actor actor, boolean addToSelection) {
        if (!addToSelection) {
            selected.clear();
        }
        if (actor != null && actor.owner == currentPlayer) {
            selected.add(actor);
        }
    }

    public void setTarget(double x, double y, Level level) {
        if (selected.isEmpty()) return;

        // Разнесение точек для предотвращения столкновений
        List<Actor> actors = new ArrayList<>(selected);
        distributeTargets(actors, x, y);
    }

    private void distributeTargets(List<Actor> actors, double centerX, double centerY) {
        int n = actors.size();
        if (n == 1) {
            actors.get(0).setTarget(centerX, centerY);
            return;
        }

        // Расставляем по кругу с учётом радиусов
        double totalRadius = 0;
        for (Actor a : actors) totalRadius += a.radius();

        double spacing = totalRadius * 1.5;  // расстояние между центрами

        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            double offset = spacing * 0.6;  // радиус размещения
            double tx = centerX + Math.cos(angle) * offset;
            double ty = centerY + Math.sin(angle) * offset;
            actors.get(i).setTarget(tx, ty);
        }
    }

    public void fireAt(double x, double y, Level level) {
        for (Actor actor : selected) {
            if (actor instanceof SpaceShip ship) {
                level.addShell(ship.strike(x, y));
            }
        }
    }

    public void attackContact(Contact contact, Level level) {
        for (Actor actor : selected) {
            if (actor instanceof SpaceShip ship) {
                level.addShell(ship.strikeAtContact(contact));
            }
        }
    }

    public void stop() {
        for (Actor actor : selected) {
            actor.target = null;
        }
    }

    public void delete(Level level) {
        for (Actor actor : selected) {
            level.getActors().remove(actor);
        }
        selected.clear();
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
        selected.clear();  // сбросить выделение при смене игрока
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Set<Actor> getSelected() {
        return Collections.unmodifiableSet(selected);
    }

    public boolean isSelected(Actor actor) {
        return selected.contains(actor);
    }

    public int getSelectedCount() {
        return selected.size();
    }

    public void clear() {
        selected.clear();
    }
}
