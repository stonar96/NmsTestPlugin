package com.vanillage.nmstestplugin;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.command.CraftCommandMap;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Throwables;

import net.minecraft.commands.Commands;

public final class NmsTestPlugin extends JavaPlugin {
    private volatile Configuration configuration;

    @Override
    public void onEnable() {
        if (!new File(getDataFolder(), "README.txt").exists()) {
            saveResource("README.txt", false);
        }

        saveDefaultConfig();
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        // Add defaults.
        // saveConfig();
        configuration = config;
        // Initialize stuff.
        // Register events.
        registerCommands();
        getLogger().info(getPluginMeta().getDisplayName() + " enabled");
    }

    @Override
    public void onDisable() {
        Throwable throwable = null;

        try {
            try {
                unregisterCommands();
            } catch (Throwable t) {
                throwable = t;
            } finally {
                // Cleanup stuff.
            }
        } catch (Throwable t) {
            if (throwable == null) {
                throwable = t;
            } else {
                throwable.addSuppressed(t);
            }
        } finally {
            if (throwable != null) {
                Throwables.throwIfUnchecked(throwable);
                throw new RuntimeException(throwable);
            }
        }

        getLogger().info(getPluginMeta().getDisplayName() + " disabled");
    }

    public synchronized void onReload() {
        Throwable throwable = null;

        try {
            try {
                // Cleanup stuff.
            } catch (Throwable t) {
                throwable = t;
            } finally {
                if (throwable != null) {
                    getServer().getPluginManager().disablePlugin(this);
                }
            }
        } catch (Throwable t) {
            if (throwable == null) {
                throwable = t;
            } else {
                throwable.addSuppressed(t);
            }
        } finally {
            if (throwable != null) {
                Throwables.throwIfUnchecked(throwable);
                throw new RuntimeException(throwable);
            }
        }

        saveDefaultConfig();
        reloadConfig();
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        // Add defaults.
        // saveConfig();
        configuration = config;
        // Initialize stuff.
        getLogger().info(getPluginMeta().getDisplayName() + " reloaded");
    }

    public void registerCommands() {
        ((CraftServer) getServer()).getServer().vanillaCommandDispatcher.getDispatcher().register(Commands.literal("nmstestplugin").requires(c -> c.getBukkitSender().hasPermission("nmstestplugin.command.nmstestplugin")).then(Commands.literal("reload").requires(c -> c.getBukkitSender().hasPermission("nmstestplugin.command.nmstestplugin.reload")).executes(c -> {
            onReload();
            sendMessage(c.getSource().getBukkitSender(), "reload");
            return 1;
        })).then(Commands.literal("text").requires(c -> c.getBukkitSender().hasPermission("nmstestplugin.command.nmstestplugin.text")).executes(c -> {
            sendMessage(c.getSource().getBukkitSender(), "text");
            return 1;
        })));
    }

    public void unregisterCommands() {
        Command command = ((CraftServer) getServer()).getCommandMap().getCommand("minecraft:nmstestplugin");

        if (command != null) {
            command.unregister(((CraftServer) getServer()).getCommandMap());
        }

        if (((CraftCommandMap) ((CraftServer) getServer()).getCommandMap()).getKnownCommands().remove("nmstestplugin", ((CraftCommandMap) ((CraftServer) getServer()).getCommandMap()).getKnownCommands().remove("minecraft:nmstestplugin"))) {
            ((CraftServer) getServer()).getServer().getCommands().getDispatcher().getRoot().removeCommand("nmstestplugin");
        }

        ((CraftServer) getServer()).getServer().vanillaCommandDispatcher.getDispatcher().getRoot().removeCommand("nmstestplugin");
        ((CraftServer) getServer()).getServer().getCommands().getDispatcher().getRoot().removeCommand("minecraft:nmstestplugin");
        getServer().getOnlinePlayers().forEach(p -> ((CraftServer) getServer()).getServer().getCommands().sendCommands(((CraftPlayer) p).getHandle()));
    }

    private void sendMessage(CommandSender sender, String messageName) {
        sender.sendMessage(configuration.getStringList("messages." + messageName).toArray(new String[0]));
    }
}
