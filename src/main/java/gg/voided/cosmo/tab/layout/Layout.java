package gg.voided.cosmo.tab.layout;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.util.adventure.AdventureSerializer;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerListHeaderAndFooter;
import gg.voided.cosmo.tab.TabHandler;
import gg.voided.cosmo.tab.adapter.Bars;
import gg.voided.cosmo.tab.adapter.TabEntry;
import gg.voided.cosmo.tab.skin.Skin;
import gg.voided.cosmo.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.*;

@RequiredArgsConstructor
public class Layout {
    private final TabHandler handler = TabHandler.getInstance();
    private final ArrayList<EntryInfo> entries = new ArrayList<>();
    private final Player player;

    private String header = "";
    private String footer = "";

    public void initialize() {
        WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.ADD_PLAYER);
        List<WrapperPlayServerPlayerInfo.PlayerData> players = packet.getPlayerDataList();

        for (int index = 0; index < 80; index++) {
            UserProfile profile = createProfile(index);
            entries.add(new EntryInfo(profile));

            String name = getTeam(index);
            Team team = player.getScoreboard().getTeam(name);
            if (team == null) team = player.getScoreboard().registerNewTeam(name);
            team.addEntry(profile.getName());

            players.add(
                new WrapperPlayServerPlayerInfo.PlayerData(
                    AdventureSerializer.fromLegacyFormat(name),
                    profile,
                    GameMode.SURVIVAL,
                    Bars.ONE.getPing()
                )
            );
        }

        sendPacket(packet);
    }

    public void update(String header, String footer, List<TabEntry> entries) {
        setHeaderAndFooter(header, footer);

        HashMap<Integer, TabEntry> map = new HashMap<>();
        entries.forEach(entry -> map.put(entry.getIndex(), entry));

        for (int index = 0; index < this.entries.size(); index++) {
            TabEntry entry = map.get(index);
            if (entry == null) entry = new TabEntry(index % 4, index / 4);

            String content = StringUtils.color(entry.getContent());

            String[] split = StringUtils.tabSplit(content);
            String prefix = StringUtils.color(split[0]);
            String suffix = StringUtils.color(split[1]);

            EntryInfo info = this.entries.get(index);

            boolean changed = false;

            if (!prefix.equals(info.getPrefix())) {
                info.setPrefix(prefix);
                changed = true;
            }

            if (!suffix.equals(info.getSuffix())) {
                info.setSuffix(suffix);
                changed = true;
            }

            boolean updated = updateSkin(info, entry.getSkin(), content);
            updatePing(info, entry.getPing());
            if (!updated && changed) updateContent(info, content);
        }
    }

    public void setHeaderAndFooter(String header, String footer) {
        header = StringUtils.color(header);
        footer = StringUtils.color(footer);

        if (this.header.equals(header) && this.footer.equals(footer)) return;

        this.header = header;
        this.footer = footer;

        sendPacket(
            new WrapperPlayServerPlayerListHeaderAndFooter(
                AdventureSerializer.fromLegacyFormat(header),
                AdventureSerializer.fromLegacyFormat(footer)
            )
        );
    }

    private void updatePing(EntryInfo entry, int ping) {
        int last = entry.getPing();
        if (last == ping) return;
        entry.setPing(ping);

        WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo(
            WrapperPlayServerPlayerInfo.Action.UPDATE_LATENCY,
            new WrapperPlayServerPlayerInfo.PlayerData(null, entry.getProfile(), GameMode.SURVIVAL, ping)
        );

        sendPacket(packet);
    }

    private boolean updateSkin(EntryInfo entry, Skin skin, String content) {
        if (skin == null) skin = Skin.DEFAULT;

        Skin last = entry.getSkin();
        if (last == skin) return false;
        entry.setSkin(skin);

        UserProfile profile = entry.getProfile();
        TextureProperty texture = new TextureProperty("textures", skin.getValue(), skin.getSignature());
        profile.setTextureProperties(Collections.singletonList(texture));

        WrapperPlayServerPlayerInfo remove = new WrapperPlayServerPlayerInfo(
            WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER,
            new WrapperPlayServerPlayerInfo.PlayerData(null, profile, GameMode.SURVIVAL, entry.getPing())
        );

        boolean modern = handler.getPacketEvents().getPlayerManager().getClientVersion(player).isNewerThanOrEquals(ClientVersion.V_1_16);
        if (modern) sendPacket(remove);

        WrapperPlayServerPlayerInfo add = new WrapperPlayServerPlayerInfo(
            WrapperPlayServerPlayerInfo.Action.ADD_PLAYER,
            new WrapperPlayServerPlayerInfo.PlayerData(
                AdventureSerializer.fromLegacyFormat(content),
                profile,
                GameMode.SURVIVAL,
                entry.getPing()
            )
        );

        sendPacket(add);
        return true;
    }

    private void updateContent(EntryInfo entry, String content) {
        WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo(
            WrapperPlayServerPlayerInfo.Action.UPDATE_DISPLAY_NAME,
            new WrapperPlayServerPlayerInfo.PlayerData(
                AdventureSerializer.fromLegacyFormat(content),
                entry.getProfile(),
                null,
                0
            )
        );

        sendPacket(packet);
    }

    private void sendPacket(PacketWrapper<?> packet) {
        handler.getPacketEvents().getPlayerManager().sendPacket(player, packet);
    }

    private UserProfile createProfile(int index) {
        UserProfile profile = new UserProfile(UUID.randomUUID(), getTeam(index));
        TextureProperty texture = new TextureProperty("textures", Skin.DEFAULT.getValue(), Skin.DEFAULT.getSignature());
        profile.setTextureProperties(Collections.singletonList(texture));

        return profile;
    }

    private String getTeam(int index) {
        StringBuilder builder = new StringBuilder()
            .append(ChatColor.COLOR_CHAR).append(index % 4);

        for (char character : String.valueOf(index / 4).toCharArray()) {
            builder.append(ChatColor.COLOR_CHAR).append(character);
        }

        return builder.append(ChatColor.RESET).toString();
    }
}
