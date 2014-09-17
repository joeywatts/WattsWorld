package com.wattsworld;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Chunk {
    public static final int SIZE_X = 16, SIZE_Z = 16, SIZE_Y = 128;
    private int[][][] data;
    private int locX, locZ;
    private Mesh mesh;
    private Geometry geom;
    //private Noise n;
    private PerlinNoise n;
    private CollisionShape m_colShape;
    private boolean addedToPhysicsSpace = false;
    public Chunk(PerlinNoise n, int x, int z) {
        data = new int[SIZE_X][SIZE_Y][SIZE_Z];
        locX = x;
        locZ = z;
        this.n = n;
        generateChunk();
        tesselateChunk();
    }

    private static final String[] blocks = {
            BlockConst.wool_colored_black,
            BlockConst.wool_colored_blue,
            BlockConst.wool_colored_brown,
            BlockConst.wool_colored_cyan,
            BlockConst.wool_colored_gray,
            BlockConst.wool_colored_green,
            BlockConst.wool_colored_light_blue,
            BlockConst.wool_colored_lime,
            BlockConst.wool_colored_magenta,
            BlockConst.wool_colored_orange,
            BlockConst.wool_colored_pink,
            BlockConst.wool_colored_purple,
            BlockConst.wool_colored_red,
            BlockConst.wool_colored_silver,
            BlockConst.wool_colored_white,
            BlockConst.wool_colored_yellow
    };

    private void generateChunk() {
        for (int x = 0; x < SIZE_X; x++)
            for (int y = 0; y < SIZE_Y; y++)
                for (int z = 0; z < SIZE_Z; z++) {
                    if (blockExists(x,y,z)) {
                        data[x][y][z] = Block.getTextureIndex(blocks[(int)(Math.random()*blocks.length)]);
                    } else
                        data[x][y][z] = Block.getTextureIndex(BlockConst.air);
                }
    }

    private boolean blockExists(int x, int y, int z) {
        double noiseVal = n.noise3((locX + x) / 20.0f, (SIZE_Y - y) / 20.0f, (locZ + z) / 20.0f);
        double heightVal = (double)(SIZE_Y-y)/SIZE_Y;
        noiseVal /= 3;
        double val = noiseVal + heightVal;
        return val > .5f;
    }
    public void tesselateChunk() {
        mesh = ChunkMeshMaker.tesselate(this);
        //data = null;
        geom = new Geometry(locX + " " + locZ, mesh);
        geom.setLocalTranslation(locX, 0, locZ);
        geom.setCullHint(Spatial.CullHint.Never);
        geom.setMaterial(Block.getBlockMaterial());
        m_colShape = new MeshCollisionShape(mesh);
        geom.addControl(new RigidBodyControl(m_colShape, 0));
    }
    public void addToPhysicsSpace(PhysicsSpace physicsSpace) {
        if (!addedToPhysicsSpace) {
            physicsSpace.add(geom);
            addedToPhysicsSpace = true;
        }
    }
    public void removeFromPhysicsSpace(PhysicsSpace physicsSpace) {
        if (addedToPhysicsSpace)
            physicsSpace.remove(geom);
    }
    public void addToNode(Node n) {
        if (n.hasChild(geom))
            return;
        n.attachChild(geom);
    }
    public void removeFromNode(Node n) {
        if (n.hasChild(geom))
            n.detachChild(geom);
    }
    public int get(int x, int y, int z) {
        if (x == SIZE_X || x == -1 || z == -1 || z == SIZE_Z) {
            return Block.getTextureIndex(blockExists(x,y,z)?BlockConst.dirt:BlockConst.air);
        }
        return data[x][y][z];
    }
    public int getX() {
        return locX;
    }
    public int getZ() {
        return locZ;
    }
}
