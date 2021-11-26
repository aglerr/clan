package co.vargoi.clan.database.redis.data;

import co.vargoi.clan.clan.objects.ClanBedwars;
import co.vargoi.clan.clan.objects.ClanPlayer;
import co.vargoi.clan.clan.objects.ClanStats;
import co.vargoi.clan.database.mysql.SQLHelper;
import co.vargoi.clan.database.redis.ClanCache;
import co.vargoi.clan.database.redis.RedisSave;
import me.aglerr.lazylibs.libs.Common;
import org.bukkit.ChatColor;
import redis.clients.jedis.Jedis;

public class RedisClanBedwars {

    public static boolean loadClanBedwarsSync(ClanCache clanCache, String uuid){
        try(Jedis jedis = clanCache.getRedisHandler().getJedisPool().getResource()){
            jedis.auth(clanCache.getRedisHandler().getPassword());
            String key = ClanCache.CACHE_CLAN_BEDWARS + uuid;
            if(jedis.hgetAll(key).isEmpty()){
                // There is no ClanStats data for this clan on redis cache
                String condition = "SELECT uuid FROM `" + SQLHelper.BEDWARS_TABLE + "` WHERE " +
                        "uuid=\"" + uuid + "\";";
                if(SQLHelper.doesConditionExist(condition)){
                    // Clan data exist on database, so load it from mysql
                    SQLHelper.executeQuery(
                            "SELECT * FROM `" + SQLHelper.BEDWARS_TABLE + "` WHERE uuid=\"" + uuid + "\";",
                            resultSet -> {
                                int wins = resultSet.getInt("wins");
                                int losses = resultSet.getInt("losses");
                                int kills = resultSet.getInt("kills");
                                int deaths = resultSet.getInt("deaths");
                                int bed = resultSet.getInt("bed");
                                int finalKills = resultSet.getInt("finalKills");

                                ClanBedwars clanBedwars = new ClanBedwars(uuid, wins, losses, kills, deaths, bed, finalKills);
                                RedisSave.saveAndPublishToRedis(clanCache.getRedisHandler(), clanCache.getGson(), clanBedwars);
                            }
                    );
                } else {
                    // Create a new data
                    ClanBedwars clanBedwars = new ClanBedwars(uuid, 0, 0, 0, 0, 0, 0);
                    // Save data and publish it to redis
                    RedisSave.saveAndPublishToRedis(clanCache.getRedisHandler(), clanCache.getGson(), clanBedwars);
                }
            } else {
                // Data is exist on redis cache, so load it from there
                ClanBedwars clanBedwars = clanCache.getGson().fromJson(jedis.hget(key, "details"), ClanBedwars.class);
                // Cache it on the local
                clanCache.addClanBedwars(clanBedwars);
            }
            return true;
        } catch (Exception ex){
            Common.log(ChatColor.RED, "Failed to load ClanStats (" + uuid + "):");
            ex.printStackTrace();
            return false;
        }
    }

    public static void saveClanBedwarsSync(ClanCache clanCache, String uuid){
        ClanBedwars clanBedwars = clanCache.removeClanBedwars(uuid);
        if(clanBedwars == null){
            return;
        }
        RedisSave.saveAndPublishToRedis(clanCache.getRedisHandler(), clanCache.getGson(), clanBedwars);
        SQLHelper.save(clanBedwars);
    }

}
