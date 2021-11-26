package co.vargoi.clan.database.redis;

import co.vargoi.clan.clan.objects.ClanPlayer;
import co.vargoi.clan.enums.ClanRank;

import java.util.Map;

public class RedisUtils {

    public static ClanPlayer serializeFromRedis(String name, Map<String, String> playerMap){
        String clanUUID = playerMap.get("uuid").equals("null") ? null : playerMap.get("uuid");
        ClanRank clanRank = ClanRank.getRank(playerMap.get("rank"));
        return new ClanPlayer(name, clanUUID, clanRank);
    }

}
