package com.petsplugin.placeholder;

import com.petsplugin.PetsPlugin;
import com.petsplugin.database.PetManager;
import com.petsplugin.enums.Pet;
import com.petsplugin.enums.Rarity;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.util.Map;

public class PetsPlaceholderExpansion extends PlaceholderExpansion {
    
    private final PetManager petManager;
    
    public PetsPlaceholderExpansion(PetsPlugin plugin) {
        this.petManager = plugin.getPetManager();
    }
    
    @Override public String getIdentifier() { return "pets"; }
    @Override public String getAuthor() { return "Sayan"; }
    @Override public String getVersion() { return "1.0.0"; }
    @Override public boolean persist() { return true; }
    
    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) return "";
        Map<String, String> activePets = petManager.getActivePets(player.getUniqueId());
        
        return switch (params.toLowerCase()) {
            case "primary_pet" -> getPetName(activePets, "primary_pet", "primary_rarity");
            case "secondary_pet" -> getPetName(activePets, "secondary_pet", "secondary_rarity");
            case "primary_rarity" -> getRarityName(activePets, "primary_rarity");
            case "secondary_rarity" -> getRarityName(activePets, "secondary_rarity");
            case "primary_stat" -> getPetStat(activePets, "primary_pet", "primary_rarity");
            case "secondary_stat" -> getPetStat(activePets, "secondary_pet", "secondary_rarity");
            default -> null;
        };
    }
    
    private String getPetName(Map<String, String> activePets, String petKey, String rarityKey) {
        if (activePets.get(petKey) != null) {
            try {
                Pet pet = Pet.valueOf(activePets.get(petKey));
                Rarity rarity = Rarity.valueOf(activePets.get(rarityKey));
                return rarity.getFormattedName() + " " + pet.getDisplayName();
            } catch (IllegalArgumentException ignored) {}
        }
        return "None";
    }
    
    private String getRarityName(Map<String, String> activePets, String rarityKey) {
        if (activePets.get(rarityKey) != null) {
            try {
                return Rarity.valueOf(activePets.get(rarityKey)).getFormattedName();
            } catch (IllegalArgumentException ignored) {}
        }
        return "None";
    }
    
    private String getPetStat(Map<String, String> activePets, String petKey, String rarityKey) {
        if (activePets.get(petKey) != null) {
            try {
                return Pet.valueOf(activePets.get(petKey)).getFormattedStat(Rarity.valueOf(activePets.get(rarityKey)));
            } catch (IllegalArgumentException ignored) {}
        }
        return "None";
    }
}

