package co.vargoi.clan.clan.objects;

import co.vargoi.clan.database.mysql.SQLHelper;
import me.aglerr.lazylibs.libs.Executor;

import java.time.LocalDateTime;
import java.util.List;

public class Clan {

    private final String uuid;
    private String name;
    private String description;
    private String tag;
    private String colorTag;
    private String owner;
    private int maxMembers;
    private final LocalDateTime createdAt;
    private final List<ClanPlayer> members;
    private final ClanStats clanStats;
    private final ClanBedwars clanBedwars;

    public Clan(String uuid, String name, String description, String tag, String colorTag, String owner,
                int maxMembers, LocalDateTime createdAt, List<ClanPlayer> members, ClanStats clanStats,
                ClanBedwars clanBedwars) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.tag = tag;
        this.colorTag = colorTag;
        this.owner = owner;
        this.maxMembers = maxMembers;
        this.createdAt = createdAt;
        this.members = members;
        this.clanStats = clanStats;
        this.clanBedwars = clanBedwars;
    }

    public String getClanUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getColorTag() {
        return colorTag;
    }

    public void setColorTag(String colorTag) {
        this.colorTag = colorTag;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<ClanPlayer> getMembers() {
        return members;
    }

    public ClanStats getClanStats() {
        return clanStats;
    }

    public ClanBedwars getClanBedwars() {
        return clanBedwars;
    }

    public void saveAsync(){
        Executor.async(() -> SQLHelper.save(this));
    }

}
