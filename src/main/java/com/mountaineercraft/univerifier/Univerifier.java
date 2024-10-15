package com.mountaineercraft.univerifier;

import com.mountaineercraft.univerifier.tokens.Verification;
import net.luckperms.api.LuckPerms;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public final class Univerifier extends JavaPlugin {

    // Get host, port, username, password, and fromEmail from config.yml
    public static String smtpHost;
    public static String smtpPort;
    public static String fromEmail;
    public static String accessToken;
    public static String domain;
    public static String requiredEmailDomain;

    public static Logger logger;

    public static LuckPerms luckPerms;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        logger = getLogger();
        luckPerms = getServer().getServicesManager().load(LuckPerms.class);

        // Token server configuration (Javalin)
        Verification verificationServer = new Verification();

        // Mail related configuration
        smtpHost = getConfig().getString("smtpHost");
        smtpPort = getConfig().getString("smtpPort");
        fromEmail = getConfig().getString("fromEmail");
        domain = getConfig().getString("domain");
        requiredEmailDomain = getConfig().getString("requiredEmailDomain");

        // Register the command
        Objects.requireNonNull(getCommand("verify")).setExecutor(new CommandVerify());

        // Register events
        getServer().getPluginManager().registerEvents(new UnverifiedLock(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }
}
