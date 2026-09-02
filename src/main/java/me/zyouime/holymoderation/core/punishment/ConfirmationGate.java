package me.zyouime.holymoderation.core.punishment;

import org.apache.commons.lang3.StringUtils;

public final class ConfirmationGate {

    private String pendingCommand = StringUtils.EMPTY;

    public boolean confirm(String command) {
        if (command.equals(pendingCommand)) {
            pendingCommand = StringUtils.EMPTY;
            return true;
        }
        pendingCommand = command;
        return false;
    }

    public void invalidateUnless(String command) {
        if (!command.equals(pendingCommand)) {
            reset();
        }
    }

    public void reset() {
        pendingCommand = StringUtils.EMPTY;
    }

    public boolean isPending() {
        return !pendingCommand.isEmpty();
    }
}
