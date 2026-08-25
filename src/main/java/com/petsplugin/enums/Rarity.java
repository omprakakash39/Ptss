package com.petsplugin.enums;

import org.bukkit.ChatColor;

public enum Rarity {
    REGULAR(ChatColor.WHITE, ChatColor.BOLD, "Regular"),
    GOLD(ChatColor.GOLD, ChatColor.BOLD, "Gold"),
    RAINBOW(ChatColor.WHITE, ChatColor.BOLD, "Rainbow"),
    SHINY(ChatColor.AQUA, ChatColor.BOLD, "Shiny");
    
    private final ChatColor color;
    private final ChatColor bold;
    private final String name;
    
    Rarity(ChatColor color, ChatColor bold, String name) {
        this.color = color;
        this.bold = bold;
        this.name = name;
    }
    
    public String getFormattedName() { return bold + color + name; }
    public String getName() { return name; }
    public ChatColor getColor() { return color; }
}

