package net.glowstone.block.blocktype;

import net.glowstone.GlowChunk;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TESkull;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.entity.GlowPlayer;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Skull;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BlockSkull extends BlockType {

    private final static Map<Location, SkullType> skullTypes = Collections.synchronizedMap(new HashMap<Location, SkullType>());

    /**
     * Gets the SkullType placed at the given Location and removes it from the internal Map.
     *
     * @param l Location to get SkullType on
     * @return SkullType
     */
    public static SkullType getSkullType(final Location l) {
        Validate.notNull(l, "Location cannot be null");
        synchronized (skullTypes) {
            return skullTypes.remove(l);
        }
    }

    private static void setSkullType(final Location l, final SkullType st) {
        synchronized (skullTypes) {
            skullTypes.put(l, st);
        }
    }

    public BlockSkull() {
        setDrops(new ItemStack(Material.SKULL_ITEM));
    }

    public TileEntity createTileEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new TESkull(chunk.getBlock(cx, cy, cz));
    }

    public boolean isWallSkull(BlockFace face) {
        return face == BlockFace.EAST || face == BlockFace.WEST || face == BlockFace.SOUTH || face == BlockFace.NORTH;
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        final MaterialData data = state.getData();
        if (!(data instanceof Skull)) {
            warnMaterialData(Skull.class, data);
            return;
        }
        final Skull s = (Skull) data;
        s.setFacingDirection(getFacing(player.getLocation(), face));
        setSkullType(state.getLocation(), getSkullType(holding.getDurability()));
    }

    private BlockFace getFacing(final Location l, final BlockFace face) {
        switch (face) {
            case NORTH:
            case SOUTH:
            case WEST:
            case EAST:
                return face;
            case UP:
                return getOppositeBlockFace(l, false);
            default:
                return BlockFace.SELF;
        }
    }

    public static SkullType getSkullType(final short skullType) {
        final SkullType[] sts = SkullType.values();
        return skullType < 0 || skullType >= sts.length ? SkullType.PLAYER : SkullType.values()[skullType];
    }

}
