package me.yhamarsheh.bridgersumo.objects;

import me.yhamarsheh.bridgersumo.storage.objects.DabPlayer;

public abstract class Statistics {

    private final DabPlayer dabPlayer;

    private int wins;
    private int kills;
    private int deaths;
    private int winStreak;
    private int gamesPlayed;

    public Statistics(DabPlayer dabPlayer) {
        this.dabPlayer = dabPlayer;
    }

    public void addKill() {
        kills++;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void addWin() {
        wins++;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void addDeath() {
        deaths++;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void addWinStreak() {
        winStreak++;
    }

    public void setWinStreak(int winStreak) {
        this.winStreak = winStreak;
    }
    public void addGamesPlayed() {
        gamesPlayed++;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public DabPlayer getDabPlayer() {
        return dabPlayer;
    }

    public int getWins() {
        return wins;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getWinStreak() {
        return winStreak;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

}
