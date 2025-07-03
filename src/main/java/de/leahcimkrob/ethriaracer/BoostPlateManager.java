package de.leahcimkrob.ethriaracer;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BoostPlateManager {
    private final EthriaRacer plugin;
    private List<BoostPlate> plates;

    public BoostPlateManager(EthriaRacer plugin) {
        this.plugin = plugin;
        this.plates = new ArrayList<>();
        loadPlates();
    }

    public List<BoostPlate> getAllPlates() {
        return new ArrayList<>(plates);
    }

    public void addBoostPlate(BoostPlate plate) {
        plates.add(plate);
        savePlates();
    }

    public void removeBoostPlateById(int id) {
        plates.removeIf(p -> p.getId() == id);
        savePlates();
    }

    public void loadPlates() {
        plates.clear();
        File file = new File(plugin.getDataFolder(), "boost.yml");
        if (!file.exists()) return;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        List<?> boosts = yaml.getList("boosts");
        if (boosts == null) return;

        for (Object obj : boosts) {
            if (obj instanceof Map<?, ?> map) {
                try {
                    int id = ((Number) map.get("id")).intValue();
                    String type = (String) map.get("type");
                    double modifier = ((Number) map.get("modifier")).doubleValue();
                    int duration = ((Number) map.get("duration")).intValue();
                    String tap = (String) map.get("tap");
                    String location = (String) map.get("location");
                    plates.add(new BoostPlate(id, BoosterType.valueOf(type), modifier, duration, tap, location, String.valueOf(id)));
                } catch (Exception ignored) {}
            }
        }
    }

    public void savePlates() {
        File file = new File(plugin.getDataFolder(), "boost.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        List<Map<String, Object>> boosts = new ArrayList<>();
        for (BoostPlate plate : plates) {
            Map<String, Object> entry = new java.util.HashMap<>();
            entry.put("id", plate.getId());
            entry.put("type", plate.getType());
            entry.put("modifier", plate.getModifier());
            entry.put("duration", plate.getDuration());
            entry.put("tap", plate.getTap());
            entry.put("location", plate.getLocation());
            boosts.add(entry);
        }
        yaml.set("boosts", boosts);
        try {
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save boost.yml: " + e.getMessage());
        }
    }

    // Fügt eine neue BoostPlate hinzu
    public void addPlate(BoostPlate plate) {
        plates.add(plate);
    }
    // Holt eine BoostPlate anhand ihres Index (z.B. für die GUI)
    public BoostPlate getPlateByIndex(int index) {
        if (index < 0 || index >= plates.size()) {
            return null;
        }
        return plates.get(index);
    }

    // Holt eine BoostPlate anhand ihres Keys (optional, falls benötigt)
    public BoostPlate getPlateByKey(String key) {
        for (BoostPlate plate : plates) {
            if (plate.getKey().equals(key)) {
                return plate;
            }
        }
        return null;
    }

    // Beispiel: Entfernt eine BoostPlate anhand ihres Keys
    public boolean removePlateByKey(String key) {
        return plates.removeIf(plate -> plate.getKey().equals(key));
    }

}