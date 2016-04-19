package com.ternsip.stabilizermod;

import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by TrnMain on 07.04.2016.
 */
public class Registrant {

    private ConcurrentHashMap<Long, HashSet<Container>> containers = new ConcurrentHashMap<Long, HashSet<Container>>();

    private int timer = 0;
    private int delay = 0;

    public Registrant(int delay) {
        this.delay = delay;
    }

    public void tick() {
        if (timer > 0) {
            --timer;
            return;
        } else {
            timer = delay;
        }
        for (HashSet<Container> chunkContainers : containers.values()) {
            for (Iterator<Container> iterator = chunkContainers.iterator(); iterator.hasNext();) {
                Container container = iterator.next();
                if (container.getChargeable().chargeableIC2()) {
                    container.balance();
                } else {
                    iterator.remove();
                }
            }
        }
    }

    public void register(final Container container) {
        if (!container.getChargeable().chargeableIC2()) {
            return;
        }
        long chunkIndex = getChunkID(container.getChunkX(), container.getChunkZ());
        if (containers.containsKey(chunkIndex)) {
            containers.get(chunkIndex).add(container);
        } else {
            containers.put(chunkIndex, new HashSet<Container>() {{add(container);}});
        }
    }

    public void unregister(final Container container) {
        long chunkIndex = getChunkID(container.getChunkX(), container.getChunkZ());
        if (containers.containsKey(chunkIndex)) {
            containers.get(chunkIndex).remove(container);
        }
    }

    public void unregister(int chunkX, int chunkZ) {
        containers.remove(getChunkID(chunkX, chunkZ));
    }

    public void register(World world, int chunkX, int chunkZ) {
        for (int y = 0; y < 256; ++y) {
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    register(new Container(world, x + 16 * chunkX, y, z + 16 * chunkZ));
                }
            }
        }
    }

    private static long getChunkID(int chunkX, int chunkZ) {
        return (long)chunkX << 32 | chunkZ & 0xFFFFFFFFL;
    }

    public void unregister() {
        containers = new ConcurrentHashMap<Long, HashSet<Container>>();
    }


}
