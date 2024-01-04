package gg.voided.cosmo.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

@UtilityClass
public class StringUtils {

    public String color(String content) {
        return ChatColor.translateAlternateColorCodes('&', content);
    }

    public String[] tabSplit(String content) {
        if (content.length() <= 16) return new String[] { content, "" };

        String prefix = content.substring(0, 16);
        int section = prefix.lastIndexOf(ChatColor.COLOR_CHAR);
        int ampersand = prefix.lastIndexOf("&");

        int highest = Math.max(section, ampersand);
        if (highest < 14) return new String[] { prefix, getLastColors(prefix) + content.substring(0, 16) };

        prefix = prefix.substring(0, highest);

        return new String[] { prefix, content.substring(0, highest) };
    }

    public net.md_5.bungee.api.ChatColor getLastColors(String content) {
        String prefix = ChatColor.getLastColors(color(content));
        if (prefix.isEmpty()) return null;

        ChatColor color = ChatColor.getByChar(prefix.substring(prefix.length() - 1).charAt(0));
        if (color == null) return null;

        return color.asBungee();
    }
}
