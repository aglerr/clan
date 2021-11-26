package co.vargoi.clan.clan;

import co.vargoi.clan.clan.objects.Clan;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClanHandler {

    private final Map<String, Clan> clanMap = new HashMap<>();

    public Clan getClanByUUID(String uuid){
        return clanMap.get(uuid);
    }

    public void createClan(Player owner, String clanName){

    }

    public String generateClansUUID(){
        String uuid = null;
        while(uuid == null){
            String generatedUUID = UUID.randomUUID().toString();
            if(clanMap.get(generatedUUID) != null){
                continue;
            }
            uuid = generatedUUID;
        }
        return uuid;
    }

}
