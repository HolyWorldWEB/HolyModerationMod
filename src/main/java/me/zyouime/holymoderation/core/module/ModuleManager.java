package me.zyouime.holymoderation.core.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import me.zyouime.holymoderation.core.command.ModCommand;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class ModuleManager {

    private final List<Module> modules = new ArrayList<>();
    private boolean installed = false;
    @Getter
    private boolean enabled = true;

    public ModuleManager add(Module module) {
        if (installed) {
            throw new IllegalStateException("Модули нельзя добавлять после installAll(): " + module.name());
        }
        modules.add(module);
        return this;
    }

    public void initAll() {
        if (installed) {
            return;
        }
        installed = true;
        for (Module module : modules) {
            module.init();
        }
        ClientTickEvents.END_CLIENT_TICK.register(client -> tick());
    }

    public List<ModCommand> collectCommands() {
        return modules.stream()
                      .flatMap(module -> module.commands().stream())
                      .toList();
    }

    public void toggle(boolean value) {
        enabled = value;
        setAllEnabled(value);
    }

    public List<Module> all() {
        return List.copyOf(modules);
    }

    public <T extends Module> Optional<T> find(Class<T> type) {
        return modules.stream().filter(type::isInstance).map(type::cast).findFirst();
    }

    public void setEnabled(Class<? extends Module> module, boolean enabled) {
        this.find(module).ifPresent(m -> m.setEnabled(enabled));
    }

    public void setAllEnabled(boolean enabled) {
        modules.forEach(module -> module.setEnabled(enabled));
    }

    private void tick() {
        for (Module module : modules) {
            if (!module.isEnabled()) {
                continue;
            }
            module.tick();
        }
    }
}
