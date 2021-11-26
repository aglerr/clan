package co.vargoi.clan.database.redis;

import co.vargoi.clan.VargoiClan;
import co.vargoi.clan.clan.objects.Clan;
import co.vargoi.clan.clan.objects.ClanBedwars;
import co.vargoi.clan.clan.objects.ClanPlayer;
import co.vargoi.clan.clan.objects.ClanStats;
import co.vargoi.clan.enums.ClanRank;

import java.time.LocalDateTime;
import java.util.Map;

public class RedisSerializer {

    public static Clan serializeClan(String uuid, Map<String, String> map){
        String name = map.get("name");
        String description = map.get("description");
        String tag = map.get("tag");
        String colorTag = map.get("colorTag");
        String owner = map.get("owner");
        int maxMembers = Integer.parseInt(map.get("maxMembers"));
        LocalDateTime createdAt = LocalDateTime.parse(map.get("createdAt"), VargoiClan.TIME_FORMAT);
        return new Clan(uuid, name, description, tag, colorTag, owner, maxMembers, createdAt);
    }

    public static ClanBedwars serializeClanBedwars(String uuid, Map<String, String> map){
        int wins = Integer.parseInt(map.get("wins"));
        int losses = Integer.parseInt(map.get("losses"));
        int kills = Integer.parseInt(map.get("kills"));
        int deaths = Integer.parseInt(map.get("deaths"));
        int bed = Integer.parseInt(map.get("bed"));
        int finalKills = Integer.parseInt(map.get("finalKills"));
        return new ClanBedwars(uuid, wins, losses, kills, deaths, bed, finalKills);
    }

    public static ClanPlayer serializeClanPlayer(String name, Map<String, String> map){
        String clanUUID = map.get("uuid").equals("null") ? null : map.get("uuid");
        ClanRank clanRank = ClanRank.getRank(map.get("rank"));
        return new ClanPlayer(name, clanUUID, clanRank);
    }

    public static ClanStats serializeClanStats(String uuid, Map<String, String> map){
        int balance = Integer.parseInt(map.get("balance"));
        int points = Integer.parseInt(map.get("points"));
        return new ClanStats(uuid, balance, points);
    }

}
