package ru.jetlabs.core.objects.sensors;

public record Contact(
    long id,                  // Уникальный ID (НЕ ссылка на Actor!)
    Double distance,          // null для passive radar
    double bearing,           // radians
    double confidence,        // 0..1
    long timestamp,           // System.currentTimeMillis()
    ContactType type          // ACTIVE, PASSIVE, VISUAL
) {
    public boolean isStale(long maxAgeMs) {
        return System.currentTimeMillis() - timestamp > maxAgeMs;
    }
}
