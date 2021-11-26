package co.vargoi.clan.database.redis;

import co.vargoi.clan.clan.objects.Clan;
import co.vargoi.clan.clan.objects.ClanBedwars;
import co.vargoi.clan.clan.objects.ClanPlayer;
import co.vargoi.clan.clan.objects.ClanStats;
import me.aglerr.lazylibs.libs.Common;
import org.bukkit.ChatColor;
import redis.clients.jedis.Jedis;

public class RedisSave {

    public static void saveToRedis(RedisHandler redisHandler, Clan clan){
        try(Jedis jedis = redisHandler.getJedisPool().getResource()){
            jedis.auth(redisHandler.getPassword());
            String key = ClanCache.CACHE_CLAN + clan.getClanUUID();

            jedis.hset(key, "name", clan.getName());
            jedis.hset(key, "description", clan.getDescription());
            jedis.hset(key, "tag", clan.getTag());
            jedis.hset(key, "colorTag", clan.getColorTag());
            jedis.hset(key, "owner", clan.getOwner());
            jedis.hset(key, "maxMembers", String.valueOf(clan.getMaxMembers()));
            jedis.hset(key, "createdAt", clan.getCreatedAtInString());
        } catch (Exception ex){
            Common.log(ChatColor.RED, "Failed to save Clan object to Redis:");
            ex.printStackTrace();
        }
    }

    public static void saveToRedis(RedisHandler redisHandler, ClanBedwars clanBedwars){
        try(Jedis jedis = redisHandler.getJedisPool().getResource()){
            jedis.auth(redisHandler.getPassword());
            String key = ClanCache.CACHE_CLAN_BEDWARS + clanBedwars.getClanUUID();

            jedis.hset(key, "wins", String.valueOf(clanBedwars.getWins()));
            jedis.hset(key, "losses", String.valueOf(clanBedwars.getLosses()));
            jedis.hset(key, "kills", String.valueOf(clanBedwars.getKills()));
            jedis.hset(key, "deaths", String.valueOf(clanBedwars.getDeaths()));
            jedis.hset(key, "bed", String.valueOf(clanBedwars.getBedsDestroyed()));
            jedis.hset(key, "finalKills", String.valueOf(clanBedwars.getFinalKills()));
        } catch (Exception ex){
            Common.log(ChatColor.RED, "Failed to save ClanBedwars object to Redis:");
            ex.printStackTrace();
        }
    }

    public static void saveToRedis(RedisHandler redisHandler, ClanPlayer clanPlayer){
        try(Jedis jedis = redisHandler.getJedisPool().getResource()){
            jedis.auth(redisHandler.getPassword());
            String key = ClanCache.CACHE_CLAN_PLAYER + clanPlayer.getName();

            jedis.hset(key, "uuid", clanPlayer.getClanUUID() == null ? "null" : clanPlayer.getClanUUID());
            jedis.hset(key, "rank", clanPlayer.getRank() == null ? "null" : clanPlayer.getRank().name());
        } catch (Exception ex){
            Common.log(ChatColor.RED, "Failed to save ClanPlayer object to Redis:");
            ex.printStackTrace();
        }
    }

    public static void saveToRedis(RedisHandler redisHandler, ClanStats clanStats){
        try(Jedis jedis = redisHandler.getJedisPool().getResource()){
            jedis.auth(redisHandler.getPassword());
            String key = ClanCache.CACHE_CLAN_STATS + clanStats.getClanUUID();

            jedis.hset(key, "balance", String.valueOf(clanStats.getBalance()));
            jedis.hset(key, "points", String.valueOf(clanStats.getPoints()));
        } catch (Exception ex){
            Common.log(ChatColor.RED, "Failed to save ClanStats object to Redis:");
            ex.printStackTrace();
        }
    }

}
