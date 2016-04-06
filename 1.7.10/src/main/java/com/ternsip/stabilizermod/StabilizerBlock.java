package com.ternsip.stabilizermod;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by TrnMain on 05.04.2016.
 */
public class StabilizerBlock extends Block {

    @SideOnly(Side.CLIENT)
    public IIcon icon = null;

    protected StabilizerBlock() {
        super(Material.iron);
        this.setBlockName("stabilizer");
        this.setBlockTextureName(Stabilizermod.MODID + ":" + "stabilizer");
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setHardness(2.0F);
        this.setResistance(6.0F);
        this.setLightLevel(0.0F);
        this.setHarvestLevel("pickaxe", 3);
        this.setStepSound(Block.soundTypeMetal);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return this.icon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        register.registerIcon("minecraft:iron_block");
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        world.scheduleBlockUpdate(x, y, z, this, 8);
        return super.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, meta);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        super.updateTick(world, x, y, z, random);
        TileEntity[] tiles = {
                world.getTileEntity(x, y - 1, z),
                world.getTileEntity(x, y + 1, z),
                world.getTileEntity(x - 1, y, z),
                world.getTileEntity(x + 1, y, z),
                world.getTileEntity(x, y, z - 1),
                world.getTileEntity(x, y, z + 1)
        };
        NBTTagCompound[] tags = new NBTTagCompound[6];
        int count = 0;
        long energy = 0;
        double[] values = new double[6];
        for (int i = 0; i < 6; ++i) {
            if (tiles[i] != null) {
                tags[i] = new NBTTagCompound();
                NBTTagCompound tag = tags[i];
                tiles[i].writeToNBT(tag);
                if (tag.hasKey("Energy")) {
                    values[i] = getValue(tag.getTag("Energy"));
                    energy += values[i];
                    ++count;
                } else if (tag.hasKey("energy")) {
                    values[i] = getValue(tag.getTag("energy"));
                    energy += values[i];
                    ++count;
                }
            }
        }
        if (count > 1 && energy > 0) {
            long average = energy / count;
            long leak = energy % count;
            for (int i = 0; i < 6; ++i) {
                NBTTagCompound tag = tags[i];
                if (tag != null) {
                    if (tag.hasKey("Energy")) {
                        setValue(tag, "Energy", values[i] - (long)values[i] + average + leak);
                        tiles[i].readFromNBT(tag);
                        leak = 0;
                    } else if (tag.hasKey("energy")) {
                        setValue(tag, "energy", values[i] - (long)values[i] + average + leak);
                        tiles[i].readFromNBT(tag);
                        leak = 0;
                    }
                }
            }
        }
        world.scheduleBlockUpdate(x, y, z, this, 8);
    }

    private static double getValue(NBTBase tag) {
        if (tag instanceof NBTTagDouble) {
            return ((NBTTagDouble) tag).func_150286_g();
        } else if (tag instanceof NBTTagInt) {
            return ((NBTTagInt) tag).func_150287_d();
        } else if (tag instanceof NBTTagLong) {
            return ((NBTTagLong) tag).func_150291_c();
        } else if (tag instanceof NBTTagShort) {
            return ((NBTTagShort) tag).func_150289_e();
        } else if (tag instanceof NBTTagFloat) {
            return ((NBTTagFloat) tag).func_150288_h();
        }
        return 0;
    }

    private static void setValue(NBTTagCompound tag, String key, double value) {
        NBTBase base = tag.getTag(key);
        if (base instanceof NBTTagDouble) {
            tag.setDouble(key, value);
        } else if (base instanceof NBTTagInt) {
            tag.setInteger(key, (int) value);
        } else if (base instanceof NBTTagLong) {
            tag.setLong(key, (long) value);
        } else if (base instanceof NBTTagShort) {
            tag.setShort(key, (short) value);
        } else if (base instanceof NBTTagFloat) {
            tag.setFloat(key, (float) value);
        }
    }

}
