package co.vargoi.clan.listeners;

import co.vargoi.clan.VargoiClan;
import co.vargoi.clan.clan.objects.ClanPlayer;
import co.vargoi.clan.database.redis.ClanCache;
import co.vargoi.clan.database.redis.data.RedisClan;
import co.vargoi.clan.database.redis.data.RedisClanBedwars;
import co.vargoi.clan.database.redis.data.RedisClanPlayer;
import co.vargoi.clan.database.redis.data.RedisClanStats;
import me.aglerr.lazylibs.libs.Executor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListeners implements Listener {

    private final ClanCache clanCache;
    public PlayerListeners(ClanCache clanCache){
        this.clanCache = clanCache;
    }

    @EventHandler
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event){
        if(RedisClanPlayer.loadPlayerSync(
                clanCache,
                event.getName()
        )){
            //event.setLoginResult(AsyncPlayerPreLoginEvent.Result.ALLOWED);
            // If loading the user's data successful, we want to load Clan data of the player too.
            // First of all, check If the user has a clan or not
            ClanPlayer clanPlayer = clanCache.getClanPlayer(event.getName());
            if(clanPlayer == null || clanPlayer.getClanUUID() == null ||
                    clanPlayer.getClanUUID().equalsIgnoreCase("null")){
                return;
            }
            // Proceed to load the clan
            if(RedisClan.loadClanSync(clanCache, clanPlayer.getClanUUID())){
                // If loading the clan successful, load the bedwars and stats data too
                RedisClanBedwars.loadClanBedwarsSync(clanCache, clanPlayer.getClanUUID());
                RedisClanStats.loadClanStatsSync(clanCache, clanPlayer.getClanUUID());
            }
        } else {
            // If loading the user's data failed, kick the player
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage("Failed to load your data, please rejoin!");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Executor.async(() -> RedisClanPlayer.saveClanPlayerSync(
                clanCache,
                event.getPlayer().getName()
        ));
    }

}
