package gg.voided.cosmo.tablist.layout;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.util.adventure.AdventureSerializer;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerListHeaderAndFooter;
import gg.voided.cosmo.tablist.TabHandler;
import gg.voided.cosmo.tablist.adapter.TabEntry;
import gg.voided.cosmo.tablist.skin.Skin;
import gg.voided.cosmo.tablist.adapter.TabAdapter;
import gg.voided.cosmo.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import java.util.*;

@RequiredArgsConstructor
public class Layout {
    private final TabHandler handler = TabHandler.getInstance();
    private final ArrayList<EntryInfo> entries = new ArrayList<>();
    private final Player player;

    private String header = "";
    private String footer = "";

    public void create() {
        Team global = player.getScoreboard().getTeam("tab");

        if (global == null) {
            global = player.getScoreboard().registerNewTeam("tab");
            global.setNameTagVisibility(NameTagVisibility.NEVER);
        }

        Bukkit.getOnlinePlayers().stream().filter(Objects::nonNull).map(Player::getName).forEach(global::addEntry);

        for (Player other : Bukkit.getOnlinePlayers()) {
            Team team = other.getScoreboard().getTeam("tab");
            if (team == null) continue;
            team.addEntry(player.getName());
        }

        update(handler.getAdapter().getEntries(player));
    }

    public void update(List<TabEntry> entries) {
        setHeaderAndFooter();
        adjustSize(entries.size());

        for (int i = 0; i < entries.size(); i++) {
            update(i, entries.get(i));
        }
    }

    private void update(int index, TabEntry entry) {
        String content = entry.getContent();

        String[] split = StringUtils.tabSplit(content);
        String prefix = StringUtils.color(split[0]);
        String suffix = StringUtils.color(split[1]);
        content = StringUtils.color(content);

        EntryInfo info = entries.get(index);
        if (info == null) return;

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
        if (!updated && changed) updateContent(info, content.isEmpty() ? getTeam(index) : content);
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

    public void setHeaderAndFooter() {
        TabAdapter adapter = handler.getAdapter();

        String header = StringUtils.color(adapter.getHeader(player));
        String footer = StringUtils.color(adapter.getFooter(player));

        if (this.header.equals(header) && this.footer.equals(footer)) return;

        this.header = header;
        this.footer = footer;

        WrapperPlayServerPlayerListHeaderAndFooter packet = new WrapperPlayServerPlayerListHeaderAndFooter(
            AdventureSerializer.fromLegacyFormat(header),
            AdventureSerializer.fromLegacyFormat(footer)
        );

        sendPacket(packet);
    }

    private void sendPacket(PacketWrapper<?> packet) {
        handler.getPacketEvents().getPlayerManager().sendPacket(player, packet);
    }

    public void adjustSize(int size) {
        if (size == entries.size()) return;

        if (size < entries.size()) {
            List<EntryInfo> remove = entries.subList(size - 1, entries.size());
            entries.removeAll(remove);

            WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER);
            List<WrapperPlayServerPlayerInfo.PlayerData> data = new ArrayList<>();

            remove.forEach(entry -> {
                data.add(
                    new WrapperPlayServerPlayerInfo.PlayerData(
                        null,
                        entry.getProfile(),
                        GameMode.SURVIVAL,
                        entry.getPing()
                    )
                );
            });

            packet.setPlayerDataList(data);
            sendPacket(packet);

            for (int i = 0; i < remove.size(); i++) {
                Team team = player.getScoreboard().getTeam("$" + getTeam(entries.size() + i));
                if (team == null) continue;
                team.unregister();
            }

            return;
        }

        int add = size - entries.size();

        WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.ADD_PLAYER);
        List<WrapperPlayServerPlayerInfo.PlayerData> data = packet.getPlayerDataList();

        for (int i = 0; i < add; i++) {
            UserProfile profile = createProfile(i);
            EntryInfo info = new EntryInfo(profile);
            entries.add(info);

            String name = getTeam(entries.size() + i);
            Team team = player.getScoreboard().getTeam(name);

            if (team == null) {
                team = player.getScoreboard().registerNewTeam(name);
                team.addEntry("$" + name);
            }

            data.add(
                new WrapperPlayServerPlayerInfo.PlayerData(
                    AdventureSerializer.fromLegacyFormat(getTeam(i)),
                    profile,
                    GameMode.SURVIVAL,
                    0
                )
            );
        }

        sendPacket(packet);
    }

    private UserProfile createProfile(int index) {
        UserProfile profile = new UserProfile(UUID.randomUUID(), getTeam(index));
        TextureProperty texture = new TextureProperty("textures", Skin.DEFAULT.getValue(), Skin.DEFAULT.getSignature());
        profile.setTextureProperties(Collections.singletonList(texture));

        return profile;
    }

    public String getTeam(int index) {
        int x = index % 4;
        int y = index / 4;

        StringBuilder builder = new StringBuilder(ChatColor.BLACK.toString())
            .append(ChatColor.COLOR_CHAR).append(x);

        for (char character : String.valueOf(y).toCharArray()) {
            builder.append(ChatColor.COLOR_CHAR).append(character);
        }

        builder.append(ChatColor.RESET);

        return builder.toString();
    }
}
