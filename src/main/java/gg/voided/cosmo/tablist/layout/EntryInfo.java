package gg.voided.cosmo.tablist.layout;

import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import gg.voided.cosmo.tablist.adapter.Bars;
import gg.voided.cosmo.tablist.skin.Skin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor @Getter @Setter
public class EntryInfo {
    private final UserProfile profile;
    private int ping = Bars.NONE.getPing();
    private Skin skin = Skin.DEFAULT;
    private String prefix = "";
    private String suffix = "";
    private WrapperPlayServerTeams.ScoreBoardTeamInfo team = null;
}
