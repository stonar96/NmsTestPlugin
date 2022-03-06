package com.vanillage.nmstestplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.command.CraftCommandMap;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.commands.Commands;

public final class NmsTestPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        saveResource("README.txt", false);
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        // saveConfig();
        // Initialize stuff.
        // Register events.
        registerCommands();
        getLogger().info(getDescription().getFullName() + " enabled");
    }

    @Override
    public void onDisable() {
        unregisterCommands();
        // Cleanup stuff.
        getLogger().info(getDescription().getFullName() + " disabled");
    }

    public void onReload() {
        // Cleanup stuff.
        saveDefaultConfig();
        reloadConfig();
        getConfig().options().copyDefaults(true);
        // saveConfig();
        // Initialize stuff.
        getLogger().info(getDescription().getFullName() + " reloaded");
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
        sender.sendMessage(getConfig().getStringList("messages." + messageName).toArray(new String[0]));
    }
}
