package de.leahcimkrob.ethriaracer.command;

import de.leahcimkrob.ethriaracer.EthriaRacer;
import de.leahcimkrob.ethriaracer.EthriaRacerGUIManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EthriaRacerCommand implements CommandExecutor {

    private final EthriaRacer plugin;
    private final EthriaRacerGUIManager guiManager;

    public EthriaRacerCommand(EthriaRacer plugin, EthriaRacerGUIManager guiManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7Verwende: §e/ethriaracer reload §7oder §e/ethriaracer edit");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reloadPlugin();
                sender.sendMessage("§aEthriaRacer wurde neu geladen.");
                return true;
            case "edit":
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§cNur Spieler können die GUI öffnen.");
                    return true;
                }
                guiManager.openPlatesOverview(player);
                return true;
            default:
                sender.sendMessage("§7Unbekannter Unterbefehl: §e" + args[0]);
                return true;
        }
    }
}