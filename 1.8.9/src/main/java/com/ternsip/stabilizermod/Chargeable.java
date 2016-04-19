package com.ternsip.stabilizermod;

import akka.japi.pf.FI;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Created by TrnMain on 13.04.2016.
 */
public class Chargeable {

    private enum Style{

        ERR (0),
        INT (1),
        DBL (2);

        private final int value;

        Style(int value) {
            this.value = value;
        }

        public static Style valueOf(Type type) {
            if (type == double.class) return DBL;
            if (type == int.class) return Style.INT;
            return Style.ERR;
        }

    }

    private Object target = null;
    private Field field = null;
    private Style style = Style.ERR;
    private boolean ic2 = false;

    public Chargeable(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        if (tile != null) {
            this.ic2 = tile.getClass().getName().toLowerCase().contains("ic2");
            if (detectEnergy(tile, "energy")) {return;}
            if (detectEnergy(tile, "Energy")) {return;}
            Class superClass = tile.getClass();
            while (superClass != null) {
                try {
                    Field energyField = superClass.getDeclaredField("energy");
                    energyField.setAccessible(true);
                    detectEnergy(energyField.get(tile), "storage");
                    return;
                } catch (IllegalAccessException ignored) {} catch (NoSuchFieldException ignored) {}
                superClass = superClass.getSuperclass();
            }
        }
    }

    private boolean detectEnergy(Object target, String name) {
        Field field = null;
        try {field = target.getClass().getField(name);} catch (NoSuchFieldException ignored) {}
        try {field = target.getClass().getDeclaredField(name);} catch (NoSuchFieldException ignored) {}
        if (field != null) {
            field.setAccessible(true);
            Style style = Style.valueOf(field.getType());
            if (style == Style.ERR) {
                return false;
            }
            this.target = target;
            this.field = field;
            this.style = style;
            return true;
        }
        return false;
    }

    public boolean chargeable() {
        return field != null;
    }

    public boolean chargeableIC2() {
        return chargeable() && ic2;
    }

    public double getEnergy() {
        try {
            return style == Style.DBL ? field.getDouble(target) : field.getInt(target);
        } catch (IllegalAccessException ignored) {}
        return 0;
    }

    public void setEnergy(double energy) {
        try {
            if (style == Style.DBL) field.setDouble(target, energy); else field.setInt(target, (int) energy);
        } catch (IllegalAccessException ignored) {}
    }


}
