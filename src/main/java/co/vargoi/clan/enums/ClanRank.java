package co.vargoi.clan.enums;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

public enum ClanRank {
    /**
     * Owner can change clans name, tag, disband the clan,
     * purchase upgrades, and promote/demote members.
     */
    @SerializedName("owner")
    OWNER,
    /**
     * Moderator can invite or kick members.
     */
    @SerializedName("moderator")
    MODERATOR,
    /**
     * Member doesn't have any advantages other than
     * gaining stats for the clan.
     */
    @SerializedName("member")
    MEMBER;

    @Nullable
    public static ClanRank getRank(String rank){
        if(rank == null){
            return null;
        }
        switch(rank.toLowerCase()){
            case "owner":
                return OWNER;
            case "moderator":
                return MODERATOR;
            case "member":
                return MEMBER;
            default:
                return null;
        }
    }

}
