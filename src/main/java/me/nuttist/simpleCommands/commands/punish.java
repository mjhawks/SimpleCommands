package me.nuttist.simpleCommands.commands;

import me.nuttist.simpleCommands.core.SimpleCommands;
import me.nuttist.simpleCommands.database.PlayerEloDB;
import me.nuttist.simpleCommands.util.PlayerActions;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import java.awt.*;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getOfflinePlayer;

public class punish implements CommandExecutor{
    private final SimpleCommands plugin;
    private int xrayPenalty;
    private int theftPenalty;
    private int screentimePenalty;
    private int hatespeechPenalty;
    private int massgriefPenalty;
    private int customdefaultPenalty;
    private int pointLimit;
    private PlayerEloDB PEDB;
    private Logger logger = getLogger();

    private int xrayTempBanLength = 30*24;
    private int theftTempBanLength = 30*24;
    private int screentimeTempBanLength = 0;

    public punish(SimpleCommands sc){
        plugin = sc;
        xrayPenalty = sc.getXrayPen();
        theftPenalty = sc.getTheftPen();
        screentimePenalty = sc.getScreentimePen();
        hatespeechPenalty = sc.getHatespeechPen();
        massgriefPenalty = sc.getMassgriefPen();
        customdefaultPenalty = sc.getCustomDefaultPen();
        pointLimit = sc.getBanThreshold();
        PEDB = sc.getPEDB();

    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(command.getName().equalsIgnoreCase("punish")) {
            Player p = (Player)commandSender;
            // 1 = punish, 2 = name, 3 = default, *4 = number if 3 = custom
            if(args.length == 0){
                p.sendMessage("Please provide a player and a reason");
                return false;
            }
            else if(args.length == 1){
                p.sendMessage("Please provide a reason");
                return false;
            }
            else if(args.length > 1){
                if(getOfflinePlayer(args[0]).hasPlayedBefore()){
                    UUID playertopunish = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
                    switch(args[1]){
                        case "xray":
                            xray(playertopunish);
                            break;
                        case "theft":
                            theft(playertopunish);
                            break;
                        case "screentime":
                            if(!screentime(playertopunish)){
                                commandSender.sendMessage("Player is offline. Infraction has been added, but no warning was issued");
                            }
                            break;
                        case "hatespeech":
                            hatespeech(playertopunish);
                            break;
                        case "massgrief":
                            massgrief(playertopunish);
                            break;
                        case "custom":
                            int customPenaltySize = customdefaultPenalty;
                            if(args.length >= 3){
                                try {
                                    customPenaltySize = Integer.parseInt(args[2]);
                                } catch (NumberFormatException e) {
                                    // handle invalid number
                                    commandSender.sendMessage("§cInvalid penalty size. please use a number or leave blank for default value of "+customdefaultPenalty);
                                    return false;
                                }
                            }
                            customPenalty(playertopunish, customPenaltySize);
                            break;
                    }
                }
                else{
                    p.sendMessage(args[0] + " has not played on the server");
                }
            }
        }
        return false;
    }

    private boolean xray(UUID uuid){
        try{
            PEDB.punishPlayer(uuid,xrayPenalty, "x-ray", xrayTempBanLength);
        } catch (SQLException e) {
            logger.info("X-ray punish attempt on "+Bukkit.getOfflinePlayer(uuid).getName()+" failed: "+e);
            return false;
        }

        plugin.clearPlayerInventory(uuid);

        return true;

    }
    private boolean theft(UUID uuid){
        try{
            PEDB.punishPlayer(uuid,theftPenalty, "Theft", theftTempBanLength);
        } catch (SQLException e) {
            logger.info("theft punish attempt on "+Bukkit.getOfflinePlayer(uuid).getName()+" failed: "+e);
            return false;
        }

        plugin.clearPlayerInventory(uuid);

        return true;
    }
    private boolean screentime(UUID uuid){
        Player player = Bukkit.getPlayer(uuid);
        try{
            PEDB.punishPlayer(uuid,screentimePenalty, "Screen Time Warrior", screentimeTempBanLength);
        } catch (SQLException e) {
            logger.info("screentime punish attempt on "+Bukkit.getOfflinePlayer(uuid).getName()+" failed: "+e);
            return false;
        }
        if(player != null){
            try{
                player.teleport(Bukkit.getWorld("world").getSpawnLocation());
            } catch (NullPointerException e) {
                logger.info("teleportation of "+Bukkit.getOfflinePlayer(uuid).getName()+" to world spawn failed: "+e);
                return false;
            }

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§aPlease do not crowd the streamer"));

            return true;
        }
        return false;
    }
    private boolean hatespeech(UUID uuid){
        try{
            PEDB.punishPlayer(uuid,hatespeechPenalty, "Abusive Communication", 0);
        } catch (SQLException e) {
            logger.info("hatespeech punish attempt on "+Bukkit.getOfflinePlayer(uuid).getName()+" failed: "+e);
            return false;
        }
        plugin.clearPlayerInventory(uuid);
        return true;
    }
    private boolean massgrief(UUID uuid){
        try{
            PEDB.punishPlayer(uuid,massgriefPenalty, "Grief", 0);
        } catch (SQLException e) {
            logger.info("massgrief punish attempt on "+Bukkit.getOfflinePlayer(uuid).getName()+" failed: "+e);
            return false;
        }
        plugin.clearPlayerInventory(uuid);
        return true;
    }
    private boolean customPenalty(UUID uuid, int amount){
        try{
            PEDB.punishPlayer(uuid, amount, "", 0);
        } catch (SQLException e) {
            logger.info("custom punish attempt on "+Bukkit.getOfflinePlayer(uuid).getName()+" failed: "+e);
            return false;
        }

        return true;
    }


}
