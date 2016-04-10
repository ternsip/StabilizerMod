package com.ternsip.stabilizermod;


import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.WorldEvent;

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
    public static final String VERSION = "2.2";
    public static final String AUTHOR = "Ternsip";
    public static final String MCVERSION = "1.7.*";

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
        registrant.register(new Container(event.world, event.x, event.y, event.z));
    }

    @SubscribeEvent
    public void breakBlock(BlockEvent.BreakEvent event) {
        registrant.unregister(new Container(event.world, event.x, event.y, event.z));
    }

    @SubscribeEvent
    public void unloadWorld(WorldEvent.Unload event) {
        registrant.unregister();
    }




}
