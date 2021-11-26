package co.vargoi.clan;

import co.vargoi.clan.database.mysql.SQLDatabaseInitializer;
import co.vargoi.clan.database.mysql.SQLHelper;
import co.vargoi.clan.database.redis.ClanCache;
import co.vargoi.clan.database.redis.RedisHandler;
import me.aglerr.lazylibs.LazyLibs;
import me.aglerr.lazylibs.libs.Common;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.format.DateTimeFormatter;

public class VargoiClan extends JavaPlugin {

    /**
     *
     * Database Tables Details:
     * vargoiclan_clan - (uuid, name, description, tag, colorTag, owner, maxMembers, createdAt)
     * vargoiclan_stats (uuid, balance, points)
     * vargoiclan_bedwars (uuid, wins, losses, kills, deaths, bed, finalKills)
     * vargoiclan_player (uuid, name, rank)
     *
     */

    public static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private final RedisHandler redisHandler = new RedisHandler("redis-14727.c295.ap-southeast-1-1.ec2.cloud.redislabs.com", 14727, "ddb5zidfmljovHMTagJKAmLuupyS0U3f");

    @Override
    public void onEnable(){
        LazyLibs.inject(this);
        Common.setPrefix("[VargoiClan]");

        redisHandler.connect();

        SQLDatabaseInitializer.getInstance().init(this);
    }

    @Override
    public void onDisable(){
        SQLHelper.close();
    }

}
