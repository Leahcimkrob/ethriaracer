package de.leahcimkrob.ethriaracer;

import de.leahcimkrob.ethriaracer.listener.BoostPlateListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EthriaRacerGUIManager implements Listener {
    private final EthriaRacer plugin;
    private final BoostPlateListener.BoostPlateManager plateManager;
    private final LanguageManager language;

    // Track which player is editing which plate
    // Key: Player UUID, Value: Plate Key (e.g. "world:x:y:z")
    private final Map<UUID, String> editing = new java.util.HashMap<>();

    public EthriaRacerGUIManager(EthriaRacer plugin, BoostPlateListener.BoostPlateManager plateManager, LanguageManager language) {
        this.plugin = plugin;
        this.plateManager = plateManager;
        this.language = language;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openPlatesOverview(Player player) {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection guiSec = config.getConfigurationSection("gui.main");
        int size = 27; // Optional: aus Config machen

        Inventory inv = Bukkit.createInventory(null, size, language.get("gui.title.main"));

        // Platten anzeigen
        List<String> plateSlots = guiSec.getStringList("plate_slots");
        List<BoostPlate> plates = plateManager.getAllPlates();

        for (int i=0; i<plates.size() && i<plateSlots.size(); i++) {
            int slot = Integer.parseInt(plateSlots.get(i));
            inv.setItem(slot, plates.get(i).toItemStack());
        }

        // Exit-Button
        ConfigurationSection exitSec = guiSec.getConfigurationSection("exit");
        inv.setItem(exitSec.getInt("slot"), buildButton(exitSec, language.get("gui.exit")));

        player.openInventory(inv);
    }

    public void openPlateEdit(Player player, BoostPlate plate) {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection guiSec = config.getConfigurationSection("gui.edit");
        int size = 27;

        Inventory inv = Bukkit.createInventory(null, size, language.get("gui.title.edit"));

        // Booster-Type
        ConfigurationSection typeSec = guiSec.getConfigurationSection("booster_type");
        ItemStack typeItem = buildButton(typeSec, plate.getType().getDisplayName(language));
        inv.setItem(typeSec.getInt("slot"), typeItem);

        // Modifier
        ConfigurationSection modSec = guiSec.getConfigurationSection("modifier");
        ItemStack modItem = buildButton(modSec, String.valueOf(plate.getModifier()));
        inv.setItem(modSec.getInt("slot"), modItem);

        // Exit-Button (hier: nicht schließbar, kann weggelassen oder als "Zurück"-Button markiert werden)
        ConfigurationSection exitSec = guiSec.getConfigurationSection("exit");
        inv.setItem(exitSec.getInt("slot"), buildButton(exitSec, language.get("gui.exit")));

        editing.put(player.getUniqueId(), plate.getKey());
        player.openInventory(inv);
    }

    private ItemStack buildButton(ConfigurationSection sec, String displayName) {
        Material mat = Material.valueOf(sec.getString("material", "STONE"));
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        // TODO: Skull-Texturen etc. hier einbauen!
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        // Haupt-GUI
        if (title.equals(language.get("gui.title.main"))) {
            event.setCancelled(true);
            int slot = event.getRawSlot();

            FileConfiguration config = plugin.getConfig();
            ConfigurationSection guiSec = config.getConfigurationSection("gui.main");
            ConfigurationSection exitSec = guiSec.getConfigurationSection("exit");
            int exitSlot = exitSec.getInt("slot");

            if (slot == exitSlot) {
                player.closeInventory();
                plugin.reloadPlugin();
                return;
            }

            // Platten-Slot?
            List<String> plateSlots = guiSec.getStringList("plate_slots");
            for (int i=0; i<plateSlots.size(); i++) {
                int ps = Integer.parseInt(plateSlots.get(i));
                if (slot == ps) {
                    BoostPlate plate = plateManager.getPlateByIndex(i);
                    if (plate == null) break;

                    if (event.getClick() == ClickType.LEFT) {
                        openPlateEdit(player, plate);
                    } else if (event.getClick() == ClickType.RIGHT) {
                        plateManager.removePlate(plate);
                        openPlatesOverview(player);
                    }
                    return;
                }
            }
            // Drag n Drop für neue Platten: Optional, z.B. bei ItemMove
        }

        // Bearbeiten-GUI
        if (title.equals(language.get("gui.title.edit"))) {
            event.setCancelled(true);
            UUID uuid = player.getUniqueId();
            if (!editing.containsKey(uuid)) return;
            String plateKey = editing.get(uuid);
            BoostPlate plate = plateManager.getPlateByKey(plateKey);
            if (plate == null) return;

            FileConfiguration config = plugin.getConfig();
            ConfigurationSection guiSec = config.getConfigurationSection("gui.edit");

            int typeSlot = guiSec.getConfigurationSection("booster_type").getInt("slot");
            int modSlot = guiSec.getConfigurationSection("modifier").getInt("slot");
            int exitSlot = guiSec.getConfigurationSection("exit").getInt("slot");

            int slot = event.getRawSlot();
            if (slot == typeSlot) {
                if (event.getClick() == ClickType.LEFT) {
                    plate.nextType();
                } else if (event.getClick() == ClickType.RIGHT) {
                    plate.prevType();
                }
                openPlateEdit(player, plate);
            }
            if (slot == modSlot) {
                if (event.getClick() == ClickType.LEFT) {
                    plate.increaseModifier();
                } else if (event.getClick() == ClickType.RIGHT) {
                    plate.decreaseModifier();
                }
                openPlateEdit(player, plate);
            }

            // Exit/Zurück-Button (GUI darf sich nicht schließen!)
            if (slot == exitSlot) {
                // NICHT schließen!
                event.setCancelled(true);
            }
        }
    }
}