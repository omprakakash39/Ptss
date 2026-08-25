package com.petsplugin;

import com.petsplugin.commands.PetsCommand;
import com.petsplugin.commands.GiveCommand;
import com.petsplugin.database.PetManager;
import com.petsplugin.listeners.PetListener;
import com.petsplugin.placeholder.PetsPlaceholderExpansion;
import org.bukkit.plugin.java.JavaPlugin;

public class PetsPlugin extends JavaPlugin {
    
    private static PetsPlugin instance;
    private PetManager petManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize database
        petManager = new PetManager(this);
        petManager.initialize();
        
        // Register commands
        getCommand("pets").setExecutor(new PetsCommand(this));
        getCommand("give").setExecutor(new GiveCommand(this));
        
        // Register events
        getServer().getPluginManager().registerEvents(new PetListener(this), this);
        
        // Register PlaceholderAPI expansion
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PetsPlaceholderExpansion(this).register();
            getLogger().info("PlaceholderAPI integration enabled!");
        } else {
            getLogger().warning("PlaceholderAPI not found - placeholders will not work!");
        }
        
        getLogger().info("PetsPlugin enabled successfully!");
    }
    
    @Override
    public void onDisable() {
        if (petManager != null) {
            petManager.close();
        }
        getLogger().info("PetsPlugin disabled!");
    }
    
    public static PetsPlugin getInstance() {
        return instance;
    }
    
    public PetManager getPetManager() {
        return petManager;
    }
}

