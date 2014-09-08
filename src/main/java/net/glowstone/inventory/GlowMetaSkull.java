package net.glowstone.inventory;

import net.glowstone.GlowServer;
import net.glowstone.entity.meta.PlayerProfile;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Map;

class GlowMetaSkull extends GlowMetaItem implements SkullMeta {

    static final int MAX_OWNER_LENGTH = 16;

    private PlayerProfile profile = null;

    @Override
    public SkullMeta clone() {
        return new GlowMetaSkull(this);
    }

    /**
     * Create a GlowMetaItem, copying from another if possible.
     *
     * @param meta The meta to copy from, or null.
     */
    public GlowMetaSkull(final GlowMetaItem meta) {
        super(meta);
        if (meta == null || !(meta instanceof GlowMetaSkull)) {
            return;
        }
        final GlowMetaSkull skull = (GlowMetaSkull) meta;
        profile = skull.profile;
    }

    @Override
    public String getOwner() {
        return profile.getName();
    }

    @Override
    public boolean hasOwner() {
        return profile != null;
    }

    @Override
    public boolean setOwner(String name) {
        if (name != null && name.length() > MAX_OWNER_LENGTH) {
            return false;
        }
        if (name == null) {
            profile = null;
        } else {
            profile = new PlayerProfile(name, ((GlowServer) Bukkit.getServer()).getPlayerDataService().lookupUUID(name));
        }
        return true;
    }

    public boolean isApplicable(Material material) {
        return material == Material.SKULL_ITEM;
    }

    void writeNbt(CompoundTag tag) {
        super.writeNbt(tag);
        if (hasOwner()) {
            tag.putString("owner", getOwner());
        }
    }

    void readNbt(CompoundTag tag) {
        super.readNbt(tag);
        if (tag.isString("owner")) {
            setOwner(tag.getString("owner"));
        }
    }

    public Map<String, Object> serialize() {
        final Map<String, Object> result = super.serialize();
        result.put("meta-type", "SKULL");
        if (hasOwner()) {
            result.put("owner", getOwner());
        }
        return result;
    }
}
