package co.vargoi.clan.clan.objects;

import co.vargoi.clan.database.mysql.SQLHelper;
import co.vargoi.clan.enums.ClanRank;
import me.aglerr.lazylibs.libs.Executor;

public class ClanPlayer {

    private final String name;
    private final String uuid;
    private final ClanRank rank;

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

    public ClanRank getRank() {
        return rank;
    }

    public void saveAsync(){
        Executor.async(() -> SQLHelper.save(this));
    }

}
