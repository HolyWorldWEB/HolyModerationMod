package me.zyouime.holymoderation.core.punishment;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import me.zyouime.holymoderation.core.service.CheckoutService;
import me.zyouime.holymoderation.core.service.NotificationsService;
import me.zyouime.holymoderation.core.service.PunishmentService;
import me.zyouime.holymoderation.core.util.Colors;
import net.minecraft.util.ActionResult;

@RequiredArgsConstructor
public final class PunishmentCommandListener {
    private static final String VK_MARKER = "вопросы?";
    private static final char[] FORBIDDEN_NICK_CHARS = {
            '!', '/', '#', '$', '%', '&', '\'', '(', ')', '*', '+', '-', ',', '.', ':', ';', '<',
            '=', '>', '?', '@', '[', '\\', ']', '^', '`', '{', '|', '}', '~', '"'
    };
    private final PunishmentService punishmentService;
    private final CheckoutService checkoutService;
    private final NotificationsService notifications;
    private final ConfirmationGate strangeNickGate = new ConfirmationGate();
    private final ConfirmationGate suspectGate = new ConfirmationGate();

    public ActionResult onCommand(String command) {
        Optional<PunishmentType> type = PunishmentType.byCommand(command.split(" ", 2)[0]);
        if (type.isEmpty()) {
            strangeNickGate.invalidateUnless(command);
            suspectGate.invalidateUnless(command);
            return ActionResult.PASS;
        }
        execute(type.get(), command);
        return ActionResult.FAIL;
    }

    public void reset() {
        strangeNickGate.reset();
        suspectGate.reset();
    }

    private void execute(PunishmentType type, String command) {
        PunishmentParser.Result result = PunishmentParser.parse(type, command);
        if (result.error() != null) {
            notifications.error(result.error());
            return;
        }
        Punishment punishment = result.punishment();
        String player = punishment.player();
        if (!isNicknameValid(player)) {
            notifications.error("Некорректный никнейм.");
            return;
        }
        if (!passesStrangeNickGate(player, command) || !passesSuspectGate(player, command)) {
            return;
        }
        if (!send(punishment)) {
            return;
        }
        if (type.endsCheckout() && checkoutService.isSuspect(player)) {
            checkoutService.finishAfterBan();
        }
    }

    private boolean send(Punishment punishment) {
        boolean addVk = needsVk(punishment);
        if (punishment.isPermanent()) {
            return punishmentService.punish(punishment.type(), punishment.player(), punishment.reason(), addVk);
        }
        return punishmentService.punish(punishment.type(), punishment.player(), punishment.time(), punishment.reason(), addVk);
    }

    private boolean needsVk(Punishment punishment) {
        if (!punishment.type().isSupportsVk()) {
            return false;
        }
        return !punishment.reason().toLowerCase().contains(VK_MARKER);
    }

    private boolean isNicknameValid(String nickname) {
        for (char forbidden : FORBIDDEN_NICK_CHARS) {
            if (nickname.indexOf(forbidden) >= 0) {
                return false;
            }
        }
        return true;
    }

    private boolean passesStrangeNickGate(String player, String command) {
        if (!PunishmentTime.looksLikeNumber(player)) {
            strangeNickGate.reset();
            return true;
        }
        if (strangeNickGate.confirm(command)) {
            return true;
        }
        notifications.warning("%sВы %s%sУВЕРЕНЫ%s, что хотите выдать наказание игроку %s%s%s%s? Введите команду ещё раз.".formatted(Colors.WHITE, Colors.RED, Colors.BOLD, Colors.WHITE, Colors.GREEN, Colors.BOLD, player, Colors.WHITE));
        return false;
    }

    private boolean passesSuspectGate(String player, String command) {
        if (!checkoutService.isSuspect(player)) {
            suspectGate.reset();
            return true;
        }
        if (suspectGate.confirm(command)) {
            return true;
        }
        notifications.warning("%sВы %s%sУВЕРЕНЫ%s, что хотите выдать наказание игроку, который у вас на проверке? Введите команду ещё раз.".formatted(Colors.WHITE, Colors.RED, Colors.BOLD, Colors.WHITE));
        return false;
    }
}
