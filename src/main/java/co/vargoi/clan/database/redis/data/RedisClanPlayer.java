package co.vargoi.clan.database.redis.data;

import co.vargoi.clan.clan.objects.ClanPlayer;
import co.vargoi.clan.database.mysql.SQLHelper;
import co.vargoi.clan.database.redis.ClanCache;
import co.vargoi.clan.database.redis.RedisSave;
import co.vargoi.clan.enums.ClanRank;
import me.aglerr.lazylibs.libs.Common;
import org.bukkit.ChatColor;
import redis.clients.jedis.Jedis;

public class RedisClanPlayer {

    public static boolean loadPlayerSync(ClanCache clanCache, String name){
        try(Jedis jedis = clanCache.getRedisHandler().getJedisPool().getResource()){
            jedis.auth(clanCache.getRedisHandler().getPassword());
            String key = ClanCache.CACHE_CLAN_PLAYER + name;
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
                                RedisSave.saveAndPublishToRedis(clanCache.getRedisHandler(), clanCache.getGson(), clanPlayer);
                            }
                    );
                } else {
                    // Create a new data
                    ClanPlayer clanPlayer = new ClanPlayer(name, null, null);
                    // Save the player data on redis cache
                    RedisSave.saveAndPublishToRedis(clanCache.getRedisHandler(), clanCache.getGson(), clanPlayer);
                }
            } else {
                // The data is exist on redis cache, so load it from there
                ClanPlayer clanPlayer = clanCache.getGson().fromJson(jedis.hget(key, "details"), ClanPlayer.class);
                clanCache.addClanPlayer(clanPlayer);
            }
            return true;
        } catch (Exception ex){
            Common.log(ChatColor.RED, "Failed to load '" + name + "' data:");
            ex.printStackTrace();
            return false;
        }
    }

    public static void saveClanPlayerSync(ClanCache clanCache, String name){
        ClanPlayer clanPlayer = clanCache.removeClanPlayer(name);
        if(clanPlayer == null){
            return;
        }
        RedisSave.saveAndPublishToRedis(clanCache.getRedisHandler(), clanCache.getGson(), clanPlayer);
        SQLHelper.save(clanPlayer);
    }

}
