package me.nuttist.simpleCommands.core;

import me.nuttist.simpleCommands.commands.getPlayerStat;
import me.nuttist.simpleCommands.commands.punish;
import me.nuttist.simpleCommands.commands.punishTabCompleter;
import me.nuttist.simpleCommands.database.PlayerEloDB;
import me.nuttist.simpleCommands.util.PlayerActions;
import me.nuttist.simpleCommands.util.eloCalculator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

public final class SimpleCommands extends JavaPlugin implements Listener {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SimpleCommands.class);
    private Logger logger = getLogger();

    private int xrayPen;
    private int theftPen;
    private int screentimePen;
    private int hatespeechPen;
    private int massgriefPen;
    private int customDefaultPen;
    private int banThreshold;
    private int hoursToDecrementPoint;

    private eloCalculator eloCalc;
    private PlayerActions playerActions;
    private PlayerEloDB PEDB;

    @Override
    public void onEnable() {

        // Plugin startup logic
        saveDefaultConfig();



        xrayPen = getConfig().getInt("penalty-xray");
        theftPen = getConfig().getInt("penalty-theft");
        screentimePen = getConfig().getInt("penalty-screentime");
        hatespeechPen = getConfig().getInt("penalty-hatespeech");
        massgriefPen = getConfig().getInt("penalty-massgrief");
        customDefaultPen = getConfig().getInt("penalty-custom-default");
        banThreshold = getConfig().getInt("ban-threshold");
        hoursToDecrementPoint = getConfig().getInt("hours-to-decrement-point");

        try{
            if(!getDataFolder().exists()){
                getDataFolder().mkdirs();
            }
            PEDB = new PlayerEloDB(getDataFolder().getAbsolutePath()+"/playerelo.db",this);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("player elo Failed to connect to the database "+ e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }
        playerActions = new PlayerActions(this);
        eloCalc = new eloCalculator(this);

        loadCommands();
        getServer().getPluginManager().registerEvents(this,this);


        logger.info("Simple Commands by Nuttist has successfully booted up");

    }

    private void loadCommands(){
        getCommand("punish").setExecutor(new punish(this));
        getCommand("punish").setTabCompleter(new punishTabCompleter());
        getCommand("getPlayerStat").setExecutor(new getPlayerStat(this));
    }

    public void clearPlayerInventory(UUID uuid) {
        Player online = Bukkit.getPlayer(uuid);

        if (online != null && online.isOnline()) {
            online.getInventory().clear();
            online.getEnderChest().clear();
        } else {
            playerActions.addPlayerToInventoryClearList(uuid);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        if (playerActions.InventoryClearListContains(p.getUniqueId())) {
            p.getInventory().clear();
            p.getEnderChest().clear();
            playerActions.InventoryClearListRemove(p.getUniqueId());
        }
    }


    @Override
    public void onDisable() {
        try{
            PEDB.closeConnection();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public int getXrayPen() {
        return xrayPen;
    }

    public int getTheftPen() {
        return theftPen;
    }

    public int getScreentimePen() {
        return screentimePen;
    }

    public int getHatespeechPen() {
        return hatespeechPen;
    }

    public int getMassgriefPen() {
        return massgriefPen;
    }

    public int getCustomDefaultPen() {
        return customDefaultPen;
    }

    public int getBanThreshold() {
        return banThreshold;
    }

    public int getHoursToDecrementPoint() {
        return hoursToDecrementPoint;
    }

    public eloCalculator getEloCalc(){
        return eloCalc;
    }

    public PlayerEloDB getPEDB() {
        return PEDB;
    }


}
