package ru.jetlabs.core.objects.sensors;

public enum ContactType {
    ACTIVE,    // Активный радар: есть distance
    PASSIVE,   // Пассивный: только bearing
    VISUAL     // Оптический (для будущего)
}
