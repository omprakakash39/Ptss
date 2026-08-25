package com.petsplugin.listeners;

import com.petsplugin.PetsPlugin;
import com.petsplugin.abilities.PetAbilityManager;
import com.petsplugin.gui.FusionGUI;
import com.petsplugin.gui.PetGUI;
import com.petsplugin.handlers.EggHandler;
import com.petsplugin.items.PetItemHandler;
import com.petsplugin.enums.Pet;
import com.petsplugin.enums.Rarity;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class PetListener implements Listener {
    
    private final PetsPlugin plugin;
    private final PetAbilityManager abilityManager;
    private final EggHandler eggHandler;
    
    public PetListener(PetsPlugin plugin) {
        this.plugin = plugin;
        this.abilityManager = new PetAbilityManager(plugin);
        this.eggHandler = new EggHandler(plugin);
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null || (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            return;
        }
        
        if (PetItemHandler.isEggItem(item)) {
            event.setCancelled(true);
            eggHandler.hatchEgg(player, item);
            return;
        }
        
        if (PetItemHandler.isPetItem(item)) {
            event.setCancelled(true);
            Pet pet = PetItemHandler.getPetFromItem(item);
            Rarity rarity = PetItemHandler.getRarityFromItem(item);
            if (pet != null && rarity != null) {
                activatePet(player, pet, rarity);
            }
        }
    }
    
    private void activatePet(Player player, Pet pet, Rarity rarity) {
        PetGUI petGUI = new PetGUI(plugin, player);
        if (plugin.getPetManager().getPetCount(player.getUniqueId(), pet, rarity) <= 0) {
            player.sendMessage(ChatColor.RED + "You don't have this pet!");
            return;
        }
        
        Map<String, String> activePets = plugin.getPetManager().getActivePets(player.getUniqueId());
        if (activePets.get("primary_pet") == null) {
            petGUI.setActivePet(pet, rarity, true);
            player.sendMessage(ChatColor.GREEN + "Activated " + rarity.getFormattedName() + " " + pet.getDisplayName() + " as Primary Pet!");
        } else if (activePets.get("secondary_pet") == null) {
            petGUI.setActivePet(pet, rarity, false);
            player.sendMessage(ChatColor.GREEN + "Activated " + rarity.getFormattedName() + " " + pet.getDisplayName() + " as Secondary Pet!");
        } else {
            player.sendMessage(ChatColor.RED + "You already have 2 active pets! Deactivate one first.");
            return;
        }
        player.getInventory().removeItem(PetItemHandler.createPetItem(pet, rarity));
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();
        
        if (title.startsWith("Pets")) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            PetGUI petGUI = new PetGUI(plugin, player);
            
            if (slot == 45) { petGUI.previousPage(); return; }
            if (slot == 53) { petGUI.nextPage(); return; }
            if (slot == 49) { new FusionGUI(plugin, player).openFusionGUI(); return; }
            if (slot == 22) { petGUI.deactivatePet(true); player.sendMessage(ChatColor.YELLOW + "Deactivated Primary Pet!"); return; }
            if (slot == 31) { petGUI.deactivatePet(false); player.sendMessage(ChatColor.YELLOW + "Deactivated Secondary Pet!"); return; }
            
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && PetItemHandler.isPetItem(clickedItem)) {
                Pet pet = PetItemHandler.getPetFromItem(clickedItem);
                Rarity rarity = PetItemHandler.getRarityFromItem(clickedItem);
                if (pet != null && rarity != null) {
                    Map<String, String> activePets = plugin.getPetManager().getActivePets(player.getUniqueId());
                    if (activePets.get("primary_pet") == null) {
                        petGUI.setActivePet(pet, rarity, true);
                    } else if (activePets.get("secondary_pet") == null) {
                        petGUI.setActivePet(pet, rarity, false);
                    } else {
                        player.sendMessage(ChatColor.RED + "You already have 2 active pets!");
                    }
                }
            }
        }
        
        if (title.equals("Pet Fusion")) {
            event.setCancelled(true);
            FusionGUI fusionGUI = new FusionGUI(plugin, player);
            FusionGUI.FusionRecipe recipe = fusionGUI.getRecipeFromSlot(event.getRawSlot());
            if (recipe != null) fusionGUI.performFusion(recipe);
        }
        
        if (event.getInventory().getType() == InventoryType.ANVIL && event.getRawSlot() == 2) {
            event.setCancelled(true);
            new FusionGUI(plugin, player).openFusionGUI();
        }
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player defender && event.getDamager() instanceof Player attacker) {
            abilityManager.applyCombatAbilities(attacker, defender, event);
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        abilityManager.checkDeathSave(player);
        if (player.getHealth() > 0) {
            event.setKeepInventory(true);
            event.setKeepLevel(true);
            event.setDroppedExp(0);
            event.getDrops().clear();
        }
    }
}

