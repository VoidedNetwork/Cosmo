package gg.voided.cosmo.tablist.listeners;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class FixListener extends PacketListenerAbstract {

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Server.PLAYER_INFO)) return;

        WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo(event);
        if (packet.getAction() != WrapperPlayServerPlayerInfo.Action.ADD_PLAYER) return;

        Player player = (Player) event.getPlayer();

        for (WrapperPlayServerPlayerInfo.PlayerData data : packet.getPlayerDataList()) {
            UserProfile profile = data.getUserProfile();
            if (profile == null) return;

            prevent(player, profile);
        }
    }

    private void prevent(Player player, UserProfile profile) {
        Player online = Bukkit.getPlayer(profile.getUUID());
        if (online == null) return;

        Scoreboard scoreboard = player.getScoreboard();
        Team team = scoreboard.getTeam("tab");

        if (team == null) {
            team = scoreboard.registerNewTeam("tab");
            team.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OWN_TEAM);

            for (Player other : Bukkit.getOnlinePlayers()) {
                team.addEntry(other.getName());
            }
        }

        team.addEntry(online.getName());
    }
}
