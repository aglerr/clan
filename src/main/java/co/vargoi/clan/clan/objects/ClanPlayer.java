package co.vargoi.clan.clan.objects;

import co.vargoi.clan.database.mysql.SQLHelper;
import co.vargoi.clan.enums.ClanRank;
import me.aglerr.lazylibs.libs.Executor;

public class ClanPlayer {

    private final String name;
    private String uuid;
    private ClanRank rank;

    public ClanPlayer(String name, String uuid, ClanRank rank){
        this.name = name;
        this.uuid = uuid;
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public String getClanUUID() {
        return uuid;
    }

    public void setClanUUID(String uuid){
        this.uuid = uuid;
    }

    public ClanRank getRank() {
        return rank;
    }

    public void setRank(ClanRank rank){
        this.rank = rank;
    }

    public void saveAsync(){
        Executor.async(() -> SQLHelper.save(this));
    }

    public void info(){
        System.out.println("name=" + name + ",clanuuid=" + uuid + ",rank=" + rank);
    }

}
