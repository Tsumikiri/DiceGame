package com.example.ew8217sh.dicegame;

import android.app.Application;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DiceGameApplication extends Application {

    private ArrayList<DiceGamePlayer> players; //This list of players

    public ArrayList<DiceGamePlayer> getPlayers() {
        if (players == null) {
            players = new ArrayList<DiceGamePlayer>();
        }
        return players;
    }

    /**
     * Loops over the list of players, for each player:
     *   If the player is clearable, then remove it from the list
     *   Else, reset that player's tries and score and make that player clearable
     */
    public void clearPlayers() {
        if (players != null) {
            for (int i = players.size() - 1; i >= 0; i--) {
                if (players.get(i).isClearable()) {
                    players.remove(i);
                } else {
                    players.get(i).resetTries();
                    players.get(i).setPoints(0);
                    players.get(i).setClearable(true);
                }
            }
        }
    }

    /**
     * Uses an ArrayList to account for ties
     * @return An ArrayList of DiceGamePlayer objects containing only the player(s) with the highest score
     */
    public ArrayList<DiceGamePlayer> getHighScorePlayers() {
        ArrayList<DiceGamePlayer> highScorePlayers = new ArrayList<DiceGamePlayer>();
        for (int i = 0; i < players.size(); i++) {
            if (highScorePlayers.size() <= 0 || players.get(i).getPoints() == highScorePlayers.get(0).getPoints()) {
                highScorePlayers.add(players.get(i));
            } else if (players.get(i).getPoints() > highScorePlayers.get(0).getPoints()) {
                highScorePlayers.clear();
                highScorePlayers.add(players.get(i));
            }
        }
        return highScorePlayers;
    }

    /**
     * @return The players as a comma-delimited string of names
     */
    public String getPlayersAsString() {
        return DiceGamePlayer.getPlayersArrayListAsString(players);
    }

}
