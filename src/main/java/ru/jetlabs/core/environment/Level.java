package ru.jetlabs.core.environment;

import ru.jetlabs.core.objects.Actor;
import ru.jetlabs.core.objects.entities.Shell;

import java.util.ArrayList;
import java.util.List;

public class Level {
    private final List<Actor> actors = new ArrayList<>();
    private final List<Shell> shells = new ArrayList<>();
    private final List<Actor> actorsToRemove = new ArrayList<>();
    private final List<Shell> shellsToRemove = new ArrayList<>();

    public void addActor(Actor actor) {
        actor.level = this;
        actor.actors = this.actors;
        actors.add(actor);
        System.out.println(actor.mass);
        System.out.println(actor.radius);
    }
    public synchronized void addShell(Shell shell) {
        if (shell == null) return;
        shell.level = this;
        shell.shells = this.shells;
        shells.add(shell);
        shell.actors = this.actors;
        System.out.println(shell.mass);
        System.out.println(shell.radius);
    }

    public List<Actor> getActors() {
        return actors;
    }
    public List<Shell> getShells() {
        return shells;
    }

    public synchronized void removeActor(Actor actor) {
        actorsToRemove.add(actor);
    }

    public synchronized void removeShell(Shell shell) {
        shellsToRemove.add(shell);
    }

    public synchronized void updateActors(double deltaTime) {
        for (Actor actor : actors) {
            if (!actorsToRemove.contains(actor)) {
                actor.update(deltaTime);
            }
        }
        for (int i = shells.size() - 1; i >= 0; i--) {
            Shell shell = shells.get(i);
            if (!shellsToRemove.contains(shell)) {
                shell.update(deltaTime);
            }
        }

        // Удаляем ПОСЛЕ всех обновлений
        actors.removeAll(actorsToRemove);
        shells.removeAll(shellsToRemove);
        actorsToRemove.clear();
        shellsToRemove.clear();
    }
}