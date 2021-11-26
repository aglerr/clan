package co.vargoi.clan.database.redis;

import co.vargoi.clan.clan.objects.ClanStats;
import co.vargoi.clan.exceptions.ClanStatsException;
import org.bukkit.event.Listener;
import redis.clients.jedis.Jedis;

import java.sql.ResultSet;

public class ClanCache implements Listener {

    public static final String CACHE_CLAN = "cache::clan::";
    public static final String CACHE_CLAN_BEDWARS = "cache::clanbedwars::";
    public static final String CACHE_CLAN_PLAYER = "cache::clanplayer::";
    public static final String CACHE_CLAN_STATS = "cache::clanstats::";

    private final RedisHandler redisHandler;
    public ClanCache(RedisHandler redisHandler){
        this.redisHandler = redisHandler;
    }

    public void loadClanStats(ResultSet resultSet){
        String uuid = null;
        int balance = 0;
        int points = 0;

        try(Jedis jedis = redisHandler.getJedisPool().getResource()){
            jedis.auth(redisHandler.getPassword());

            System.out.println("jedis.hgetAll(\"asd\") = " + jedis.hgetAll("asd"));

        } catch (Exception ex){
            new ClanStatsException(
                    uuid,
                    balance,
                    points,
                    "There is an error while saving a clan stats"
            ).printStackTrace();
        }
    }

    public void saveClanStats(ClanStats clanStats){
        try(Jedis jedis = redisHandler.getJedisPool().getResource()){
            jedis.auth(redisHandler.getPassword());

            String key = "cache::clanstats::" + clanStats.getClanUUID();
            jedis.hset(key, "balance", String.valueOf(clanStats.getBalance()));
            jedis.hset(key, "points", String.valueOf(clanStats.getPoints()));

            jedis.expire(key, 1800L);
        } catch (Exception ex){
            new ClanStatsException(
                    clanStats.getClanUUID(),
                    clanStats.getBalance(),
                    clanStats.getPoints(),
                    "There is an error while saving a clan stats"
            ).printStackTrace();
        }
    }

    public void sendMessage(ClanStats clanStats){

    }

}
