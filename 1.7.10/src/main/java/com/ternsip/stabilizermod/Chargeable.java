package com.ternsip.stabilizermod;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.lang.reflect.Field;

/**
 * Created by TrnMain on 13.04.2016.
 */
public class Chargeable {

    private TileEntity tile = null;
    private Field field = null;

    public Chargeable(World world, int x, int y, int z) {
        this.tile = world.getTileEntity(x, y, z);
        if (tile != null) {
            try {this.field = this.tile.getClass().getField("energy");} catch (NoSuchFieldException ignored) {}
            try {this.field = this.tile.getClass().getField("Energy");} catch (NoSuchFieldException ignored) {}
        }
    }

    public boolean chargeable() {
        return this.field != null;
    }

    public boolean chargeableIC2() {
        return chargeable() && tile.getClass().getName().toLowerCase().contains("ic2");
    }

    public double getEnergy() {
        try {return this.field.getDouble(tile);} catch (IllegalAccessException ignored) {}
        try {return this.field.getInt(tile);} catch (IllegalAccessException ignored) {}
        return 0;
    }

    public void setEnergy(double energy) {
        try {this.field.setDouble(this.tile, energy); return;} catch (IllegalAccessException ignored) {} catch (IllegalArgumentException ignored) {}
        try {this.field.setInt(this.tile, (int) energy); return;} catch (IllegalAccessException ignored) {} catch (IllegalArgumentException ignored) {}
    }


}
