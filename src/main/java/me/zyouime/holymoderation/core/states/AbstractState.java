package me.zyouime.holymoderation.core.states;

public abstract class AbstractState {

    public AbstractState() {
        this.reset();
    }

    public abstract void reset();
}
