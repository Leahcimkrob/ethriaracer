package de.leahcimkrob.ethriaracer;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BoostPlate {
    private final int id;
    private BoosterType type;
    private double modifier;
    private final int duration;
    private final String tap;
    private final String location;
    private final String key;

    public BoostPlate(int id, BoosterType type, double modifier, int duration, String tap, String location, String key) {
        this.id = id;
        this.type = type;
        this.modifier = modifier;
        this.duration = duration;
        this.tap = tap;
        this.location = location;
        this.key = key;
    }

    public int getId() { return id; }
    public BoosterType getType() { return type; }
    public double getModifier() { return modifier; }
    public int getDuration() { return duration; }
    public String getTap() { return tap; }
    public String getLocation() { return location; }
    public String getKey() { return key; }

    public void nextType() {
        this.type = this.type.next();
    }

    public void prevType() {
        this.type = this.type.prev();
    }

    public void setType(BoosterType type) {
        this.type = type;
    }
    public void increaseModifier() {
        this.modifier += 0.1; // oder ein anderes Schrittmaß
    }

    public void decreaseModifier() {
        this.modifier -= 0.1; // ggf. mit Minimum absichern
    }

    public ItemStack toItemStack() {
        Material mat = Material.valueOf(tap);
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§eBoostPlate #" + id + " (" + type + ")");
        List<String> lore = new ArrayList<>();
        lore.add("Modifier: " + modifier);
        lore.add("Dauer: " + duration + "s");
        lore.add("Ort: " + location);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}