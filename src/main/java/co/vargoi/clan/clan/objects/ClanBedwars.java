package co.vargoi.clan.clan.objects;

import co.vargoi.clan.database.mysql.SQLHelper;
import me.aglerr.lazylibs.libs.Executor;

public class ClanBedwars {

    private final String uuid;
    private int wins;
    private int losses;
    private int kills;
    private int deaths;
    private int bedsDestroyed;
    private int finalKills;

    public ClanBedwars(String uuid, int wins, int losses, int kills, int deaths, int bedsDestroyed, int finalKills) {
        this.uuid = uuid;
        this.wins = wins;
        this.losses = losses;
        this.kills = kills;
        this.deaths = deaths;
        this.bedsDestroyed = bedsDestroyed;
        this.finalKills = finalKills;
    }

    public String getClanUUID() {
        return uuid;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getBedsDestroyed() {
        return bedsDestroyed;
    }

    public void setBedsDestroyed(int bedsDestroyed) {
        this.bedsDestroyed = bedsDestroyed;
    }

    public int getFinalKills() {
        return finalKills;
    }

    public void setFinalKills(int finalKills) {
        this.finalKills = finalKills;
    }

    public void saveAsync(){
        Executor.async(() -> SQLHelper.save(this));
    }

}
