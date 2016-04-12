package com.ternsip.stabilizermod;

import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by TrnMain on 01.04.2016.
 */
public class Container {

    private World world;
    private int x, y, z;

    public Container(World world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public TileEntity getTileEntity() {
        return world.getTileEntity(x, y, z);
    }

    private static boolean isIC2Experimental(NBTTagCompound tag) {
        return tag.getTag("energy") instanceof NBTTagDouble;
    }

    private static boolean isIC2Classic(NBTTagCompound tag) {
        return tag.hasKey("facing") && tag.getTag("energy") instanceof NBTTagInt;
    }

    private static boolean isBC(NBTTagCompound tag) {
        if (tag.hasKey("Energy")) {
            return tag.getTag("Energy") instanceof NBTTagInt;
        }
        return !tag.hasKey("facing") && tag.hasKey("energy") && (tag.getTag("energy") instanceof NBTTagInt);
    }

    public static boolean chargeableIC2(TileEntity tile) {
        if (tile == null) {
            return false;
        }
        NBTTagCompound tag = new NBTTagCompound();
        tile.writeToNBT(tag);
        return isIC2Experimental(tag) || isIC2Classic(tag);
    }

    public static boolean chargeableBC(TileEntity tile) {
        if (tile == null) {
            return false;
        }
        NBTTagCompound tag = new NBTTagCompound();
        tile.writeToNBT(tag);
        return isBC(tag);
    }

    public static double getIC2(TileEntity tile) {
        NBTTagCompound tag = new NBTTagCompound();
        tile.writeToNBT(tag);
        return isIC2Experimental(tag) ? tag.getDouble("energy") : tag.getInteger("energy");
    }

    public static int getBC(TileEntity tile) {
        NBTTagCompound tag = new NBTTagCompound();
        tile.writeToNBT(tag);
        if (tag.hasKey("Energy")) {
            return tag.getInteger("Energy");
        }
        return tag.getInteger("energy");
    }

    public static void setIC2(TileEntity tile, double energy) {
        NBTTagCompound tag = new NBTTagCompound();
        tile.writeToNBT(tag);
        if (isIC2Experimental(tag))
            tag.setDouble("energy", energy);
        else
            tag.setInteger("energy", (int) (energy + 0.25));
        tile.readFromNBT(tag);
    }

    public static void setBC(TileEntity tile, int energy) {
        NBTTagCompound tag = new NBTTagCompound();
        tile.writeToNBT(tag);
        if (tag.hasKey("Energy")) {
            tag.setInteger("Energy", energy);
        } else {
            tag.setInteger("energy", energy);
        }
        tile.readFromNBT(tag);
    }

    public void balance() {
        TileEntity tileEntity = getTileEntity();
        if (chargeableIC2(tileEntity)) {
            TileEntity[] tiles = {
                    world.getTileEntity(x, y - 1, z),
                    world.getTileEntity(x, y + 1, z),
                    world.getTileEntity(x - 1, y, z),
                    world.getTileEntity(x + 1, y, z),
                    world.getTileEntity(x, y, z - 1),
                    world.getTileEntity(x, y, z + 1)
            };
            double ic2Energy = getIC2(tileEntity);
            for (int i = 0; i < 6; ++i) {
                if (chargeableBC(tiles[i])) {
                    int energy = getBC(tiles[i]);
                    for (int voltage = 9; voltage >= 0; --voltage) {
                        int power = 1 << voltage;
                        if (ic2Energy - energy > power + 0.5) {
                            ic2Energy -= power;
                            energy += power;
                        }
                        if (energy - ic2Energy > power + 0.5) {
                            ic2Energy += power;
                            energy -= power;
                        }
                    }
                    setBC(tiles[i], energy);
                }
            }
            setIC2(tileEntity, ic2Energy);
        }
    }

    public int getChunkX() {
        return world.getChunkFromBlockCoords(x, z).xPosition;
    }

    public int getChunkZ() {
        return world.getChunkFromBlockCoords(x, z).zPosition;
    }

}
