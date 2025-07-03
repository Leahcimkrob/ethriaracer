package de.leahcimkrob.ethriaracer;

import dev.lone.itemsadder.api.CustomEntity;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class EthriaRacerListener implements Listener {

    private final EthriaRacer plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public EthriaRacerListener(EthriaRacer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        Entity vehicle = event.getVehicle();
        CustomEntity customEntity = CustomEntity.getFromEntity(vehicle);
        if (customEntity == null) return;

        Block under = vehicle.getLocation().subtract(0, 1, 0).getBlock();
        String key = under.getWorld().getName() + ":" + under.getX() + ":" + under.getY() + ":" + under.getZ();

        FileConfiguration boostConfig = plugin.getBoostConfig();
        if (!boostConfig.contains("boosts." + key)) return;

        UUID id = vehicle.getUniqueId();
        long now = System.currentTimeMillis();
        if (cooldowns.containsKey(id) && now - cooldowns.get(id) < 3000) return;

        cooldowns.put(id, now);

        double multiplier = boostConfig.getDouble("boosts." + key + ".modifier", 2.0);
        Vector boosted = vehicle.getVelocity().multiply(multiplier);
        vehicle.setVelocity(boosted);

        new BukkitRunnable() {
            @Override
            public void run() {
                vehicle.setVelocity(vehicle.getVelocity().multiply(0.5));
            }
        }.runTaskLater(plugin, 40L);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("ethriaracer.admin")) return;
        openMainGUI(player);
    }

    public void openMainGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "Boost-Platten Einstellungen");

        ItemStack exit = new ItemStack(Material.IRON_DOOR);
        ItemMeta meta = exit.getItemMeta();
        meta.setDisplayName("Verlassen");
        exit.setItemMeta(meta);
        gui.setItem(22, exit);

        player.openInventory(gui);
    }

    @EventHandler
    public void onGUIClick(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.CHEST) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();
        if (!title.equals("Boost-Platten Einstellungen")) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        if (clicked.getType() == Material.IRON_DOOR) {
            player.closeInventory();
            reloadPlugin();
        }
    }

    private void reloadPlugin() {
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ethriaracer:reload");
        });
    }
}
