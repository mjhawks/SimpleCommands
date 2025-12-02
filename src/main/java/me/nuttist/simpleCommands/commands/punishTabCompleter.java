package me.nuttist.simpleCommands.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class punishTabCompleter implements TabCompleter {
    private List<String> punishOptions = new ArrayList<>(Arrays.asList("xray","theft","screentime","abusivecoms","massgrief","custom"));

    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {

        if(args.length == 1){
            List<String> arg1options = new ArrayList<>();
            String senderName = "";
            if(commandSender instanceof Player){
                senderName = commandSender.getName();
            }
            for(Player p: Bukkit.getOnlinePlayers()){
                if(p!=commandSender) {
                    arg1options.add(p.getName());
                }
            }
            return arg1options;
        }
        else if(args.length == 2){
            return punishOptions;
        }
        return null;
    }
}
