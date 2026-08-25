package com.petsplugin.abilities;

import com.petsplugin.PetsPlugin;
import com.petsplugin.database.PetManager;
import com.petsplugin.enums.Pet;
import com.petsplugin.enums.Rarity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class PetAbilityManager {
    
    private final PetManager petManager;
    
    public PetAbilityManager(PetsPlugin plugin) {
        this.petManager = plugin.getPetManager();
    }
    
    public void applyCombatAbilities(Player attacker, Player defender, EntityDamageByEntityEvent event) {
        Map<String, String> attackerPets = petManager.getActivePets(attacker.getUniqueId());
        
        if (attackerPets.get("primary_pet") != null) {
            try {
                applyPetAbility(attacker, Pet.valueOf(attackerPets.get("primary_pet")), Rarity.valueOf(attackerPets.get("primary_rarity")), event);
            } catch (IllegalArgumentException ignored) {}
        }
        if (attackerPets.get("secondary_pet") != null) {
            try {
                applyPetAbility(attacker, Pet.valueOf(attackerPets.get("secondary_pet")), Rarity.valueOf(attackerPets.get("secondary_rarity")), event);
            } catch (IllegalArgumentException ignored) {}
        }
        
        if (defender != null) {
            Map<String, String> defenderPets = petManager.getActivePets(defender.getUniqueId());
            if (defenderPets.get("primary_pet") != null) {
                try {
                    applyDefenseAbility(defender, Pet.valueOf(defenderPets.get("primary_pet")), Rarity.valueOf(defenderPets.get("primary_rarity")), event);
                } catch (IllegalArgumentException ignored) {}
            }
            if (defenderPets.get("secondary_pet") != null) {
                try {
                    applyDefenseAbility(defender, Pet.valueOf(defenderPets.get("secondary_pet")), Rarity.valueOf(defenderPets.get("secondary_rarity")), event);
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }
    
    private void applyPetAbility(Player player, Pet pet, Rarity rarity, EntityDamageByEntityEvent event) {
        double stat = pet.getStat(rarity);
        switch (pet) {
            case WOLF -> event.setDamage(event.getDamage() * (1 + stat / 100));
            case SKELETON -> {
                if (event.getDamager() instanceof org.bukkit.entity.Projectile) {
                    event.setDamage(event.getDamage() * (1 + stat / 100));
                }
            }
            case PIG -> {
                if (Math.random() * 100 < stat) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, (int)(stat / 2), false, false));
                }
            }
            default -> {}
        }
    }
    
    private void applyDefenseAbility(Player player, Pet pet, Rarity rarity, EntityDamageByEntityEvent event) {
        double stat = pet.getStat(rarity);
        switch (pet) {
            case GOLEM -> event.setDamage(event.getDamage() * (1 - Math.abs(stat) / 100));
            case ENDERMAN -> {
                if (event.getDamager() instanceof org.bukkit.entity.Projectile && Math.random() * 100 < stat) {
                    event.setCancelled(true);
                    player.sendMessage("§aYour Enderman Pet avoided the projectile!");
                }
            }
            default -> {}
        }
    }
    
    public void checkDeathSave(Player player) {
        Map<String, String> activePets = petManager.getActivePets(player.getUniqueId());
        for (String petKey : new String[]{"primary_pet", "secondary_pet"}) {
            if (activePets.get(petKey) != null) {
                try {
                    Pet pet = Pet.valueOf(activePets.get(petKey));
                    Rarity rarity = Rarity.valueOf(activePets.get(petKey.replace("pet", "rarity")));
                    if (pet == Pet.TOTEM && Math.random() * 100 < pet.getStat(rarity)) {
                        player.setHealth(player.getMaxHealth());
                        player.sendMessage("§aYour Totem Pet saved you from death!");
                        player.playSound(player.getLocation(), org.bukkit.Sound.ITEM_TOTEM_USE, 1.0f, 1.0f);
                        return;
                    }
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }
}

