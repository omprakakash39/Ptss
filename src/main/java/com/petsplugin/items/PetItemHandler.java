package com.petsplugin.items;

import com.petsplugin.enums.Pet;
import com.petsplugin.enums.Rarity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PetItemHandler {
    
    public static ItemStack createPetItem(Pet pet, Rarity rarity) {
        ItemStack item = new ItemStack(pet.getMaterial());
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            String displayName = rarity.getFormattedName() + " " + pet.getDisplayName();
            meta.setDisplayName(displayName);
            
            List<String> lore = new ArrayList<>();
            lore.add("§7§o" + pet.getAbilityName());
            lore.add("");
            lore.add("§e§l" + pet.getFormattedStat(rarity));
            lore.add("");
            lore.add("§aRight-click to activate");
            lore.add("§cDeactivate from GUI");
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    public static ItemStack createEggItem(Rarity rarity) {
        ItemStack item = new ItemStack(Material.EGG);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            String displayName = rarity.getFormattedName() + " Pet Egg";
            meta.setDisplayName(displayName);
            
            List<String> lore = new ArrayList<>();
            lore.add("§7§oRight-click to hatch");
            lore.add("");
            lore.add("§eWill hatch a " + rarity.getFormattedName() + " §epet");
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    public static boolean isPetItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return false;
        String displayName = meta.getDisplayName();
        return displayName.contains("Pet") && !displayName.contains("Egg");
    }
    
    public static boolean isEggItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return false;
        String displayName = meta.getDisplayName();
        return displayName.contains("Pet Egg");
    }
    
    public static Pet getPetFromItem(ItemStack item) {
        if (!isPetItem(item)) return null;
        String displayName = item.getItemMeta().getDisplayName();
        String petName = displayName.substring(displayName.lastIndexOf(" ") + 1);
        for (Pet pet : Pet.values()) {
            if (pet.getDisplayName().equals(petName)) return pet;
        }
        return null;
    }
    
    public static Rarity getRarityFromItem(ItemStack item) {
        if (!isPetItem(item) && !isEggItem(item)) return null;
        String displayName = item.getItemMeta().getDisplayName();
        for (Rarity rarity : Rarity.values()) {
            if (displayName.contains(rarity.getName())) return rarity;
        }
        return null;
    }
}

