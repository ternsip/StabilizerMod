package com.ternsip.stabilizermod;

import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.lang.reflect.Field;

/**
 * Created by TrnMain on 01.04.2016.
 */
public class Container {

    private World world;
    private int x, y, z;
    private Chargeable chargeable;

    public Container(World world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.chargeable = new Chargeable(world, x, y, z);
    }

    public void balance() {
        if (!chargeable.chargeableIC2()) {
            return;
        }
        Chargeable[] charges = {
                new Chargeable(world, x, y - 1, z),
                new Chargeable(world, x, y + 1, z),
                new Chargeable(world, x - 1, y, z),
                new Chargeable(world, x + 1, y, z),
                new Chargeable(world, x, y, z - 1),
                new Chargeable(world, x, y, z + 1)
        };
        double midEnergy = chargeable.getEnergy();
        for (int i = 0; i < 6; ++i) {
            if (!charges[i].chargeable() || charges[i].chargeableIC2()) {
                continue;
            }
            double energy = charges[i].getEnergy();
            for (int voltage = 12; voltage >= 0; --voltage) {
                int power = 1 << voltage;
                if ((int)midEnergy - (int)energy > 2 * power) {
                    midEnergy -= power;
                    energy += power;
                    break;
                }
                if ((int)energy - (int)midEnergy > 2 * power) {
                    midEnergy += power;
                    energy -= power;
                    break;
                }
            }
            charges[i].setEnergy(energy);
        }
        chargeable.setEnergy(midEnergy);
    }

    public int getChunkX() {
        return world.getChunkFromBlockCoords(x, z).xPosition;
    }

    public int getChunkZ() {
        return world.getChunkFromBlockCoords(x, z).zPosition;
    }

    public Chargeable getChargeable() {
        return chargeable;
    }
}
