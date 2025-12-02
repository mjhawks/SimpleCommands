package me.nuttist.simpleCommands.util;

import me.nuttist.simpleCommands.core.SimpleCommands;

public class eloCalculator {
    private SimpleCommands sc;
    private int pointDecayTime;

    public eloCalculator(SimpleCommands simpleCommands){
        sc = simpleCommands;
        pointDecayTime = sc.getHoursToDecrementPoint();

    }
    public int DegredateOldEloAndAddNewElo(int oldAmount/*last registered elo*/, int newAmount/*elo to add*/, int oldTimePlayed/*last time registered*/, int newTimePlayed/*current time played*/){
        int timeSince = newTimePlayed-oldTimePlayed;
        int timeToDecrease1point = pointDecayTime * 72000;
        return(Math.max(0, oldAmount-(timeSince/timeToDecrease1point)) + newAmount);
    }
    public int DegredateElo(int oldAmount, int oldTimePlayed, int newTimePlayed){
        int timeSince = newTimePlayed-oldTimePlayed;
        int timeToDecrease1point = pointDecayTime * 72000;
        return(Math.max(0, oldAmount-(timeSince/timeToDecrease1point)));
    }
}
