package de.leahcimkrob.ethriaracer;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
    private final Map<String, String> messages = new HashMap<>();
    private final String languageCode;
    private final EthriaRacer plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public LanguageManager(EthriaRacer plugin, String languageCode) {
        this.plugin = plugin;
        this.languageCode = languageCode;
        loadLanguage(languageCode);
    }

    private void loadLanguage(String lang) {
        File langFile = new File(plugin.getDataFolder(), "lang" + File.separator + lang + ".yml");
        if (!langFile.exists()) {
            plugin.getLogger().warning("Language file not found: " + langFile.getName() + ", using fallback en_EN.yml!");
            langFile = new File(plugin.getDataFolder(), "lang" + File.separator + "en_EN.yml.yml");
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(langFile);
        loadSection("", config);
    }

    private void loadSection(String prefix, YamlConfiguration section) {
        for (String key : section.getKeys(false)) {
            Object value = section.get(key);
            if (value instanceof YamlConfiguration) {
                loadSection(prefix + key + ".", (YamlConfiguration) value);
            } else if (value instanceof String) {
                messages.put(prefix + key, (String) value);
            } else if (value instanceof org.bukkit.configuration.ConfigurationSection) {
                loadSection(prefix + key + ".", (YamlConfiguration) value);
            }
        }
    }

    /**
     * Gibt die Nachricht als MiniMessage-Komponente zurück, ersetzt {0}, {1}, ...
     */
    public Component mm(String key, Object... args) {
        String msg = messages.getOrDefault(key, key);
        for (int i = 0; i < args.length; i++) {
            msg = msg.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return miniMessage.deserialize(msg);
    }

    /**
     * Für Fälle, wo du nur den Text brauchst (z.B. für Logging)
     */
    public String raw(String key, Object... args) {
        String msg = messages.getOrDefault(key, key);
        for (int i = 0; i < args.length; i++) {
            msg = msg.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return msg;
    }
}