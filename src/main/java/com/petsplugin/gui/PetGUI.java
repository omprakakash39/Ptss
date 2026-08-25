package com.petsplugin.gui;

import com.petsplugin.PetsPlugin;
import com.petsplugin.database.PetManager;
import com.petsplugin.enums.Pet;
import com.petsplugin.enums.Rarity;
import com.petsplugin.items.PetItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class PetGUI {
    
    private final PetsPlugin plugin;
    private final PetManager petManager;
    private final Player player;
    private int currentPage = 1;
    private static final int ITEMS_PER_PAGE = 4;
    
    public PetGUI(PetsPlugin plugin, Player player) {
        this.plugin = plugin;
        this.petManager = plugin.getPetManager();
        this.player = player;
    }
    
    public void openGUI() {
        int totalPages = (int) Math.ceil((double) Pet.values().length / ITEMS_PER_PAGE);
        currentPage = Math.min(currentPage, totalPages);
        
        String title = "Pets (" + currentPage + "/" + totalPages + ")";
        Inventory inv = Bukkit.createInventory(null, 54, title);
        
        addPetsToGUI(inv);
        addActivePetSlots(inv);
        addNavigationButtons(inv, totalPages);
        
        player.openInventory(inv);
    }
    
    private void addPetsToGUI(Inventory inv) {
        Map<String, Integer> playerPets = petManager.getPlayerPets(player.getUniqueId());
        Pet[] allPets = Pet.values();
        
        int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allPets.length);
        
        for (int i = startIndex; i < endIndex; i++) {
            Pet pet = allPets[i];
            int row = i - startIndex;
            
            int[] slots = {row * 9 + 1, row * 9 + 2, row * 9 + 6, row * 9 + 7};
            Rarity[] rarities = {Rarity.REGULAR, Rarity.GOLD, Rarity.RAINBOW, Rarity.SHINY};
            
            for (int j = 0; j < rarities.length; j++) {
                Rarity rarity = rarities[j];
                String key = pet.name() + "_" + rarity.name();
                int amount = playerPets.getOrDefault(key, 0);
                
                if (amount > 0) {
                    ItemStack petItem = PetItemHandler.createPetItem(pet, rarity);
                    ItemMeta meta = petItem.getItemMeta();
                    
                    if (meta != null) {
                        List<String> lore = meta.getLore();
                        if (lore != null) {
                            lore.add(0, "§eAmount: " + amount);
                            meta.setLore(lore);
                        }
                        petItem.setItemMeta(meta);
                    }
                    inv.setItem(slots[j], petItem);
                }
            }
        }
    }
    
    private void addActivePetSlots(Inventory inv) {
        ItemStack primarySlot = createActiveSlot("Primary Pet", Material.LIME_STAINED_GLASS_PANE);
        inv.setItem(22, primarySlot);
        
        ItemStack secondarySlot = createActiveSlot("Secondary Pet", Material.LIME_STAINED_GLASS_PANE);
        inv.setItem(31, secondarySlot);
        
        Map<String, String> activePets = petManager.getActivePets(player.getUniqueId());
        
        if (activePets.containsKey("primary_pet") && activePets.get("primary_pet") != null) {
            try {
                Pet pet = Pet.valueOf(activePets.get("primary_pet"));
                Rarity rarity = Rarity.valueOf(activePets.get("primary_rarity"));
                inv.setItem(22, PetItemHandler.createPetItem(pet, rarity));
            } catch (IllegalArgumentException ignored) {}
        }
        
        if (activePets.containsKey("secondary_pet") && activePets.get("secondary_pet") != null) {
            try {
                Pet pet = Pet.valueOf(activePets.get("secondary_pet"));
                Rarity rarity = Rarity.valueOf(activePets.get("secondary_rarity"));
                inv.setItem(31, PetItemHandler.createPetItem(pet, rarity));
            } catch (IllegalArgumentException ignored) {}
        }
    }
    
    private ItemStack createActiveSlot(String name, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§a" + name);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    private void addNavigationButtons(Inventory inv, int totalPages) {
        if (currentPage > 1) {
            ItemStack prevButton = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevButton.getItemMeta();
            if (prevMeta != null) { prevMeta.setDisplayName("§ePrevious Page"); prevButton.setItemMeta(prevMeta); }
            inv.setItem(45, prevButton);
        }
        
        if (currentPage < totalPages) {
            ItemStack nextButton = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextButton.getItemMeta();
            if (nextMeta != null) { nextMeta.setDisplayName("§eNext Page"); nextButton.setItemMeta(nextMeta); }
            inv.setItem(53, nextButton);
        }
        
        ItemStack fusionButton = new ItemStack(Material.ANVIL);
        ItemMeta fusionMeta = fusionButton.getItemMeta();
        if (fusionMeta != null) { fusionMeta.setDisplayName("§6§lPet Fusion"); fusionButton.setItemMeta(fusionMeta); }
        inv.setItem(49, fusionButton);
    }
    
    public void nextPage() { currentPage++; openGUI(); }
    public void previousPage() { if (currentPage > 1) { currentPage--; openGUI(); } }
    
    public void setActivePet(Pet pet, Rarity rarity, boolean isPrimary) {
        Map<String, String> currentActive = petManager.getActivePets(player.getUniqueId());
        
        Pet currentPrimary = currentActive.containsKey("primary_pet") ? Pet.valueOf(currentActive.get("primary_pet")) : null;
        Rarity currentPrimaryRarity = currentActive.containsKey("primary_rarity") ? Rarity.valueOf(currentActive.get("primary_rarity")) : null;
        
        Pet currentSecondary = currentActive.containsKey("secondary_pet") ? Pet.valueOf(currentActive.get("secondary_pet")) : null;
        Rarity currentSecondaryRarity = currentActive.containsKey("secondary_rarity") ? Rarity.valueOf(currentActive.get("secondary_rarity")) : null;
        
        if (isPrimary) {
            if (currentPrimary != null && currentPrimaryRarity != null) {
                petManager.addPet(player.getUniqueId(), currentPrimary, currentPrimaryRarity, 1);
            }
            petManager.setActivePets(player.getUniqueId(), pet, rarity, currentSecondary, currentSecondaryRarity);
        } else {
            if (currentSecondary != null && currentSecondaryRarity != null) {
                petManager.addPet(player.getUniqueId(), currentSecondary, currentSecondaryRarity, 1);
            }
            petManager.setActivePets(player.getUniqueId(), currentPrimary, currentPrimaryRarity, pet, rarity);
        }
        
        petManager.removePet(player.getUniqueId(), pet, rarity, 1);
        openGUI();
    }
    
    public void deactivatePet(boolean isPrimary) {
        Map<String, String> currentActive = petManager.getActivePets(player.getUniqueId());
        
        Pet currentPrimary = currentActive.containsKey("primary_pet") ? Pet.valueOf(currentActive.get("primary_pet")) : null;
        Rarity currentPrimaryRarity = currentActive.containsKey("primary_rarity") ? Rarity.valueOf(currentActive.get("primary_rarity")) : null;
        
        Pet currentSecondary = currentActive.containsKey("secondary_pet") ? Pet.valueOf(currentActive.get("secondary_pet")) : null;
        Rarity currentSecondaryRarity = currentActive.containsKey("secondary_rarity") ? Rarity.valueOf(currentActive.get("secondary_rarity")) : null;
        
        if (isPrimary && currentPrimary != null && currentPrimaryRarity != null) {
            petManager.addPet(player.getUniqueId(), currentPrimary, currentPrimaryRarity, 1);
            petManager.setActivePets(player.getUniqueId(), null, null, currentSecondary, currentSecondaryRarity);
        } else if (!isPrimary && currentSecondary != null && currentSecondaryRarity != null) {
            petManager.addPet(player.getUniqueId(), currentSecondary, currentSecondaryRarity, 1);
            petManager.setActivePets(player.getUniqueId(), currentPrimary, currentPrimaryRarity, null, null);
        }
        openGUI();
    }
}

