package com.petsplugin.commands;

import com.petsplugin.PetsPlugin;
import com.petsplugin.enums.Rarity;
import com.petsplugin.items.PetItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveCommand implements CommandExecutor {
    public GiveCommand(PetsPlugin plugin) {}
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("pets.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        if (args.length < 3 || !args[0].equalsIgnoreCase("petegg")) {
            sender.sendMessage("§cUsage: /give petegg <rarity> <player>");
            return true;
        }
        Rarity rarity;
        try {
            rarity = Rarity.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cInvalid rarity! Use: regular, gold, rainbow, shiny");
            return true;
        }
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return true;
        }
        target.getInventory().addItem(PetItemHandler.createEggItem(rarity));
        sender.sendMessage("§aGave " + rarity.getFormattedName() + " §ePet Egg to " + target.getName());
        target.sendMessage("§aYou received a " + rarity.getFormattedName() + " §ePet Egg!");
        return true;
    }
}

