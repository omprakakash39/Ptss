package com.petsplugin.enums;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum Pet {
    WOLF("Wolf Pet", "Attack Boost", PetType.SECONDARY, 
        new double[]{5.0, 10.0, 15.0, 20.0}, 
        "+%.2f%% damage", Material.WOLF_SPAWN_EGG),
    
    GOLEM("Golem Pet", "Damage Reduction", PetType.SECONDARY,
        new double[]{-4.0, -8.0, -12.0, -16.0},
        "-%.2f%% damage taken", Material.IRON_GOLEM_SPAWN_EGG),
    
    WITCH("Witch Pet", "Potion Duration", PetType.SECONDARY,
        new double[]{10.0, 20.0, 30.0, 40.0},
        "+%.2f%% potion duration", Material.WITCH_SPAWN_EGG),
    
    VILLAGER("Villager Pet", "Trade Master", PetType.SECONDARY,
        new double[]{4, 5, 6, 7},
        "Hero of the Village %d", Material.VILLAGER_SPAWN_EGG),
    
    SKELETON("Skeleton Pet", "Sharpshooter", PetType.SECONDARY,
        new double[]{10.0, 20.0, 30.0, 40.0},
        "+%.2f%% projectile damage", Material.SKELETON_SPAWN_EGG),
    
    SILVERFISH("Silverfish Pet", "Rich Veins", PetType.SECONDARY,
        new double[]{2.0, 3.0, 4.0, 5.0},
        "%.0fx ore drops + Haste", Material.SILVERFISH_SPAWN_EGG),
    
    BANKER("Banker Pet", "Golden Touch", PetType.SECONDARY,
        new double[]{5.0, 10.0, 15.0, 20.0},
        "+%.2f%% Sell Boost", Material.EMERALD),
    
    TOTEM("Totem Pet", "Second Chance", PetType.SECONDARY,
        new double[]{10.0, 15.0, 20.0, 25.0},
        "%.2f%% save from death", Material.TOTEM_OF_UNDYING),
    
    CREEPER("Creeper Pet", "Blast Proof", PetType.SECONDARY,
        new double[]{-5.0, -10.0, -15.0, -20.0},
        "-%.2f%% explosion damage", Material.CREEPER_SPAWN_EGG),
    
    ENDERMAN("Enderman Pet", "Phase Shift", PetType.SECONDARY,
        new double[]{10.0, 15.0, 20.0, 25.0},
        "%.2f%% avoid projectile damage", Material.ENDERMAN_SPAWN_EGG),
    
    WITHER_SKELETON("Wither Skeleton Pet", "Withering Strike", PetType.SECONDARY,
        new double[]{1.0, 2.0, 3.0, 4.0},
        "%.2f%% chance apply Wither", Material.WITHER_SKELETON_SPAWN_EGG),
    
    PIG("Pig Pet", "Pork Power", PetType.SECONDARY,
        new double[]{0.5, 1.0, 1.5, 2.0},
        "%.2f%% chance Regeneration", Material.PIG_SPAWN_EGG);
    
    private final String displayName;
    private final String abilityName;
    private final PetType petType;
    private final double[] stats; 
    private final String statFormat;
    private final Material material;
    
    Pet(String displayName, String abilityName, PetType petType, double[] stats, 
         String statFormat, Material material) {
        this.displayName = displayName;
        this.abilityName = abilityName;
        this.petType = petType;
        this.stats = stats;
        this.statFormat = statFormat;
        this.material = material;
    }
    
    public String getDisplayName() { return displayName; }
    public String getAbilityName() { return abilityName; }
    public PetType getPetType() { return petType; }
    public double getStat(Rarity rarity) { return stats[rarity.ordinal()]; }
    
    public String getFormattedStat(Rarity rarity) {
        double stat = getStat(rarity);
        if (statFormat.contains("%d")) {
            return String.format(statFormat, (int) stat);
        }
        return String.format(statFormat, stat);
    }
    
    public Material getMaterial() { return material; }
    
    public enum PetType { SECONDARY }
}

