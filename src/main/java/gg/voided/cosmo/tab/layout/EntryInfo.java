package gg.voided.cosmo.tab.layout;

import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import gg.voided.cosmo.tab.adapter.Bars;
import gg.voided.cosmo.tab.skin.Skin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter @RequiredArgsConstructor
public class EntryInfo {
    private final UserProfile profile;
    private int ping = Bars.NONE.getPing();
    private Skin skin = Skin.DEFAULT;
    private String prefix = "";
    private String suffix = "";
    private WrapperPlayServerTeams.ScoreBoardTeamInfo team = null;
}
