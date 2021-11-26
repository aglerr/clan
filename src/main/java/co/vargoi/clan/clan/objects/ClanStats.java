package co.vargoi.clan.clan.objects;

import co.vargoi.clan.database.mysql.SQLHelper;
import me.aglerr.lazylibs.libs.Executor;

public class ClanStats {

    private final String uuid;
    private int balance;
    private int points;

    public ClanStats(String uuid, int balance, int points){
        this.uuid = uuid;
        this.balance = balance;
        this.points = points;
    }

    public String getClanUUID() {
        return uuid;
    }

    public int getBalance(){
        return balance;
    }

    public void setBalance(int amount){
        this.balance = amount;
    }

    public void addBalance(int amount){
        this.balance = this.balance + amount;
    }

    public void removeBalance(int amount){
        this.balance = this.balance - amount;
    }

    public int getPoints(){
        return points;
    }

    public void setPoints(int amount){
        this.points = amount;
    }

    public void addPoints(int amount){
        this.points = this.points + amount;
    }

    public void removePoints(int amount){
        this.points = this.points - amount;
    }

    public void saveAsync(){
        Executor.async(() -> SQLHelper.save(this));
    }

}
