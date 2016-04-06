package com.ternsip.stabilizermod;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by TrnMain on 01.04.2016.
 */
public class Container {

    private int x, y, z;
    private TileEntity tileEntity;

    public Container(int x, int y, int z, TileEntity tileEntity) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.tileEntity = tileEntity;
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
        return tileEntity;
    }

    public static boolean chargeableIC2(TileEntity tile) {
        if (tile != null) {
            NBTTagCompound tag = new NBTTagCompound();
            tile.writeToNBT(tag);
            if (tag.hasKey("energy") && (tag.getTag("energy") instanceof NBTTagDouble)) {
                return true;
            }
        }
        return false;
    }

    public static boolean chargeableBC(TileEntity tile) {
        if (tile != null) {
            NBTTagCompound tag = new NBTTagCompound();
            tile.writeToNBT(tag);
            if (tag.hasKey("Energy") && (tag.getTag("Energy") instanceof NBTTagInt)) {
                return true;
            }
            if (tag.hasKey("energy") && (tag.getTag("energy") instanceof NBTTagInt)) {
                return true;
            }
        }
        return false;
    }

    public static double getIC2(TileEntity tile) {
        NBTTagCompound tag = new NBTTagCompound();
        tile.writeToNBT(tag);
        return tag.getDouble("energy");
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
        tag.setDouble("energy", energy);
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

    public void balance(World world) {
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
                    for (int voltage = 9; voltage >= 1; --voltage) {
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

}
