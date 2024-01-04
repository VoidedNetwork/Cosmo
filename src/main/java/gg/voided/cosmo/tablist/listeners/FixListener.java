package gg.voided.cosmo.tablist.listeners;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.util.adventure.AdventureSerializer;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import gg.voided.cosmo.tablist.TabHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class FixListener extends PacketListenerAbstract {
    private final TabHandler handler = TabHandler.getInstance();

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Server.PLAYER_INFO)) return;

        WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo(event);
        if (packet.getAction() != WrapperPlayServerPlayerInfo.Action.ADD_PLAYER) return;

        Player player = (Player) event.getPlayer();

        for (WrapperPlayServerPlayerInfo.PlayerData data : packet.getPlayerDataList()) {
            UserProfile profile = data.getUserProfile();
            if (profile == null) return;

            boolean cancel = prevent(player, profile);

            if (cancel) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(handler.getPlugin(), () -> {
                    WrapperPlayServerPlayerInfo remove = new WrapperPlayServerPlayerInfo(
                        WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER,
                        new WrapperPlayServerPlayerInfo.PlayerData(
                            AdventureSerializer.fromLegacyFormat(profile.getName()),
                            profile,
                            GameMode.SURVIVAL,
                            0
                        )
                    );

                    handler.getPacketEvents().getPlayerManager().sendPacket(player, remove);
                }, 1);
            }
        }
    }

    private boolean prevent(Player player, UserProfile profile) {
        Player online = Bukkit.getPlayer(profile.getUUID());
        if (online == null) return false;

        Scoreboard scoreboard = player.getScoreboard();
        Team team = scoreboard.getTeam("tab");

        if (team == null) {
            team = scoreboard.registerNewTeam("tab");

            for (Player other : Bukkit.getOnlinePlayers()) {
                team.addEntry(other.getName());
            }

            team.addEntry(online.getName());
            return true;
        }

        team.addEntry(online.getName());
        return false;
    }
}
