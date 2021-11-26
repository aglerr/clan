package co.vargoi.clan.listeners;

import co.vargoi.clan.VargoiClan;
import co.vargoi.clan.database.redis.ClanCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class PlayerJoin implements Listener {

    private final ClanCache clanCache;
    public PlayerJoin(ClanCache clanCache){
        this.clanCache = clanCache;
    }

    @EventHandler
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event){

    }

}
