package de.leahcimkrob.ethriaracer.command;

import de.leahcimkrob.ethriaracer.EthriaRacer;
import de.leahcimkrob.ethriaracer.EthriaRacerGUIManager;
import de.leahcimkrob.ethriaracer.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EthriaRacerCommand implements CommandExecutor {

    private final EthriaRacer plugin;
    private final EthriaRacerGUIManager guiManager;
    private final LanguageManager language;

    public EthriaRacerCommand(EthriaRacer plugin, EthriaRacerGUIManager guiManager, LanguageManager language) {
        this.plugin = plugin;
        this.guiManager = guiManager;
        this.language = language;
    }

    private String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix", ""));
    }

    private void send(CommandSender sender, String key, Object... args) {
        String msg = language.get(key, args);
        sender.sendMessage(getPrefix() + ChatColor.translateAlternateColorCodes('&', msg));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            send(sender, "command.usage");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reloadPlugin();
                send(sender, "command.reload");
                return true;
            case "edit":
                if (!(sender instanceof Player player)) {
                    send(sender, "error.only_player");
                    return true;
                }
                guiManager.openPlatesOverview(player);
                return true;
            default:
                send(sender, "command.unknown", args[0]);
                return true;
        }
    }
}