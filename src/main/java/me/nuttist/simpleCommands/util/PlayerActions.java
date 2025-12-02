package me.nuttist.simpleCommands.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerActions {

    private final JavaPlugin plugin;
    private final File file;
    private final Gson gson = new Gson();
    private Set<UUID> pendingClear;

    public PlayerActions(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "pending_clear.json");
        LoadInventoryClearList();
    }

    public static void BanPlayer(UUID uuid, String Reason){
        OfflinePlayer off = Bukkit.getOfflinePlayer(uuid);
        Bukkit.getBanList(BanList.Type.NAME).addBan(off.getName(), Reason, (Date)null, "SimpleCommands plugin");

        if (off.isOnline()) {
            off.getPlayer().kickPlayer(Reason);
        }
    }

    public static void BanPlayer(UUID uuid, String Reason, int duration/*in hours*/){
        Date expires = new Date(System.currentTimeMillis() + (duration * 3600000));
        OfflinePlayer off = Bukkit.getOfflinePlayer(uuid);
        Bukkit.getBanList(BanList.Type.NAME).addBan(off.getName(), Reason, expires, "SimpleCommands plugin");

        if (off.isOnline()) {
            off.getPlayer().kickPlayer(Reason);
        }

    }

    public void addPlayerToInventoryClearList(UUID uuid) {
        pendingClear.add(uuid);
        SaveInventoryClearList();
    }

    public boolean InventoryClearListContains(UUID uuid) {
        return pendingClear.contains(uuid);
    }

    public void InventoryClearListRemove(UUID uuid) {
        pendingClear.remove(uuid);
        SaveInventoryClearList();
    }

    private void LoadInventoryClearList() {
        if (!file.exists()) {
            pendingClear = new HashSet<>();
            SaveInventoryClearList();
            return;
        }

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Set<UUID>>() {}.getType();
            pendingClear = gson.fromJson(reader, type);
            if (pendingClear == null) pendingClear = new HashSet<>();
        } catch (Exception e) {
            e.printStackTrace();
            pendingClear = new HashSet<>();
        }
    }

    private void SaveInventoryClearList() {
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(pendingClear, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
