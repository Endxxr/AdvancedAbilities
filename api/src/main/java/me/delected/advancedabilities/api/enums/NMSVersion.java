package me.delected.advancedabilities.api.enums;

import org.bukkit.Bukkit;

public enum NMSVersion {

    UNKNOWN(0),
    v1_8_r3(88),
    v1_9_r1(92),
    v1_9_r2(94),
    v1_10_r1(102),
    v1_11_r1(112),
    v1_12_r1(120),
    v1_13_r1(130),
    v1_13_r2(132),
    v1_14_r1(144),
    v1_15_r1(152),
    v1_16_r1(161),
    v1_16_r2(163),
    v1_16_r3(165),
    v1_17_r1(171),
    v1_18_r1(181),
    v1_18_r2(182),
    v1_19_r1(191),
    v1_19_r2(192);

    private final int shortVersion;

    NMSVersion(int i) {
        this.shortVersion = i;
    }

    public int getInt() {
        return shortVersion;
    }

    public static NMSVersion fromString(String string) {
        for (NMSVersion version : values()) {
            if (version.name().equalsIgnoreCase(string)) return version;
        }
        return NMSVersion.UNKNOWN;
    }



    public static NMSVersion getSeverVersion() {

        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);

        return fromString(version);
    }


    public static boolean isLegacy() {

        return getSeverVersion().shortVersion < 130;

    }


}
