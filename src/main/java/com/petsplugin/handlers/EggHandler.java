package com.petsplugin.handlers;

import com.petsplugin.PetsPlugin;
import com.petsplugin.database.PetManager;
import com.petsplugin.enums.Pet;
import com.petsplugin.enums.Rarity;
import com.petsplugin.items.PetItemHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class EggHandler {
    
    private final PetsPlugin plugin;
    private final PetManager petManager;
    private final Random random;
    
    public EggHandler(PetsPlugin plugin) {
        this.plugin = plugin;
        this.petManager = plugin.getPetManager();
        this.random = new Random();
    }
    
    public void hatchEgg(Player player, ItemStack eggItem) {
        Rarity rarity = PetItemHandler.getRarityFromItem(eggItem);
        if (rarity == null) {
            player.sendMessage("§cInvalid egg!");
            return;
        }
        
        Pet[] pets = Pet.values();
        Pet randomPet = pets[random.nextInt(pets.length)];
        
        petManager.addPet(player.getUniqueId(), randomPet, rarity, 1);
        player.sendMessage("§aYour egg hatched into a " + rarity.getFormattedName() + " §e" + randomPet.getDisplayName() + "!");
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_TURTLE_EGG_HATCH, 1.0f, 1.0f);
        
        eggItem.setAmount(eggItem.getAmount() - 1);
    }
}

