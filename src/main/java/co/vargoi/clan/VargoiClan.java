package co.vargoi.clan;

import co.vargoi.clan.clan.objects.Clan;
import co.vargoi.clan.database.mysql.SQLDatabaseInitializer;
import co.vargoi.clan.database.mysql.SQLHelper;
import co.vargoi.clan.database.redis.ClanCache;
import co.vargoi.clan.database.redis.RedisHandler;
import co.vargoi.clan.database.redis.data.RedisClan;
import co.vargoi.clan.database.redis.data.RedisClanBedwars;
import co.vargoi.clan.database.redis.data.RedisClanStats;
import co.vargoi.clan.listeners.PlayerListeners;
import me.aglerr.lazylibs.LazyLibs;
import me.aglerr.lazylibs.libs.Common;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.format.DateTimeFormatter;

public class VargoiClan extends JavaPlugin {

    /**
     * Database Tables Details:
     * vargoiclan_clan - (uuid, name, description, tag, colorTag, owner, maxMembers, createdAt)
     * vargoiclan_stats (uuid, balance, points)
     * vargoiclan_bedwars (uuid, wins, losses, kills, deaths, bed, finalKills)
     * vargoiclan_player (uuid, name, rank)
     */

    /**
     * (/clan info)
     * Clan Name: xxxx
     * Clan Description: xxx
     * Clan Tag: xxxx
     * createdAt: xxxx
     * Owner: xxxx
     * Moderators(x): xxx
     * Members(x): xxx
     */

    public static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private RedisHandler redisHandler;
    private ClanCache clanCache;

    @Override
    public void onEnable(){
        LazyLibs.inject(this);
        Common.setPrefix("[VargoiClan]");
        Common.DEBUG = true;

        SQLDatabaseInitializer.getInstance().init(this);

        redisHandler = new RedisHandler(
                "redis-14727.c295.ap-southeast-1-1.ec2.cloud.redislabs.com",
                14727,
                "ddb5zidfmljovHMTagJKAmLuupyS0U3f"
        );
        redisHandler.connect();
        clanCache = new ClanCache(redisHandler);
        clanCache.subscribeAsync();

        Bukkit.getPluginManager().registerEvents(new PlayerListeners(clanCache), this);

    }

    @Override
    public void onDisable(){
        // Save all local cached clans
        Common.log(ChatColor.RESET, "Saving Task - saving all clans data...");
        long start = System.currentTimeMillis();
        for (Clan clan : clanCache.getClans()) {
            RedisClan.saveClanSync(clanCache, clan.getClanUUID());
            RedisClanBedwars.saveClanBedwarsSync(clanCache, clan.getClanUUID());
            RedisClanStats.saveClanStatsSync(clanCache, clan.getClanUUID());

            long finished = start - System.currentTimeMillis();
            Common.log(ChatColor.RESET, "Saved clan with uuid '" + clan.getClanUUID() + "' (took " + finished + "ms)");
        }
        SQLHelper.close();
        redisHandler.close();
    }

}
