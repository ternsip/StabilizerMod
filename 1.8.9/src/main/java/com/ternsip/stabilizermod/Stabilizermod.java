package com.ternsip.stabilizermod;


import net.minecraft.util.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

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
    public static final String VERSION = "3.4";
    public static final String AUTHOR = "Ternsip";
    public static final String MCVERSION = "1.8.*";

    public static Registrant registrant;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        registrant = new Registrant(32);
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void watchChunk(ChunkWatchEvent.Watch event) {
        registrant.register(event.player.worldObj, event.chunk.chunkXPos, event.chunk.chunkZPos);

    }

    @SubscribeEvent
    public void unwatchChunk(ChunkWatchEvent.UnWatch event) {
        registrant.unregister(event.chunk.chunkXPos, event.chunk.chunkZPos);
    }

    @SubscribeEvent
    public void worldTick(TickEvent.WorldTickEvent event) {
        registrant.tick();
    }

    @SubscribeEvent
    public void placeBlock(BlockEvent.PlaceEvent event) {
        registrant.register(new Container(event.world, event.pos.getX(), event.pos.getY(), event.pos.getZ()));
    }

    @SubscribeEvent
    public void breakBlock(BlockEvent.BreakEvent event) {
        registrant.unregister(new Container(event.world, event.pos.getX(), event.pos.getY(), event.pos.getZ()));
    }

    @SubscribeEvent
    public void unloadWorld(WorldEvent.Unload event) {
        registrant.unregister();
    }




}
