package net.glowstone.block.blocktype;

import net.glowstone.GlowChunk;
import net.glowstone.GlowServer;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TESkull;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.block.state.GlowSkull;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.profile.PlayerProfile;
import net.glowstone.entity.meta.profile.ProfileCache;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import static org.bukkit.block.BlockFace.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Skull;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

public class BlockSkull extends BlockType {

    public static final int MAX_OWNER_LENGTH = 16;

    private static final BlockFace[] ROTATIONS = new BlockFace[]{SOUTH, SOUTH_SOUTH_WEST,
        SOUTH_WEST, WEST_SOUTH_WEST, WEST, WEST_NORTH_WEST, NORTH_WEST, NORTH_NORTH_WEST,
        NORTH, NORTH_NORTH_EAST, NORTH_EAST, EAST_NORTH_EAST, EAST, EAST_SOUTH_EAST, SOUTH_EAST, SOUTH_SOUTH_EAST
    };


    public BlockSkull() {
        setDrops(new ItemStack(Material.SKULL_ITEM));
    }

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        return BlockFace.DOWN != against; // Skulls can't be placed on bottom of block
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        MaterialData data = state.getData();
        if (!(data instanceof Skull)) {
            warnMaterialData(Skull.class, data);
            return;
        }
        Skull skull = (Skull) data;
        skull.setFacingDirection(face);
    }

    @Override
    public TileEntity createTileEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new TESkull(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding) {
        GlowSkull skull = (GlowSkull) block.getState();
        skull.setSkullType(getType(holding.getDurability()));
        if (skull.getSkullType() == SkullType.PLAYER) {
            SkullMeta meta = (SkullMeta) holding.getItemMeta();
            if (meta != null) {
                skull.setOwner(meta.getOwner());
            }
        }
        MaterialData data = skull.getData();
        if (!(data instanceof Skull)) {
            warnMaterialData(Skull.class, data);
            return;
        }
        Skull skullData = (Skull) data;

        if (skullData.getFacing() == BlockFace.SELF) { // Can be rotated
            // Calculate the rotation based on the player's facing direction
            Location loc = player.getLocation();
            // 22.5 = 360 / 16
            long facing = Math.round(loc.getYaw() / 22.5) + 8;
            byte rotation = (byte) (((facing % 16) + 16) % 16);
            skull.setRotation(getRotation(rotation));
        }
        skull.update();
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block) {
        GlowSkull skull = (GlowSkull) block.getState();

        ItemStack drop = new ItemStack(Material.SKULL_ITEM, 1);
        if (skull.hasOwner()) {
            SkullMeta meta = (SkullMeta) drop.getItemMeta();
            meta.setOwner(skull.getOwner());
            drop.setItemMeta(meta);
        }
        drop.setDurability((short) skull.getSkullType().ordinal());

        return Arrays.asList(drop);
    }

    public static PlayerProfile getProfile(String name) {
        if (name == null || name.length() > MAX_OWNER_LENGTH || name.isEmpty()) return null;

        UUID uuid = ProfileCache.getUUID(name);
        if (uuid != null) {
            return ProfileCache.getProfile(uuid);
        }
        GlowServer.logger.warning("Unable to get UUID for player " + name);
        return null;
    }

    public static BlockFace getRotation(byte rotation) {
        return ROTATIONS[rotation];
    }

    public static byte getRotation(BlockFace rotation) {
        return (byte) Arrays.asList(ROTATIONS).indexOf(rotation);
    }

    public static SkullType getType(int id) {
        if (id >= SkullType.values().length || id < 0) throw new IllegalArgumentException("ID not a Skull type: " + id);
        return SkullType.values()[id];
    }

    public static byte getType(SkullType type) {
        return (byte) type.ordinal();
    }

    public static boolean canRotate(Skull skull) {
        return skull.getFacing() == BlockFace.SELF;
    }
}
