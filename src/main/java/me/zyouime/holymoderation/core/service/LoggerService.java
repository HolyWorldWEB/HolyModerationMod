package me.zyouime.holymoderation.core.service;

import lombok.Setter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerService {
    private final Logger logger;
    @Setter private Level level = Level.DEBUG;

    public LoggerService() {
        this.logger = LogManager.getLogger("HolyModeration");
    }

    public void exception(Object message) {
        if (level.intLevel() >= Level.ERROR.intLevel()) {
            logger.error("[HM EXCEPTION] {}", message);
        }
    }

    public void info(Object message) {
        if (level.intLevel() >= Level.INFO.intLevel()) {
            logger.info("[HM INFO] {}", message);
        }
    }

    public void debug(Object message) {
        if (level.intLevel() >= Level.DEBUG.intLevel()) {
            logger.warn("[HM DEBUG] {}", message);
        }
    }
}