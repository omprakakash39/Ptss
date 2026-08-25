package com.petsplugin.commands;

import com.petsplugin.PetsPlugin;
import com.petsplugin.gui.PetGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PetsCommand implements CommandExecutor {
    private final PetsPlugin plugin;
    public PetsCommand(PetsPlugin plugin) { this.plugin = plugin; }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        if (!player.hasPermission("pets.use")) {
            player.sendMessage("§cYou don't have permission to use pets!");
            return true;
        }
        new PetGUI(plugin, player).openGUI();
        return true;
    }
}

