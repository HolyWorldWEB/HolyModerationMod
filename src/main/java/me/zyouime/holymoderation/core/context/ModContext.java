package me.zyouime.holymoderation.core.context;

import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.api.JournalApi;
import me.zyouime.holymoderation.core.checkout.CheckoutJournal;
import me.zyouime.holymoderation.core.checkout.CheckoutPrompts;
import me.zyouime.holymoderation.core.checkout.TextSender;
import me.zyouime.holymoderation.core.command.CommandInitializer;
import me.zyouime.holymoderation.core.connection.ConnectionTracker;
import me.zyouime.holymoderation.core.module.impl.*;
import me.zyouime.holymoderation.core.module.ModuleManager;
import me.zyouime.holymoderation.core.parser.JsonParser;
import me.zyouime.holymoderation.core.punishment.VkLinkProvider;
import me.zyouime.holymoderation.core.service.*;
import me.zyouime.holymoderation.core.settings.SettingsEditor;
import me.zyouime.holymoderation.core.states.ModeratorState;
import me.zyouime.holymoderation.core.states.UserState;
import me.zyouime.holymoderation.core.user.VanishController;

public record ModContext(
        ModSettings settings,
        ChatService chatService,
        LoggerService loggerService,
        UserState userState,
        ModeratorState moderatorState,
        ModeratorService moderatorService,
        SpyService spyService,
        ConnectionTracker connectionTracker,
        NotificationsService notificationsService,
        HttpClientService httpClientService,
        PunishmentService punishmentService,
        CheckoutService checkoutService,
        ObsService obsService,
        JournalApi journalApi,
        ModuleManager moduleManager) {

    public static ModContext createContext() {
        ModSettings modSettings = new ModSettings();
        modSettings.loadSettings();
        ChatService chatService = new ChatService(modSettings);
        LoggerService loggerService = new LoggerService();
        UserState userState = new UserState();
        NotificationsService notificationsService = new NotificationsService();
        SpyService spyService = new SpyService(userState, chatService, modSettings, notificationsService);
        HttpClientService httpClientService = new HttpClientService();
        JsonParser jsonParser = new JsonParser();
        JournalApi journalApi = new JournalApi(httpClientService, modSettings.apiToken::getValue, loggerService, jsonParser);
        CheckoutPrompts prompts = new CheckoutPrompts(chatService);
        ModeratorState moderatorState = new ModeratorState();
        ModeratorService moderatorService = new ModeratorService(journalApi, moderatorState, modSettings, notificationsService);
        modSettings.apiToken.setSetCallback(token -> moderatorService.onTokenChanged());
        CheckoutJournal journal = new CheckoutJournal(journalApi, userState, moderatorService, notificationsService, loggerService);
        TextSender textSender = new TextSender(chatService);
        VanishController vanishController = new VanishController(userState, chatService);
        CheckoutService checkoutService = new CheckoutService(userState, chatService, notificationsService, spyService,prompts, textSender, modSettings, vanishController );
        VkLinkProvider vkLinkProvider = new VkLinkProvider(moderatorState, modSettings);
        PunishmentService punishmentService = new PunishmentService(chatService, notificationsService, vkLinkProvider);
        ObsService obsService = new ObsService(modSettings, notificationsService, loggerService);
        SettingsEditor settingsEditor = new SettingsEditor(modSettings);
        ReportService reportService = new ReportService(chatService, notificationsService);
        ModuleManager moduleManager = new ModuleManager()
                .add(new SettingsModule(modSettings, settingsEditor, chatService, notificationsService))
                .add(new ModeratorModule(moderatorState, moderatorService, chatService))
                .add(new UserStateModule(userState, chatService, notificationsService, modSettings, loggerService, vanishController))
                .add(new SpyModule(spyService, userState, checkoutService, chatService, modSettings, loggerService))
                .add(new CheckoutModule(userState, punishmentService, checkoutService, chatService, notificationsService, modSettings, prompts, journal))
                .add(new PunishmentModule(punishmentService, checkoutService, notificationsService, modSettings))
                .add(new ObsModule(obsService))
                .add(new MessageModule(checkoutService, chatService, modSettings))
                .add(new NotificationsModule(notificationsService))
                .add(new ReportModule(reportService));


        moduleManager.initAll();
        ConnectionTracker connectionTracker = new ConnectionTracker(userState, moduleManager, notificationsService);
        new CommandInitializer(moduleManager.collectCommands(), moduleManager, loggerService, notificationsService).init();
        return new ModContext(modSettings, chatService, loggerService, userState, moderatorState, moderatorService, spyService, connectionTracker, notificationsService, httpClientService, punishmentService, checkoutService, obsService,journalApi, moduleManager);
    }
}
