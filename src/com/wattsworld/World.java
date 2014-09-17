package com.wattsworld;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.input.InputManager;
import com.jme3.scene.Node;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class World extends AbstractAppState {
    private ConcurrentLinkedQueue<Chunk> chunks;
    private Node worldNode;
    private long seed;
    private PerlinNoise noise;
    private WattsWorld app;
    private WorldGenerator worldGeneratorRunnable;
    private Thread worldGenerator;
    private Player player;
    private BulletAppState bulletAppState;

    private static final int CHUNK_VIEW_DIST_X = 8;
    private static final int CHUNK_VIEW_DIST_Z = 8;

    public World(long seed) {
        worldNode = new Node("com.wattsworld.World - Seed: " + seed);
        this.seed = seed;
        noise = new PerlinNoise(seed);
        chunks = new ConcurrentLinkedQueue<Chunk>();
    }

    public World() {
        this(0);
    }

    public Node getWorldNode() {
        return worldNode;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (WattsWorld)app;
        this.app.getRootNode().attachChild(worldNode);
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.setDebugEnabled(true);
        //bulletAppState.getPhysicsSpace().enableDebug(app.getAssetManager());
        worldGenerator = new Thread(worldGeneratorRunnable = new WorldGenerator());
        worldGenerator.start();
        player = new Player(app.getCamera(), this);
    }

    public InputManager getInputManager() {
        return app.getInputManager();
    }

    public PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

    @Override
    public void cleanup() {
        super.cleanup();
        app.getRootNode().detachChild(worldNode);
        if (worldGenerator != null && worldGenerator.isAlive()) {
            worldGeneratorRunnable.stop();
            worldGeneratorRunnable = null;
            worldGenerator = null;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            app.getRootNode().attachChild(worldNode);
            worldGenerator = new Thread(worldGeneratorRunnable = new WorldGenerator());
            worldGenerator.start();
        } else {
            app.getRootNode().detachChild(worldNode);
            worldGeneratorRunnable.stop();
            worldGenerator = null;
            worldGeneratorRunnable = null;
        }
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        /* Update WattsWorld Player */
        player.update(tpf);
    }

    private static int floor(double a) {
        return a>0?(int)a:(int)(a-1);
    }

    private class WorldGenerator implements Runnable {
        private boolean shouldStop = false;
        public void stop() {
            shouldStop = true;
        }

        private boolean generateChunk(int chunkX, int chunkZ) {
            for (Chunk c : chunks) {
                if (c.getX() == -chunkX && c.getZ() == -chunkZ)
                    return false;
            }
            long time = System.currentTimeMillis();
            final Chunk c = new Chunk(noise, -chunkX, -chunkZ);
            chunks.add(c);

            Future<Boolean> bool = app.enqueue(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    c.addToNode(worldNode);
                    c.addToPhysicsSpace(bulletAppState.getPhysicsSpace());
                    return true;
                }
            });
            try {
                bool.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            System.out.println("com.wattsworld.Chunk (" + chunkX + ", " + chunkZ + ") generated in " + (System.currentTimeMillis()-time) + " ms");
            return true;
        }

        @Override
        public void run() {
            while (!shouldStop) {
                double camX = app.getCamera().getLocation().getX();
                double camZ = app.getCamera().getLocation().getZ();
                int chunkCountX = floor(camX/Chunk.SIZE_X);
                int chunkCountZ = floor(camZ/Chunk.SIZE_Z);
                int ccx = chunkCountX*Chunk.SIZE_X;
                int ccz = chunkCountZ*Chunk.SIZE_Z;
                int ccx2 = ccx+Chunk.SIZE_X;
                int ccz2 = ccz+Chunk.SIZE_Z;

                for (final Chunk c : chunks) {
                    if (Math.abs(c.getX()/Chunk.SIZE_X-chunkCountX) > CHUNK_VIEW_DIST_X+1 ||
                            Math.abs(c.getZ()/Chunk.SIZE_Z-chunkCountZ) > CHUNK_VIEW_DIST_Z+1) {
                        chunks.remove(c);
                        Future<Boolean> bool = app.enqueue(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                c.removeFromNode(worldNode);
                                c.removeFromPhysicsSpace(bulletAppState.getPhysicsSpace());
                                return true;
                            }
                        });
                        try {
                            bool.get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                }

                for (int cx = 0; cx <= CHUNK_VIEW_DIST_X*Chunk.SIZE_X; cx+=Chunk.SIZE_X) {
                    for (int cz = 0; cz <= CHUNK_VIEW_DIST_Z*Chunk.SIZE_Z; cz+=Chunk.SIZE_Z) {
                        int chunkX = cx-ccx;
                        int chunkZ = cz-ccz;
                        generateChunk(chunkX, chunkZ);
                        chunkX = -cx-ccx2;
                        generateChunk(chunkX, chunkZ);
                        chunkZ = -cz-ccz2;
                        generateChunk(chunkX, chunkZ);
                        chunkX = cx-ccx;
                        generateChunk(chunkX, chunkZ);
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
