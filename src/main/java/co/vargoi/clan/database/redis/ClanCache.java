package co.vargoi.clan.database.redis;

import co.vargoi.clan.clan.objects.ClanPlayer;
import co.vargoi.clan.clan.objects.ClanStats;
import co.vargoi.clan.database.mysql.SQLHelper;
import co.vargoi.clan.enums.ClanRank;
import co.vargoi.clan.exceptions.ClanStatsException;
import com.avaje.ebean.SqlUpdate;
import org.bukkit.event.Listener;
import redis.clients.jedis.Jedis;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class ClanCache implements Listener {

    public static final String CACHE_CLAN = "cache::clan::";
    public static final String CACHE_CLAN_BEDWARS = "cache::clanbedwars::";
    public static final String CACHE_CLAN_PLAYER = "cache::clanplayer::";
    public static final String CACHE_CLAN_STATS = "cache::clanstats::";

    private final Map<String, ClanPlayer> clanPlayerMap = new HashMap<>();

    private final RedisHandler redisHandler;
    public ClanCache(RedisHandler redisHandler){
        this.redisHandler = redisHandler;
    }

    public void loadPlayerSync(String name){
        try(Jedis jedis = redisHandler.getJedisPool().getResource()){
            String key = CACHE_CLAN_PLAYER + name;
            if(jedis.hgetAll(key).isEmpty()){
                // User's data on redis is empty, load it from mysql
                String condition = "SELECT name FROM `" + SQLHelper.PLAYER_TABLE + "` WHERE " +
                        "name=\"" + name + "\";";
                if(SQLHelper.doesConditionExist(condition)){
                    // Load from mysql
                    SQLHelper.executeQuery(
                            "SELECT * FROM `" + SQLHelper.PLAYER_TABLE + "` WHERE name=\"" + name + "\";",
                            resultSet -> {
                                String clanUUID = resultSet.getString("uuid");
                                String playerName = resultSet.getString("name");
                                ClanRank clanRank = ClanRank.getRank(resultSet.getString("rank"));

                                ClanPlayer clanPlayer = new ClanPlayer(playerName, clanUUID, clanRank);
                                // Save the player data on redis cache
                                jedis.hset(key, "uuid", clanUUID);
                                jedis.hset(key, "rank", clanRank == null ? null : clanRank.name());
                                // Finally, put it on the local hashmap
                                this.clanPlayerMap.put(name, clanPlayer);
                            }
                    );
                } else {
                    // Create a new data
                    ClanPlayer clanPlayer = new ClanPlayer(name, null, null);
                    // Save the player data on redis cache
                    jedis.hset(key, "uuid", null);
                    jedis.hset(key, "rank", null);
                    // Finally, put it on the local hashmap
                    this.clanPlayerMap.put(name, clanPlayer);
                }
            } else {
                // The data is exist on redis cache, so load it from there
                ClanPlayer clanPlayer = serializeFromRedis(name, jedis.hgetAll(key));
                // Put it on the local hashmap
                clanPlayerMap.put(name, clanPlayer);
            }
        }
    }

    private ClanPlayer serializeFromRedis(String name, Map<String, String> playerMap){
        String clanUUID = playerMap.get("uuid");
        ClanRank clanRank = ClanRank.getRank(playerMap.get("rank"));
        return new ClanPlayer(name, clanUUID, clanRank);
    }


}
