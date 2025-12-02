package me.nuttist.simpleCommands.commands;

import me.nuttist.simpleCommands.core.SimpleCommands;
import me.nuttist.simpleCommands.database.PlayerEloDB;
import me.nuttist.simpleCommands.util.PlayerActions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class getPlayerStat implements CommandExecutor {
    SimpleCommands plugin;
    PlayerEloDB PEDB;
    public getPlayerStat(SimpleCommands sc){
        plugin = sc;
        PEDB = sc.getPEDB();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        try{
           commandSender.sendMessage(PEDB.getPlayerData(Bukkit.getOfflinePlayer(args[0]).getUniqueId()));
           return true;
        }catch (Exception e){
            commandSender.sendMessage("something went wrong" + e);
        }

        return false;
    }
}
