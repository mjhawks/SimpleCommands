package me.nuttist.simpleCommands.database;

import me.nuttist.simpleCommands.core.SimpleCommands;
import me.nuttist.simpleCommands.util.PlayerActions;
import me.nuttist.simpleCommands.util.eloCalculator;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.sql.*;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

public class PlayerEloDB {

    public int scoreToBan;
    public SimpleCommands sc;

    private final Connection connection;
    public PlayerEloDB(String path, SimpleCommands simpleCommands) throws SQLException {
        sc = simpleCommands;
        scoreToBan = sc.getBanThreshold();
        connection = DriverManager.getConnection("jdbc:sqlite:"+path);
        Statement statement = connection.createStatement();
        statement.execute(
                """
                CREATE TABLE IF NOT EXISTS elo(
                uuid TEXT UNIQUE NOT NULL,
                scorefactor INT NOT NULL,
                playtimewhenupdated INT NOT NULL,
                reason TEXT
                )
                """);
        statement.close();
    }


    public void closeConnection()   throws SQLException{
        if(connection!= null && !connection.isClosed()){
            connection.close();
        }
    }

    public String getPlayerData(UUID uuid)throws SQLException{
        String reason ="";
        boolean hasEntry = false;
        int oldTime = 0;
        int oldScore = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM elo WHERE uuid = ?"
        )) {
            preparedStatement.setString(1, uuid.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    hasEntry = true;
                    oldScore = resultSet.getInt("scorefactor");
                    oldTime = resultSet.getInt("playtimewhenupdated");
                    reason = resultSet.getString("reason");
                }
            }
        }
        if(hasEntry){
            int newVal = sc.getEloCalc().DegredateElo(oldScore, oldTime, Bukkit.getOfflinePlayer(uuid).getStatistic(Statistic.PLAY_ONE_MINUTE));
            return "player's current elo is "+newVal+ " and previous punishment reasons are: \""+ reason+"\"";
        }
        return "player has no infractions on record or has not played";
    }

    public void punishPlayer(UUID uuid, int amount, String punishReason, int tempBanHours)throws SQLException{
        //check if player exists on database, and pull record if they do.
        boolean inTable = false;
        int oldScore = 0;
        int oldTime = 0;

        //score and reason initialized here to be available for ban check at end
        int newScore = amount;
        String newReason = punishReason;

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM elo WHERE uuid = ?"
        )) {
            preparedStatement.setString(1, uuid.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    inTable = true;
                    oldScore = resultSet.getInt("scorefactor");
                    oldTime = resultSet.getInt("playtimewhenupdated");
                    newReason = resultSet.getString("reason");
                }
            }
        }

        int newTimePlayed = 0;
        Player online = Bukkit.getPlayer(uuid);
        if (online != null) {
            newTimePlayed = online.getStatistic(Statistic.PLAY_ONE_MINUTE);
        }
        else{
            newTimePlayed = Bukkit.getOfflinePlayer(uuid).getStatistic(Statistic.PLAY_ONE_MINUTE);
        }

        //if there is a record we need to calculate their new elo and update it
        if(inTable){
            //update player
            try(PreparedStatement preparedStatement = connection.prepareStatement("""
                UPDATE elo SET scorefactor = ?, playtimewhenupdated = ?, reason = ? WHERE uuid = ?
                """)){
                preparedStatement.setString(4,uuid.toString());

                newScore = sc.getEloCalc().DegredateOldEloAndAddNewElo(oldScore, amount, oldTime,newTimePlayed);
                preparedStatement.setInt(1, newScore);
                preparedStatement.setInt(2, newTimePlayed);


                if(!newReason.contains(punishReason)){
                    newReason = newReason.isEmpty() ? punishReason : newReason + ", " + punishReason;
                }
                preparedStatement.setString(3, newReason);
                preparedStatement.executeUpdate();
            }
        }
        //if there is not a record, simply add one with this data
        else{
            //add player
            try(PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO elo (uuid, scorefactor, playtimewhenupdated, reason) VALUES (?, ?, ?, ?)")) {
                preparedStatement.setString(1,uuid.toString());
                preparedStatement.setInt(2,amount);
                preparedStatement.setInt(3,newTimePlayed);
                preparedStatement.setString(4, punishReason);
                preparedStatement.executeUpdate();
            }
        }
        if(newScore > scoreToBan){
            //ban requirements have been met
            PlayerActions.BanPlayer(uuid,newReason);
        }
        else if( tempBanHours > 0){
            PlayerActions.BanPlayer(uuid, punishReason, tempBanHours);
        }

    }






}
