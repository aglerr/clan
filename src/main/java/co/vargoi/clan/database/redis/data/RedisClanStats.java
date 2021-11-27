package co.vargoi.clan.database.redis.data;

import co.vargoi.clan.clan.objects.ClanStats;
import co.vargoi.clan.database.mysql.SQLHelper;
import co.vargoi.clan.database.redis.ClanCache;
import co.vargoi.clan.database.redis.RedisSave;
import me.aglerr.lazylibs.libs.Common;
import org.bukkit.ChatColor;
import redis.clients.jedis.Jedis;

public class RedisClanStats {

    public static boolean loadClanStatsSync(ClanCache clanCache, String uuid){
        try(Jedis jedis = clanCache.getRedisHandler().getJedisPool().getResource()){
            jedis.auth(clanCache.getRedisHandler().getPassword());
            String key = ClanCache.CACHE_CLAN_STATS + uuid;
            if(jedis.hgetAll(key).isEmpty()){
                // There is no ClanStats data for this clan on redis cache, load it from mysql
                String query = "SELECT * FROM `" + SQLHelper.STATS_TABLE + "` WHERE uuid=\"" + uuid + "\";";
                SQLHelper.executeQuery(query,
                        resultSet -> {
                            if(resultSet.next()){
                                int balance = resultSet.getInt("balance");
                                int points = resultSet.getInt("points");

                                ClanStats clanStats = new ClanStats(uuid, balance, points);
                                RedisSave.saveAndPublishToRedis(clanCache.getRedisHandler(), clanCache.getGson(), clanStats);
                            } else {
                                // Create a new data
                                ClanStats clanStats = new ClanStats(uuid, 0,0);
                                // Save data and publish it to redis
                                RedisSave.saveAndPublishToRedis(clanCache.getRedisHandler(), clanCache.getGson(), clanStats);
                            }
                        }
                );
            } else {
                // Data is exist on redis cache, so load it from there
                ClanStats clanStats = clanCache.getGson().fromJson(jedis.hget(key, "details"), ClanStats.class);
                // Cache it on the local
                clanCache.addClanStats(clanStats);
            }
            return true;
        } catch (Exception ex){
            Common.log(ChatColor.RED, "Failed to load ClanStats (" + uuid + "):");
            ex.printStackTrace();
            return false;
        }
    }

    public static void saveClanStatsSync(ClanCache clanCache, String uuid){
        ClanStats clanStats = clanCache.removeClanStats(uuid);
        if(clanStats == null){
            return;
        }
        RedisSave.saveAndPublishToRedis(clanCache.getRedisHandler(), clanCache.getGson(), clanStats);
        SQLHelper.save(clanStats);
    }

}
