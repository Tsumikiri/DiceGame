package com.example.ew8217sh.dicegame;

import java.util.ArrayList;

public class DiceGamePlayer {

    private String name;
    private int points;
    private int tries;
    private boolean clearable;
    public int[] dice;
    public boolean saved6, saved5, saved4;

    /**
     * @param p An ArrayList of DiceGamePlayers
     * @return A comma-delimited list of the names of the given DiceGamePlayers
     */
    public static String getPlayersArrayListAsString(ArrayList<DiceGamePlayer> p) {
        String s = "";
        for (int i = 0; i < p.size(); i++) {
            if (i > 0) {
                s += ", ";
            }
            s += p.get(i);
        }
        return s;
    }

    @Override
    public String toString() {
        return name;
    }

    DiceGamePlayer(String n) {
        name = n;
        points = 0;
        tries = 0;
        clearable = true;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int p) {
        points = p;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        name = n;
    }

    public int getTries() {
        return tries;
    }

    public void incrementTries() {
        tries++;
    }

    public void resetTries() {
        tries = 0;
    }

    public void setClearable(boolean b) {
        clearable = b;
    }

    public boolean isClearable() {
        return clearable;
    }

}
