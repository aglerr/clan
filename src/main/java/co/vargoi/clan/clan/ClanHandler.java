package co.vargoi.clan.clan;

import co.vargoi.clan.database.redis.ClanCache;

public class ClanHandler {

    private final ClanCache clanCache;
    public ClanHandler(ClanCache clanCache){
        this.clanCache = clanCache;
    }

}
