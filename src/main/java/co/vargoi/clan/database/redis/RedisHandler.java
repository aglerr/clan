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

    public void close(){
        jedisPool.close();
    }

    public String getPassword() {
        return password;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }
}
