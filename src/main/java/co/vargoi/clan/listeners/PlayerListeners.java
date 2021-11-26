package co.vargoi.clan.listeners;

import co.vargoi.clan.VargoiClan;
import co.vargoi.clan.clan.objects.ClanPlayer;
import co.vargoi.clan.database.redis.ClanCache;
import co.vargoi.clan.database.redis.data.RedisClanPlayer;
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
        if(!RedisClanPlayer.loadPlayerSync(
                clanCache,
                event.getName()
        )){
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
