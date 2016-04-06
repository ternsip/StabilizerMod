package com.ternsip.stabilizermod;


import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by TrnMain on 02.04.2016.
 */

@Mod(   modid = Stabilizermod.MODID,
        name = Stabilizermod.MODNAME,
        version = Stabilizermod.VERSION,
        acceptableRemoteVersions = "*")
public class Stabilizermod {

    public static final String MODID = "stabilizermod";
    public static final String MODNAME = "StabilizerMod";
    public static final String VERSION = "1.2";
    public static final String AUTHOR = "Ternsip";
    public static final String MCVERSION = "1.7.*";

    private static ConcurrentHashMap<Long, LinkedList<Container>> containers = new ConcurrentHashMap<Long, LinkedList<Container>>();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void watchChunk(ChunkWatchEvent.Watch event) {
        Chunk chunk = event.player.worldObj.getChunkFromChunkCoords(event.chunk.chunkXPos, event.chunk.chunkZPos);
        int chunkX = chunk.xPosition;
        int chunkZ = chunk.zPosition;
        for (int y = 0; y < 256; ++y) {
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    Container container = new Container(x + 16 * chunkX, y, z + 16 * chunkZ, chunk.getTileEntityUnsafe(x, y, z));
                    if (Container.chargeableIC2(container.getTileEntity())) {
                        register(chunkX, chunkZ, container);
                    }
                }
            }
        }
    }


    @SubscribeEvent
    public void unwatchChunk(ChunkWatchEvent.UnWatch event) {
        unregister(event.chunk.chunkXPos, event.chunk.chunkZPos);
    }

    @SubscribeEvent
    public void worldTick(TickEvent.WorldTickEvent event) {
        for (LinkedList<Container> chunkContainers : containers.values()) {
            for (Iterator<Container> iterator = chunkContainers.iterator(); iterator.hasNext();) {
                Container container = iterator.next();
                if (Container.chargeableIC2(container.getTileEntity())) {
                    container.balance(event.world);
                } else {
                    iterator.remove();
                }
            }
        }
    }

    @SubscribeEvent
    public void placeBlock(BlockEvent.PlaceEvent event) {
        TileEntity tile = event.world.getTileEntity(event.x, event.y, event.z);
        if (Container.chargeableIC2(tile)) {
            final Container container = new Container(event.x, event.y, event.z, tile);
            Chunk chunk = event.world.getChunkFromBlockCoords(event.x, event.z);
            register(chunk.xPosition, chunk.zPosition, container);
        }
    }

    @SubscribeEvent
    public void breakBlock(BlockEvent.BreakEvent event) {
        TileEntity tile = event.world.getTileEntity(event.x, event.y, event.z);
        if (Container.chargeableIC2(tile)) {
            Chunk chunk = event.world.getChunkFromBlockCoords(event.x, event.z);
            unregister(chunk.xPosition, chunk.zPosition, event.x, event.y, event.z);
        }
    }

    public void register(int chunkX, int chunkZ, final Container container) {
        long chunkIndex = (long)chunkX << 32 | chunkZ & 0xFFFFFFFFL;
        if (containers.containsKey(chunkIndex)) {
            containers.get(chunkIndex).add(container);
        } else {
            containers.put(chunkIndex, new LinkedList<Container>() {{add(container);}});
        }
    }

    public void unregister(int chunkX, int chunkZ, int x, int y, int z) {
        long chunkIndex = (long)chunkX << 32 | chunkZ & 0xFFFFFFFFL;
        for (Iterator<Container> iterator = containers.get(chunkIndex).iterator(); iterator.hasNext();) {
            Container container = iterator.next();
            if (container.getX() == x && container.getY() == y && container.getZ() == z) {
                iterator.remove();
            }
        }
    }

    public void unregister(int chunkX, int chunkZ) {
        long chunkIndex = (long)chunkX << 32 | chunkZ & 0xFFFFFFFFL;
        containers.remove(chunkIndex);
    }


}
