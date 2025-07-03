package de.leahcimkrob.ethriaracer;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class EthriaRacer extends JavaPlugin {

    private static EthriaRacer instance;
    private File boostFile;
    private FileConfiguration boostConfig;

    private File langFile;
    private FileConfiguration lang;

    @Override
    public void onEnable() {
        instance = this;
        loadConfigs();
        getServer().getPluginManager().registerEvents(new EthriaRaceListener(this), this);
        getLogger().info("Ethria-Racer wurde aktiviert.");
    }

    public static EthriaRacer getInstance() {
        return instance;
    }

    private void loadConfigs() {
        saveDefaultConfig();

        boostFile = new File(getDataFolder(), "boost.yml");
        if (!boostFile.exists()) saveResource("boost.yml", false);
        boostConfig = YamlConfiguration.loadConfiguration(boostFile);

        String langName = getConfig().getString("language", "de_DE");
        langFile = new File(getDataFolder() + "/lang", langName + ".yml");
        if (!langFile.exists()) saveResource("lang/" + langName + ".yml", false);
        lang = YamlConfiguration.loadConfiguration(langFile);
    }

    public FileConfiguration getBoostConfig() {
        return boostConfig;
    }

    public FileConfiguration getLang() {
        return lang;
    }
}

