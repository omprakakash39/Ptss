package com.petsplugin.gui;

import com.petsplugin.PetsPlugin;
import com.petsplugin.database.PetManager;
import com.petsplugin.enums.Pet;
import com.petsplugin.enums.Rarity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class FusionGUI {
    
    private final PetsPlugin plugin;
    private final PetManager petManager;
    private final Player player;
    private final Map<Integer, FusionRecipe> fusionRecipes;
    
    public FusionGUI(PetsPlugin plugin, Player player) {
        this.plugin = plugin;
        this.petManager = plugin.getPetManager();
        this.player = player;
        this.fusionRecipes = initializeFusionRecipes();
    }
    
    private Map<Integer, FusionRecipe> initializeFusionRecipes() {
        Map<Integer, FusionRecipe> recipes = new HashMap<>();
        recipes.put(1, new FusionRecipe(Rarity.REGULAR, 5, Rarity.GOLD, 100));
        recipes.put(2, new FusionRecipe(Rarity.REGULAR, 4, Rarity.GOLD, 80));
        recipes.put(3, new FusionRecipe(Rarity.GOLD, 4, Rarity.RAINBOW, 100));
        recipes.put(4, new FusionRecipe(Rarity.GOLD, 3, Rarity.RAINBOW, 70));
        recipes.put(5, new FusionRecipe(Rarity.RAINBOW, 3, Rarity.SHINY, 100));
        recipes.put(6, new FusionRecipe(Rarity.RAINBOW, 2, Rarity.SHINY, 60));
        return recipes;
    }
    
    public void openFusionGUI() {
        Inventory inv = Bukkit.createInventory(null, 54, "Pet Fusion");
        addFusionRecipes(inv);
        addInfoButton(inv);
        player.openInventory(inv);
    }
    
    private void addFusionRecipes(Inventory inv) {
        int slot = 0;
        for (Map.Entry<Integer, FusionRecipe> entry : fusionRecipes.entrySet()) {
            FusionRecipe recipe = entry.getValue();
            inv.setItem(slot, createRecipeItem(recipe));
            slot++;
            if (slot % 9 == 0) slot++;
        }
    }
    
    private ItemStack createRecipeItem(FusionRecipe recipe) {
        ItemStack item = new ItemStack(Material.ANVIL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Fusion Recipe");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE + "Input: " + recipe.amount + "x " + recipe.fromRarity.getFormattedName() + " Pets");
            lore.add(ChatColor.WHITE + "Output: 1x " + recipe.toRarity.getFormattedName() + " Pet");
            lore.add("");
            lore.add(ChatColor.GREEN + "Success Chance: " + recipe.successChance + "%");
            lore.add(ChatColor.RED + "Failure: All pets lost!");
            lore.add("");
            lore.add(ChatColor.YELLOW + "Click to fuse");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    private void addInfoButton(Inventory inv) {
        ItemStack infoButton = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = infoButton.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName(ChatColor.AQUA + "Fusion Info");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE + "Fusion Rules:");
            lore.add(ChatColor.GRAY + "• 5 Regular → 1 Gold (100%)");
            lore.add(ChatColor.GRAY + "• 4 Regular → 1 Gold (80%)");
            lore.add(ChatColor.GRAY + "• 4 Gold → 1 Rainbow (100%)");
            lore.add(ChatColor.GRAY + "• 3 Gold → 1 Rainbow (70%)");
            lore.add(ChatColor.GRAY + "• 3 Rainbow → 1 Shiny (100%)");
            lore.add(ChatColor.GRAY + "• 2 Rainbow → 1 Shiny (60%)");
            infoMeta.setLore(lore);
            infoButton.setItemMeta(infoMeta);
        }
        inv.setItem(49, infoButton);
    }
    
    public void performFusion(FusionRecipe recipe) {
        Map<String, Integer> playerPets = petManager.getPlayerPets(player.getUniqueId());
        Pet targetPet = findPetForFusion(playerPets, recipe.fromRarity, recipe.amount);
        
        if (targetPet == null) {
            player.sendMessage(ChatColor.RED + "You don't have enough " + recipe.fromRarity.getFormattedName() + " pets!");
            return;
        }
        
        petManager.removePet(player.getUniqueId(), targetPet, recipe.fromRarity, recipe.amount);
        
        if (new Random().nextInt(100) + 1 <= recipe.successChance) {
            petManager.addPet(player.getUniqueId(), targetPet, recipe.toRarity, 1);
            player.sendMessage(ChatColor.GREEN + "Fusion successful! You got a " + recipe.toRarity.getFormattedName() + " " + targetPet.getDisplayName() + "!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        } else {
            player.sendMessage(ChatColor.RED + "Fusion failed! All pets were lost.");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }
        openFusionGUI();
    }
    
    private Pet findPetForFusion(Map<String, Integer> playerPets, Rarity rarity, int amount) {
        for (Pet pet : Pet.values()) {
            String key = pet.name() + "_" + rarity.name();
            if (playerPets.containsKey(key) && playerPets.get(key) >= amount) return pet;
        }
        return null;
    }
    
    public FusionRecipe getRecipeFromSlot(int slot) { return fusionRecipes.get(slot); }
    
    public static class FusionRecipe {
        public final Rarity fromRarity;
        public final int amount;
        public final Rarity toRarity;
        public final int successChance;
        
        public FusionRecipe(Rarity fromRarity, int amount, Rarity toRarity, int successChance) {
            this.fromRarity = fromRarity;
            this.amount = amount;
            this.toRarity = toRarity;
            this.successChance = successChance;
        }
    }
}

