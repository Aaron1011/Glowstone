package net.glowstone.block.state;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.blocktype.BlockSkull;
import net.glowstone.block.entity.TESkull;
import net.glowstone.entity.meta.PlayerProfile;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Skull;

// Yes, this is ugly, but this Skull class is only used once, while the Material Skull is used a lot
public class GlowSkull extends GlowBlockState implements org.bukkit.block.Skull {

    private SkullType type;
    private PlayerProfile owner;
    private BlockFace rotation;

    public GlowSkull(GlowBlock block) {
        super(block);
        type = BlockSkull.getType(getTileEntity().getType());
        rotation = BlockSkull.getRotation(getTileEntity().getRotation());
        owner = getTileEntity().getOwner();
    }

    public TESkull getTileEntity() {
        return (TESkull) getBlock().getTileEntity();
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if(result) {
            TESkull skull = getTileEntity();
            skull.setType(BlockSkull.getType(type));
            if (canRotate((Skull) (getBlock().getState().getData()))) {
                skull.setRotation(BlockSkull.getRotation(rotation));
            }
            if(type == SkullType.PLAYER) {
                skull.setOwner(owner);
            }
            getTileEntity().updateInRange();
        }
        return result;
    }

    public static boolean canRotate(Skull skull) {
        return skull.getFacing() == BlockFace.SELF;
    }

    @Override
    public boolean hasOwner() {
        return owner != null;
    }

    @Override
    public String getOwner() {
        return (hasOwner() ? owner.getName() : null);
    }

    @Override
    public boolean setOwner(String name) {
        PlayerProfile owner = BlockSkull.getProfile(name);
        if(owner == null) {
            return false;
        }
        this.owner = owner;
        this.setSkullType(SkullType.PLAYER);
        return true;
    }

    @Override
    public BlockFace getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(BlockFace rotation) {
        this.rotation = rotation;
    }

    @Override
    public SkullType getSkullType() {
        return type;
    }

    @Override
    public void setSkullType(SkullType type) {
        if(type != SkullType.PLAYER) {
            owner = null;
        }
        this.type = type;
    }

}
