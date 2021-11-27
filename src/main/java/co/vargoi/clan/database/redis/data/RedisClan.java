package co.vargoi.clan.database.redis.data;

import co.vargoi.clan.VargoiClan;
import co.vargoi.clan.clan.objects.Clan;
import co.vargoi.clan.database.mysql.SQLHelper;
import co.vargoi.clan.database.redis.ClanCache;
import co.vargoi.clan.database.redis.RedisSave;
import me.aglerr.lazylibs.libs.Common;
import org.bukkit.ChatColor;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

public class RedisClan {

    public static boolean loadClanSync(ClanCache clanCache, String uuid){
        try(Jedis jedis = clanCache.getRedisHandler().getJedisPool().getResource()){
            jedis.auth(clanCache.getRedisHandler().getPassword());
            String key = ClanCache.CACHE_CLAN + uuid;
            if(jedis.hgetAll(key).isEmpty()){
                // There is no ClanStats data for this clan on redis cache
                String query = "SELECT * FROM `" + SQLHelper.CLAN_TABLE + "` WHERE uuid=\"" + uuid + "\";";
                AtomicBoolean isSuccess = new AtomicBoolean(false);
                SQLHelper.executeQuery(query,
                        resultSet -> {
                            if(resultSet.next()){
                                String name = resultSet.getString("name");
                                String description = resultSet.getString("description");
                                String tag = resultSet.getString("tag");
                                String colorTag = resultSet.getString("colorTag");
                                String owner = resultSet.getString("owner");
                                int maxMembers = resultSet.getInt("maxMembers");
                                LocalDateTime createdAt = LocalDateTime.parse(resultSet.getString("createdAt"), VargoiClan.TIME_FORMAT);

                                Clan clan = new Clan(uuid, name, description, tag, colorTag, owner, maxMembers, createdAt);
                                RedisSave.saveAndPublishToRedis(clanCache.getRedisHandler(), clanCache.getGson(), clan);
                                isSuccess.set(true);
                            } else {
                                Common.log(ChatColor.RED, "There is a clan with UUID " + uuid, "But there is no clan data for that clan's uuid.");
                            }
                        }
                );
                return isSuccess.get();
            } else {
                // Data is exist on redis cache, so load it from there
                Clan clan = clanCache.getGson().fromJson(jedis.hget(key, "details"), Clan.class);
                // Cache it on the local
                clanCache.addClan(clan);
            }
            return true;
        } catch (Exception ex){
            Common.log(ChatColor.RED, "Failed to load ClanStats (" + uuid + "):");
            ex.printStackTrace();
            return false;
        }
    }

    public static void saveClanSync(ClanCache clanCache, String uuid){
        Clan clan = clanCache.removeClan(uuid);
        if(clan == null){
            return;
        }
        RedisSave.saveAndPublishToRedis(clanCache.getRedisHandler(), clanCache.getGson(), clan);
        SQLHelper.save(clan);
    }

}
