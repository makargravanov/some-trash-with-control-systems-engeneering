package ru.jetlabs.core.objects.sensors;

import ru.jetlabs.core.objects.Actor;
import ru.jetlabs.core.objects.components.Component;
import ru.jetlabs.core.util.structures.Vector2d;

import java.util.*;
import java.util.stream.Collectors;

public class SensorSystem extends Component {
    private final List<Sensor> sensors = new ArrayList<>();
    private final Map<Long, Contact> lastContacts = new HashMap<>();
    private long maxContactAge = 2000;  // контакты живут 2 сек

    public SensorSystem(double mass, double size) {
        super(mass, size);
    }

    public void addSensor(Sensor sensor) {
        sensors.add(sensor);
    }

    public List<Contact> scan(List<Actor> world, Vector2d myPos, double myHeading) {
        List<Contact> allContacts = new ArrayList<>();

        for (Sensor sensor : sensors) {
            allContacts.addAll(sensor.scan(world, myPos, myHeading));
        }

        // Обновляем кэш
        for (Contact c : allContacts) {
            lastContacts.put(c.id(), c);
        }

        // Удаляем устаревшие
        lastContacts.entrySet().removeIf(e ->
            e.getValue().isStale(maxContactAge)
        );

        return new ArrayList<>(lastContacts.values());
    }

    public Contact getNearestContact(Vector2d myPos) {
        return lastContacts.values().stream()
            .filter(c -> c.distance() != null)
            .min(Comparator.comparingDouble(Contact::distance))
            .orElse(null);
    }

    public boolean hasActiveRadarOn() {
        return sensors.stream().anyMatch(s -> s instanceof ActiveRadar);
    }

    public void setMaxContactAge(long maxAgeMs) {
        this.maxContactAge = maxAgeMs;
    }
}
