package me.zyouime.holymoderation.gui.hud;

import java.awt.Color;

import me.zyouime.holymoderation.Main;
import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.spy.SpySession;
import me.zyouime.holymoderation.core.spy.SpyStatus;
import me.zyouime.holymoderation.gui.GuiColors;
import me.zyouime.holymoderation.render.msdf.Icons;

public final class SpyHud extends HudPanel {

    private static final Color ONLINE = new Color(112, 205, 133);
    private static final Color PAUSED = new Color(255, 190, 90);
    private static final Color OFFLINE = new Color(150, 130, 118);

    public SpyHud() {
        super(Icons.EYE.unicode);
    }

    @Override
    protected HudBinding binding() {
        ModSettings settings = Main.getModContext().settings();
        return new HudBinding(settings.spyHudEnabled, settings.spyHudScale, settings.spyHudX, settings.spyHudY);
    }

    @Override
    protected String placeholder() {
        return "нет слежки";
    }

    @Override
    protected String idleText() {
        return "ожидание";
    }

    @Override
    protected boolean snapshot() {
        SpySession session = Main.getModContext().spyService().sessionOrNull();
        if (session == null) {
            return false;
        }
        String activity = session.getActivity() == null ? "" : session.getActivity();
        HudLine status = statusLine(session);
        if (activity.isEmpty()) {
            content(session.getPlayer(), status);
        } else {
            content(session.getPlayer(), status, HudLine.of(activity, GuiColors.TEXT_FAINT));
        }
        return true;
    }

    private HudLine statusLine(SpySession session) {
        SpyStatus current = session.getStatus();
        return switch (current.type()) {
            case ONLINE -> {
                boolean here = session.isOnSameServer(Main.getModContext().spyService().userLocation());
                String text = here ? "на этой анке, " + current.display() : current.display();
                yield HudLine.dotted(text, ONLINE, GuiColors.TEXT_MUTED);
            }
            case PAUSED -> HudLine.dotted(current.display(), PAUSED, GuiColors.TEXT_MUTED);
            case OFFLINE -> HudLine.dotted(current.display(), OFFLINE, GuiColors.TEXT_MUTED);
            default -> HudLine.dotted("поиск", GuiColors.TEXT_FAINT, GuiColors.TEXT_MUTED);
        };
    }
}