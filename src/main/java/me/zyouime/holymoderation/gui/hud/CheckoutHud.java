package me.zyouime.holymoderation.gui.hud;

import java.awt.Color;
import java.util.Optional;

import me.zyouime.holymoderation.Main;
import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.checkout.CheckoutSession;
import me.zyouime.holymoderation.gui.GuiColors;
import me.zyouime.holymoderation.render.msdf.Icons;

public final class CheckoutHud extends HudPanel {

    private static final Color FROZEN = new Color(112, 205, 133);
    private static final Color WAITING = new Color(255, 190, 90);
    private static final int TICKS_PER_SECOND = 20;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SECONDS_PER_HOUR = 3600;

    public CheckoutHud() {
        super(Icons.EYE.unicode);
    }

    @Override
    protected HudBinding binding() {
        ModSettings settings = Main.getModContext().settings();
        return new HudBinding(settings.checkoutHudEnabled, settings.checkoutHudScale, settings.checkoutHudX, settings.checkoutHudY);
    }

    @Override
    protected String placeholder() {
        return "нет проверки";
    }

    @Override
    protected String idleText() {
        return "ожидание";
    }

    @Override
    protected boolean snapshot() {
        Optional<CheckoutSession> current = Main.getModContext().checkoutService().session();
        if (current.isEmpty()) {
            return false;
        }
        CheckoutSession session = current.get();
        content(session.getSuspect(), statusLine(session), HudLine.of(formatElapsed(session.getTicksSinceStart()), GuiColors.TEXT_FAINT));
        return true;
    }

    private static HudLine statusLine(CheckoutSession session) {
        if (session.isAwaitingFreeze()) {
            return HudLine.dotted("ожидание заморозки", WAITING, GuiColors.TEXT_MUTED);
        }
        return HudLine.dotted("заморожен", FROZEN, GuiColors.TEXT_MUTED);
    }

    private static String formatElapsed(int ticks) {
        int seconds = ticks / TICKS_PER_SECOND;
        int minutes = seconds / SECONDS_PER_MINUTE % SECONDS_PER_MINUTE;
        if (seconds >= SECONDS_PER_HOUR) {
            return "%d:%02d:%02d".formatted(seconds / SECONDS_PER_HOUR, minutes, seconds % SECONDS_PER_MINUTE);
        }
        return "%d:%02d".formatted(minutes, seconds % SECONDS_PER_MINUTE);
    }
}