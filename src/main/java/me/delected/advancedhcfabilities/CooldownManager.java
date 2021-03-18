package me.delected.advancedhcfabilities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {


    /*
            STUN
     */
    private final Map<UUID, Long> stun = new HashMap<>();

    public void setStunCooldown(UUID player, long time) {
        if (time < 1) stun.remove(player);
        else stun.put(player, time);
    }

    public long getStunCooldown(UUID player) { return stun.getOrDefault(player, 0L); }


    /*
            EGG
     */
    private final Map<UUID, Long> egg = new HashMap<>();

    public void setEggCooldown(UUID player, long time) {
        if (time < 1) egg.remove(player);
        else egg.put(player, time);
    }

    public long getEggCooldown(UUID player) { return egg.getOrDefault(player, 0L); }


    /*
            SNOWBALL
     */
    private final Map<UUID, Long> snowball = new HashMap<>();

    public void setSnowballCooldown(UUID player, long time) {
        if (time < 1) snowball.remove(player);
        else snowball.put(player, time);
    }

    public long getSnowballCooldown(UUID player) { return snowball.getOrDefault(player, 0L); }


    /*
            BONE
     */
    private final Map<UUID, Long> bone = new HashMap<>();

    public void setBoneCooldown(UUID player, long time) {
        if (time < 1) bone.remove(player);
        else bone.put(player, time);
    }

    public long getBoneCooldown(UUID player) { return bone.getOrDefault(player, 0L); }


    /*
            INSTANT CRAPPLE
     */
    private final Map<UUID, Long> crapple = new HashMap<>();

    public void setCrappleCooldown(UUID player, long time) {
        if (time < 1) crapple.remove(player);
        else crapple.put(player, time);
    }

    public long getCrappleCooldown(UUID player) { return crapple.getOrDefault(player, 0L); }


    /*
            INSTANT GAPPLE
     */
    private final Map<UUID, Long> gapple = new HashMap<>();

    public void setGappleCooldown(UUID player, long time) {
        if (time < 1) gapple.remove(player);
        else gapple.put(player, time);
    }

    public long getGappleCooldown(UUID player) { return gapple.getOrDefault(player, 0L); }


    /*
            BAMBOOZLE
     */
    private final Map<UUID, Long> bamboozle = new HashMap<>();

    public void setBamboozleCooldown(UUID player, long time) {
        if (time < 1) bamboozle.remove(player);
        else bamboozle.put(player, time);
    }

    public long getBamboozleCooldown(UUID player) { return bamboozle.getOrDefault(player, 0L); }



    /*
            GRAPPLING HOOK
     */
    private final Map<UUID, Long> grapple = new HashMap<>();

    public void setGrappleCooldown(UUID player, long time) {
        if (time < 1) grapple.remove(player);
        else grapple.put(player, time);
    }

    public long getGrappleCooldown(UUID player) { return grapple.getOrDefault(player, 0L); }



    /*
            TIME-WARP PEARL
     */
    private final Map<UUID, Long> warp = new HashMap<>();

    public void setWarpCooldown(UUID player, long time) {
        if (time < 1) warp.remove(player);
        else warp.put(player, time);
    }

    public long getWarpCooldown(UUID player) { return warp.getOrDefault(player, 0L); }



    /*
            FAKE PEARL
     */
    private final Map<UUID, Long> fake = new HashMap<>();

    public void setFakePearlCooldown(UUID player, long time) {
        if (time < 1) fake.remove(player);
        else fake.put(player, time);
    }

    public long getFakePearlCooldown(UUID player) { return fake.getOrDefault(player, 0L); }



    /*
            SAVIOUR
     */
    private final Map<UUID, Long> saviour = new HashMap<>();

    public void setSaviourCooldown(UUID player, long time) {
        if (time < 1) saviour.remove(player);
        else saviour.put(player, time);
    }

    public long getSaviourCooldown(UUID player) { return saviour.getOrDefault(player, 0L); }



    /*
            PORTABLE BARD
     */
    private final Map<UUID, Long> portablebard = new HashMap<>();

    public void setPortableBardCooldown(UUID player, long time) {
        if (time < 1) portablebard.remove(player);
        else portablebard.put(player, time);
    }

    public long getPortableBardCooldown(UUID player) { return portablebard.getOrDefault(player, 0L); }



    /*
            RABBIT'S SOUL
     */
    private final Map<UUID, Long> rabbitssoul = new HashMap<>();

    public void setRabbitsSouldCooldown(UUID player, long time) {
        if (time < 1) rabbitssoul.remove(player);
        else rabbitssoul.put(player, time);
    }

    public long getRabbitsSoulCooldown(UUID player) { return rabbitssoul.getOrDefault(player, 0L); }
}
