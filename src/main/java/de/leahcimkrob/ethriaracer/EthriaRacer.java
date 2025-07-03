package de.leahcimkrob.ethriaracer;

import de.leahcimkrob.ethriaracer.command.EthriaRacerCommand;
import de.leahcimkrob.ethriaracer.listener.BoostPlateCreateListener;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class EthriaRacer extends JavaPlugin {

    private LanguageManager languageManager;
    private EthriaRacerGUIManager guiManager;

    private NamespacedKey creatorStickKey;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadAll();

        this.creatorStickKey = new NamespacedKey(this, "creator_stick");

        guiManager = new EthriaRacerGUIManager(this);
        getCommand("ethriaracer").setExecutor(new EthriaRacerCommand(this, guiManager, languageManager));

        Bukkit.getPluginManager().registerEvents(new BoostPlateCreateListener(this), this);
    }

    /**
     * Lädt Config und Sprachdateien neu.
     */
    public void reloadAll() {
        // Konfiguration neu laden
        reloadConfig();

        // Sprache neu laden
        String lang = getConfig().getString("language", "de_DE");
        languageManager = new LanguageManager(this, lang);

        // Weitere Manager reload falls nötig
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public EthriaRacerGUIManager getGuiManager() {
        return guiManager;
    }

    public NamespacedKey getCreatorStickKey() {
        return creatorStickKey;
    }

    /**
     * Markiert den Stick per PersistentData als Creator-Stick.
     */
    public void markAsCreatorStick(ItemStack stick) {
        var meta = stick.getItemMeta();
        meta.getPersistentDataContainer().set(creatorStickKey, PersistentDataType.BYTE, (byte)1);
        stick.setItemMeta(meta);
    }

    /**
     * Prüft, ob ein Item der Creator-Stick ist.
     */
    public boolean isCreatorStick(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        var meta = item.getItemMeta();
        Byte data = meta.getPersistentDataContainer().get(creatorStickKey, PersistentDataType.BYTE);
        return data != null && data == 1;
    }

    /**
     * Fügt neue Druckplatte zur boost.yml hinzu.
     */
    public void addBoostPlate(String type, double modifier, int duration, String tap, String location) {
        File file = new File(getDataFolder(), "boost.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        List<?> boosts = yaml.getList("boosts");
        int nextId = 1;
        if (boosts != null && !boosts.isEmpty()) {
            for (Object o : boosts) {
                if (o instanceof java.util.Map<?,?>) {
                    Object idObj = ((java.util.Map<?,?>)o).get("id");
                    if (idObj instanceof Number) {
                        int id = ((Number)idObj).intValue();
                        if (id >= nextId) nextId = id + 1;
                    }
                }
            }
        }

        java.util.Map<String, Object> entry = new java.util.HashMap<>();
        entry.put("id", nextId);
        entry.put("type", type);
        entry.put("modifier", modifier);
        entry.put("duration", duration);
        entry.put("tap", tap);
        entry.put("location", location);

        if (boosts == null) {
            boosts = new java.util.ArrayList<>();
        } else {
            boosts = new java.util.ArrayList<>(boosts);
        }
        ((java.util.List<Object>)boosts).add(entry);

        yaml.set("boosts", boosts);
        try {
            yaml.save(file);
        } catch (IOException e) {
            getLogger().warning("Failed to save boost.yml: " + e.getMessage());
        }
    }
}