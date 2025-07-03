package de.leahcimkrob.ethriaracer.listener;

import de.leahcimkrob.ethriaracer.EthriaRacer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.PressurePlate;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class BoostPlateCreateListener implements Listener {

    private final EthriaRacer plugin;

    public BoostPlateCreateListener(EthriaRacer plugin) {
        this.plugin = plugin;
    }

    /**
     * Spieler klickt mit Creator-Stick auf eine Druckplatte.
     * Platte wird gespeichert und darf dabei nicht abgebaut werden,
     * selbst im Creativ-Modus!
     */
    @EventHandler
    public void onPlayerUseCreatorStick(PlayerInteractEvent event) {
        // Nur Main Hand und Rechtsklick auf Block
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (!plugin.isCreatorStick(item)) return;
        Block block = event.getClickedBlock();
        if (block == null || !(block.getBlockData() instanceof PressurePlate)) return;

        // Platte darf beim Festlegen NICHT abgebaut werden!
        event.setCancelled(true);

        String tap = block.getType().name();
        String location = block.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ();

        // Defaultwerte (anpassbar)
        String type = "SPEED";
        double modifier = 2.0;
        int duration = 5;

        plugin.addBoostPlate(type, modifier, duration, tap, location);

        event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<green>Boost-Platte an Position gespeichert!</green>"));
    }

    /**
     * Boost-Platten dürfen von niemandem abgebaut werden, wenn sie in boost.yml stehen.
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!(block.getBlockData() instanceof PressurePlate)) return;
        File file = new File(plugin.getDataFolder(), "boost.yml");
        if (!file.exists()) return;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        if (!yaml.contains("boosts")) return;
        String loc = block.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ();
        for (Object o : yaml.getList("boosts")) {
            if (o instanceof java.util.Map<?,?> map) {
                Object l = map.get("location");
                if (loc.equals(l)) {
                    event.setCancelled(true);
                    Player player = event.getPlayer();
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Boost-Platten können nicht abgebaut werden!</red>"));
                    break;
                }
            }
        }
    }
}