package me.zyouime.holymoderation.core.notification;

import java.awt.Color;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    SUCCESS(new Color(30, 60, 45, 210), new Color(80, 220, 150, 255)),
    WARNING(new Color(70, 55, 25, 210), new Color(255, 200, 80, 255)),
    ERROR(new Color(65, 25, 25, 210), new Color(255, 90, 90, 255));

    private final Color background;
    private final Color outline;
}
