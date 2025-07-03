package de.leahcimkrob.ethriaracer.command;

import de.leahcimkrob.ethriaracer.EthriaRacer;
import de.leahcimkrob.ethriaracer.EthriaRacerGUIManager;
import de.leahcimkrob.ethriaracer.LanguageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EthriaRacerCommand implements CommandExecutor {

    private final EthriaRacer plugin;
    private final EthriaRacerGUIManager guiManager;
    private final LanguageManager language;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public EthriaRacerCommand(EthriaRacer plugin, EthriaRacerGUIManager guiManager, LanguageManager language) {
        this.plugin = plugin;
        this.guiManager = guiManager;
        this.language = language;
    }

    private String getPrefixRaw() {
        return plugin.getConfig().getString("prefix", "");
    }

    private Component getPrefix() {
        return mm.deserialize(getPrefixRaw());
    }

    private void send(CommandSender sender, String key, Object... args) {
        Component out = getPrefix().append(language.mm(key, args));
        sender.sendMessage(out);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            send(sender, "command.usage");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reloadAll();
                send(sender, "command.reload");
                return true;
            case "edit":
                if (!(sender instanceof Player player)) {
                    send(sender, "error.only_player");
                    return true;
                }
                guiManager.openPlatesOverview(player);
                return true;
            case "create":
                if (!(sender instanceof Player player)) {
                    send(sender, "error.only_player");
                    return true;
                }
                giveCreatorStick(player);
                send(sender, "command.create.given");
                return true;
            default:
                send(sender, "command.unknown", args[0]);
                return true;
        }
    }

    private void giveCreatorStick(Player player) {
        ItemStack stick = new ItemStack(Material.STICK, 1);
        ItemMeta meta = stick.getItemMeta();
        meta.displayName(mm.deserialize("<gold>Boost-Platten-Ersteller</gold>"));
        meta.setUnbreakable(true);
        stick.setItemMeta(meta);
        stick.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.FIRE_ASPECT, 1);
        plugin.markAsCreatorStick(stick); // Markiert den Stick mit persistentem Tag
        player.getInventory().addItem(stick);
    }
}