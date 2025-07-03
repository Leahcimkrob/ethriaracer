package de.leahcimkrob.ethriaracer;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BoostPlate {
    private final int id;
    private final String type;
    private final double modifier;
    private final int duration;
    private final String tap;
    private final String location;

    public BoostPlate(int id, String type, double modifier, int duration, String tap, String location) {
        this.id = id;
        this.type = type;
        this.modifier = modifier;
        this.duration = duration;
        this.tap = tap;
        this.location = location;
    }

    public int getId() { return id; }
    public String getType() { return type; }
    public double getModifier() { return modifier; }
    public int getDuration() { return duration; }
    public String getTap() { return tap; }
    public String getLocation() { return location; }

    public ItemStack toItemStack() {
        Material mat = Material.valueOf(tap); // tap sollte z.B. "STONE_PRESSURE_PLATE" sein
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