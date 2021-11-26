package co.vargoi.clan.database.redis;

import co.vargoi.clan.clan.objects.Clan;
import co.vargoi.clan.clan.objects.ClanBedwars;
import co.vargoi.clan.clan.objects.ClanPlayer;
import co.vargoi.clan.clan.objects.ClanStats;
import com.google.gson.Gson;
import me.aglerr.lazylibs.libs.Common;
import org.bukkit.ChatColor;
import redis.clients.jedis.Jedis;

public class RedisSave {

    public static void saveAndPublishToRedis(RedisHandler redisHandler, Gson gson, Clan clan){
        try(Jedis jedis = redisHandler.getJedisPool().getResource()){
            jedis.auth(redisHandler.getPassword());
            String key = ClanCache.CACHE_CLAN + clan.getClanUUID();

            String jsonData = gson.toJson(clan);
            jedis.hset(key, "details", jsonData);
            jedis.publish(RedisHandler.CHANNEL_CLAN, jsonData);
        } catch (Exception ex){
            Common.log(ChatColor.RED, "Failed to save Clan object to Redis:");
            ex.printStackTrace();
        }
    }

    public static void saveAndPublishToRedis(RedisHandler redisHandler, Gson gson, ClanBedwars clanBedwars){
        try(Jedis jedis = redisHandler.getJedisPool().getResource()){
            jedis.auth(redisHandler.getPassword());
            String key = ClanCache.CACHE_CLAN_BEDWARS + clanBedwars.getClanUUID();

            String jsonData = gson.toJson(clanBedwars);
            jedis.hset(key, "details", jsonData);
            jedis.publish(RedisHandler.CHANNEL_CLAN_BEDWARS, jsonData);
        } catch (Exception ex){
            Common.log(ChatColor.RED, "Failed to save ClanBedwars object to Redis:");
            ex.printStackTrace();
        }
    }

    public static void saveAndPublishToRedis(RedisHandler redisHandler, Gson gson, ClanPlayer clanPlayer){
        try(Jedis jedis = redisHandler.getJedisPool().getResource()){
            jedis.auth(redisHandler.getPassword());
            String key = ClanCache.CACHE_CLAN_PLAYER + clanPlayer.getName();

            String jsonData = gson.toJson(clanPlayer);
            jedis.hset(key, "details", jsonData);
            jedis.publish(RedisHandler.CHANNEL_CLAN_PLAYER, jsonData);
        } catch (Exception ex){
            Common.log(ChatColor.RED, "Failed to save ClanPlayer object to Redis:");
            ex.printStackTrace();
        }
    }

    public static void saveAndPublishToRedis(RedisHandler redisHandler, Gson gson, ClanStats clanStats){
        try(Jedis jedis = redisHandler.getJedisPool().getResource()){
            jedis.auth(redisHandler.getPassword());
            String key = ClanCache.CACHE_CLAN_STATS + clanStats.getClanUUID();

            String jsonData = gson.toJson(clanStats);
            jedis.hset(key, "details", jsonData);
            jedis.publish(RedisHandler.CHANNEL_CLAN_STATS, jsonData);
        } catch (Exception ex){
            Common.log(ChatColor.RED, "Failed to save ClanStats object to Redis:");
            ex.printStackTrace();
        }
    }

}
