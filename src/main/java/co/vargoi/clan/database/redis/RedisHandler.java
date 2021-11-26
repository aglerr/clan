package co.vargoi.clan.database.redis;

import me.aglerr.lazylibs.libs.Common;
import me.aglerr.lazylibs.libs.Executor;
import org.bukkit.ChatColor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

public class RedisHandler {

    public static final String CHANNEL_CLAN_STATS = "channel::clanstats";
    public static final String CHANNEL_CLAN_PLAYER = "channel::clanplayer";
    public static final String CHANNEL_CLAN_BEDWARS = "channel::clanbedwars";
    public static final String CHANNEL_CLAN = "channel::clan";

    private JedisPool jedisPool;

    private final String host;
    private final int port;
    private final String password;

    public RedisHandler(String host, int port, String password) {
        this.host = host;
        this.port = port;
        this.password = password;
    }

    public void connect() {
        this.jedisPool = new JedisPool(host, port);
        try (Jedis jedis = this.jedisPool.getResource()) {
            Common.log(ChatColor.RESET, "Jedis connected!");
            jedis.auth(password);
            jedis.isConnected();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void subscribeAsync() {
        Executor.async(this::subscribeSync);
    }

    public void subscribeSync() {
        try (Jedis jedis = this.jedisPool.getResource()) {
            jedis.auth(password);

            JedisPubSub jedisPubSub = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    System.out.println("Channel " + channel + " has sent a message: " + message);

                    switch (channel){
                        case CHANNEL_CLAN:{
                            // Get the redis cache and apply the data to local cache
                            break;
                        }
                        case CHANNEL_CLAN_BEDWARS:{
                            break;
                        }
                        case CHANNEL_CLAN_PLAYER:{
                            break;
                        }
                        case CHANNEL_CLAN_STATS:{
                            // Get the required data from clan stats
                            String key = ClanCache.CACHE_CLAN_STATS + message;
                            int balance = Integer.parseInt(jedis.hget(key, "balance"));
                            int points = Integer.parseInt(jedis.hget(key, "points"));

                            // Get the clan by uuid
                            // Get the ClanStats object
                            // Set the balance and points
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
                    CHANNEL_CLAN,
                    CHANNEL_CLAN_BEDWARS,
                    CHANNEL_CLAN_PLAYER,
                    CHANNEL_CLAN_STATS
            );

        } catch (Exception ex) {
            Common.log(ChatColor.RED, "Failed to subscribe:");
            ex.printStackTrace();
        }
    }

    public String getPassword() {
        return password;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }
}
