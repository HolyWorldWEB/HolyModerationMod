package me.zyouime.holymoderation.core.command;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class CommandErrors {

    public static final SimpleCommandExceptionType NOT_CHECKING = Cmd.error("Вы никого не проверяете.");
    public static final SimpleCommandExceptionType NOT_SPYING = Cmd.error("Вы никого не отслеживаете.");
    public static final SimpleCommandExceptionType IN_HUB = Cmd.error("В хабе этого делать нельзя.");
}
