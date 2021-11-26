package co.vargoi.clan.database.redis;

import co.vargoi.clan.clan.objects.Clan;
import co.vargoi.clan.clan.objects.ClanBedwars;
import co.vargoi.clan.clan.objects.ClanPlayer;
import co.vargoi.clan.clan.objects.ClanStats;
import co.vargoi.clan.database.mysql.SQLHelper;
import co.vargoi.clan.enums.ClanRank;
import com.google.gson.Gson;
import me.aglerr.lazylibs.libs.Common;
import me.aglerr.lazylibs.libs.Executor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.Map;

public class ClanCache implements Listener {

    public static final String CACHE_CLAN = "cache::clan::";
    public static final String CACHE_CLAN_BEDWARS = "cache::clanbedwars::";
    public static final String CACHE_CLAN_PLAYER = "cache::clanplayer::";
    public static final String CACHE_CLAN_STATS = "cache::clanstats::";

    private final Gson gson = new Gson();

    private final Map<String, Clan> clanMap = new HashMap<>();
    private final Map<String, ClanBedwars> clanBedwarsMap = new HashMap<>();
    private final Map<String, ClanPlayer> clanPlayerMap = new HashMap<>();
    private final Map<String, ClanStats> clanStatsMap = new HashMap<>();

    private final RedisHandler redisHandler;
    public ClanCache(RedisHandler redisHandler){
        this.redisHandler = redisHandler;
    }

    @Nullable
    public ClanPlayer getClanPlayer(Player player){
        return getClanPlayer(player.getName());
    }

    @Nullable
    public ClanPlayer getClanPlayer(String name){
        return clanPlayerMap.get(name);
    }

    public ClanPlayer remove(String name){
        return clanPlayerMap.remove(name);
    }

    public boolean loadPlayerSync(String name){
        try(Jedis jedis = redisHandler.getJedisPool().getResource()){
            jedis.auth(redisHandler.getPassword());
            String key = CACHE_CLAN_PLAYER + name;
            if(jedis.hgetAll(key).isEmpty()){
                // User's data on redis is empty, load it from mysql
                String condition = "SELECT name FROM `" + SQLHelper.PLAYER_TABLE + "` WHERE " +
                        "name=\"" + name + "\";";
                Common.debug("Action: Load user's data from MySQL");
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
                                jedis.hset(key, "uuid", clanUUID == null ? "null" : clanUUID);
                                jedis.hset(key, "rank", clanRank == null ? "null" : clanRank.name());
                                // Finally, put it on the local hashmap
                                this.clanPlayerMap.put(name, clanPlayer);
                            }
                    );
                } else {
                    // Create a new data
                    ClanPlayer clanPlayer = new ClanPlayer(name, null, null);
                    // Save the player data on redis cache
                    jedis.hset(key, "uuid", "null");
                    jedis.hset(key, "rank", "null");
                    // Finally, put it on the local hashmap
                    this.clanPlayerMap.put(name, clanPlayer);
                }
            } else {
                Common.debug("Action: Load user's data from Redis Cache");
                // The data is exist on redis cache, so load it from there
                ClanPlayer clanPlayer = RedisSerializer.serializeClanPlayer(name, jedis.hgetAll(key));
                // Put it on the local hashmap
                clanPlayerMap.put(name, clanPlayer);
            }
            return true;
        } catch (Exception ex){
            Common.log(ChatColor.RED, "Failed to load '" + name + "' data:");
            ex.printStackTrace();
            return false;
        }
    }

    public void savePlayerSync(String name){
        ClanPlayer clanPlayer = clanPlayerMap.remove(name);
        if(clanPlayer == null){
            return;
        }
        RedisSave.saveToRedis(redisHandler, clanPlayer);
        SQLHelper.save(clanPlayer);
        try(Jedis jedis = redisHandler.getJedisPool().getResource()){
            jedis.auth(redisHandler.getPassword());
            jedis.publish(RedisHandler.CHANNEL_CLAN_PLAYER, gson.toJson(clanPlayer));
        }
    }

    public void subscribeAsync(){
        Executor.async(this::subscribeSync);
    }

    public void subscribeSync() {
        try (Jedis jedis = redisHandler.getJedisPool().getResource()) {
            jedis.auth(redisHandler.getPassword());

            JedisPubSub jedisPubSub = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    System.out.println("Channel " + channel + " has sent a message: " + message);

                    switch (channel){
                        case RedisHandler.CHANNEL_CLAN:{
                            Clan clan = gson.fromJson(message, Clan.class);
                            clanMap.put(clan.getClanUUID(), clan);
                            break;
                        }
                        case RedisHandler.CHANNEL_CLAN_BEDWARS:{
                            ClanBedwars clanBedwars = gson.fromJson(message, ClanBedwars.class);
                            clanBedwarsMap.put(clanBedwars.getClanUUID(), clanBedwars);
                            break;
                        }
                        case RedisHandler.CHANNEL_CLAN_PLAYER:{
                            ClanPlayer clanPlayer = gson.fromJson(message, ClanPlayer.class);
                            clanPlayerMap.put(clanPlayer.getName(), clanPlayer);
                            break;
                        }
                        case RedisHandler.CHANNEL_CLAN_STATS:{
                            ClanStats clanStats = gson.fromJson(message, ClanStats.class);
                            clanStatsMap.put(clanStats.getClanUUID(), clanStats);
                            break;
                        }
                        default:{
                            throw new IllegalArgumentException("Channel '" + channel + "' is not subscribed!");
                        }
                    }
                }
            };

            jedis.subscribe(
                    jedisPubSub,
                    RedisHandler.CHANNEL_CLAN,
                    RedisHandler.CHANNEL_CLAN_BEDWARS,
                    RedisHandler.CHANNEL_CLAN_PLAYER,
                    RedisHandler.CHANNEL_CLAN_STATS
            );

        } catch (Exception ex) {
            Common.log(ChatColor.RED, "Failed to subscribe:");
            ex.printStackTrace();
        }
    }

}
