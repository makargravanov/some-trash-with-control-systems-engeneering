package ru.jetlabs.core.environment;

import ru.jetlabs.core.objects.Actor;

import java.util.ArrayList;
import java.util.List;

public class Level {
    private final List<Actor> actors = new ArrayList<>();

    public void addActor(Actor actor) {
        actor.actors = this.actors;
        actors.add(actor);
        System.out.println(actor.mass);
        System.out.println(actor.radius);
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void updateActors(double deltaTime) {
        for (Actor actor : actors) {
            actor.update(deltaTime);
        }
    }
}