package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.blocktype.BlockSkull;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.SkullType;

public class TESkull extends TileEntity {

    private SkullType skullType = null;

    public TESkull(GlowBlock block) {
        super(block);
        setSaveId("Skull");
        skullType = BlockSkull.getSkullType(block.getLocation());
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        if (tag.containsKey("SkullType")) {
            skullType = BlockSkull.getSkullType(tag.getByte("SkullType"));
        }
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        if (skullType != null) {
            tag.putByte("SkullType", skullType.ordinal());
        }
    }

    @Override
    public void update(GlowPlayer player) {
        /*CompoundTag nbt = new CompoundTag();
        saveNbt(nbt);
        player.sendSkullChange(getBlock().getLocation(), nbt)
        not sure what this is accomplishing
        */
    }

}
