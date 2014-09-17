package com.wattsworld;

import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;

public class ChunkMeshMaker {
    /* Using bitmasks to determine which faces to draw with just one integer */
    private static int TOP_FACE = pow(2, 0),
                        BOTTOM_FACE = pow(2, 1),
                        LEFT_FACE = pow(2, 2),
                        RIGHT_FACE = pow(2, 3),
                        FRONT_FACE = pow(2, 4),
                        BACK_FACE = pow(2, 5);
    private static int pow(int base, int exp) {
        if (exp == 0)
            return 1;
        return base*pow(base,exp-1);
    }
    public static Mesh tesselate(Chunk c) {
        int numFaces = countFaces(c);
        // 4 vertices in a face
        // 3 floats per vertex
        float vertData[] = new float[numFaces*4*3];
        int indexData[] = new int[numFaces*6];
        int indexIndex = 0;
        int vertIndex = 0;
        float texCoordsData[] = new float[numFaces*4*3];
        float[] texCoordsSide = { 0, 0, 0, 1, 1, 1, 1, 0};
        float[] texCoordsTop = { 0, 0, 1, 0, 1, 1, 0, 1};
        float[] texCoordsBottom = {0, 0,
                                   1, 0,
                                   1, 1,
                                   0, 1};
        //texCoordsBottom = texCoordsTop;
        //texCoordsSide = texCoordsTop;

        int texCoordsIndex = 0;
        int vertCount = 0;
        for (int x = 0; x < Chunk.SIZE_X; x++) {
            for (int z = 0; z < Chunk.SIZE_Z; z++) {
                for (int y = 0; y < Chunk.SIZE_Y; y++) {
                    int face = getFace(c, x, y, z);
                    int ind[] = {0, 1, 2, 2, 3, 0};
                    int block = c.get(x, y, z);
                    if ((face & TOP_FACE) != 0) {
                        // create top face
                        float v[] = { x-.5f, y+.5f, z+.5f,
                                      x+.5f, y+.5f, z+.5f,
                                      x+.5f, y+.5f, z-.5f,
                                      x-.5f, y+.5f, z-.5f};
                        for (int i = 0; i < v.length; i++) {
                            vertData[vertIndex++] = v[i];
                        }
                        for (int i = 0; i < ind.length; i++) {
                            indexData[indexIndex++] = ind[i]+vertCount;
                        }
                        vertCount += 4;
                        for (int i = 0; i < texCoordsTop.length; i++) {
                            texCoordsData[texCoordsIndex++] = texCoordsTop[i];
                            if ((i+1)%2 == 0)
                                texCoordsData[texCoordsIndex++] = block;
                        }
                    } if ((face & BOTTOM_FACE) != 0) {
                        // create bottom face
                        float v[] = { x-.5f, y-.5f, z+.5f,
                                x+.5f, y-.5f, z+.5f,
                                x+.5f, y-.5f, z-.5f,
                                x-.5f, y-.5f, z-.5f};
                        for (int i = 0; i < v.length; i++) {
                            vertData[vertIndex++] = v[i];
                        }
                        for (int i = 0; i < ind.length; i++) {
                            indexData[indexIndex++] = ind[i]+vertCount;
                        }
                        vertCount += 4;
                        for (int i = 0; i < texCoordsBottom.length; i++) {
                            texCoordsData[texCoordsIndex++] = texCoordsBottom[i];
                            if ((i+1)%2 == 0)
                                texCoordsData[texCoordsIndex++] = block;
                        }
                    } if ((face & LEFT_FACE) != 0) {
                        // create left face
                        float v[] = { x-.5f, y+.5f, z+.5f,
                                x-.5f, y-.5f, z+.5f,
                                x-.5f, y-.5f, z-.5f,
                                x-.5f, y+.5f, z-.5f};
                        for (int i = 0; i < v.length; i++) {
                            vertData[vertIndex++] = v[i];
                        }
                        for (int i = 0; i < ind.length; i++) {
                            indexData[indexIndex++] = ind[i]+vertCount;
                        }
                        vertCount += 4;
                        for (int i = 0; i < texCoordsSide.length; i++) {
                            texCoordsData[texCoordsIndex++] = texCoordsSide[i];
                            if ((i+1)%2 == 0)
                                texCoordsData[texCoordsIndex++] = block;
                        }
                    } if ((face & RIGHT_FACE) != 0) {
                        // create right face
                        float v[] = { x+.5f, y+.5f, z+.5f,
                                x+.5f, y-.5f, z+.5f,
                                x+.5f, y-.5f, z-.5f,
                                x+.5f, y+.5f, z-.5f};
                        for (int i = 0; i < v.length; i++) {
                            vertData[vertIndex++] = v[i];
                        }
                        for (int i = 0; i < ind.length; i++) {
                            indexData[indexIndex++] = ind[i]+vertCount;
                        }
                        vertCount += 4;
                        for (int i = 0; i < texCoordsSide.length; i++) {
                            texCoordsData[texCoordsIndex++] = texCoordsSide[i];
                            if ((i+1)%2 == 0)
                                texCoordsData[texCoordsIndex++] = block;
                        }
                    } if ((face & FRONT_FACE) != 0) {
                        // create front face
                        float v[] = { x-.5f, y+.5f, z-.5f,
                                x-.5f, y-.5f, z-.5f,
                                x+.5f, y-.5f, z-.5f,
                                x+.5f, y+.5f, z-.5f};
                        for (int i = 0; i < v.length; i++) {
                            vertData[vertIndex++] = v[i];
                        }
                        for (int i = 0; i < ind.length; i++) {
                            indexData[indexIndex++] = ind[i]+vertCount;
                        }
                        vertCount += 4;
                        for (int i = 0; i < texCoordsSide.length; i++) {
                            texCoordsData[texCoordsIndex++] = texCoordsSide[i];
                            if ((i+1)%2 == 0)
                                texCoordsData[texCoordsIndex++] = block;
                        }
                    } if ((face & BACK_FACE) != 0) {
                        // create back face
                        float v[] = { x-.5f, y+.5f, z+.5f,
                                x-.5f, y-.5f, z+.5f,
                                x+.5f, y-.5f, z+.5f,
                                x+.5f, y+.5f, z+.5f};
                        for (int i = 0; i < v.length; i++) {
                            vertData[vertIndex++] = v[i];
                        }
                        for (int i = 0; i < ind.length; i++) {
                            indexData[indexIndex++] = ind[i]+vertCount;
                        }
                        vertCount += 4;
                        for (int i = 0; i < texCoordsSide.length; i++) {
                            texCoordsData[texCoordsIndex++] = texCoordsSide[i];
                            if ((i+1)%2 == 0)
                                texCoordsData[texCoordsIndex++] = block;
                        }
                    }
                }
            }
        }
        Mesh mesh = new Mesh();
        //mesh.setMode(Mesh.Mode.Points);

        mesh.setBuffer(VertexBuffer.Type.Position, 3, vertData);
        mesh.setBuffer(VertexBuffer.Type.Index, 3, indexData);
        mesh.setBuffer(VertexBuffer.Type.TexCoord, 3, texCoordsData);
        mesh.updateBound();
        return mesh;
    }



    private static int getFace(Chunk c, int x, int y, int z) {
        int face = 0;
        if (c.get(x,y,z) == Block.getTextureIndex(BlockConst.air))
            return face;
        if (y != Chunk.SIZE_Y-1 && c.get(x,y+1,z) == Block.getTextureIndex(BlockConst.air))
            face |= TOP_FACE;
        if (y != 0 && c.get(x,y-1,z) == Block.getTextureIndex(BlockConst.air))
            face |= BOTTOM_FACE;
        if (c.get(x-1,y,z) == Block.getTextureIndex(BlockConst.air))
            face |= LEFT_FACE;
        if (c.get(x+1,y,z) == Block.getTextureIndex(BlockConst.air))
            face |= RIGHT_FACE;
        if (c.get(x,y,z-1) == Block.getTextureIndex(BlockConst.air))
            face |= FRONT_FACE;
        if (c.get(x,y,z+1) == Block.getTextureIndex(BlockConst.air))
            face |= BACK_FACE;
        return face;
    }

    private static int countFaces(Chunk c) {
        int faces = 0;
        for (int x = 0; x < Chunk.SIZE_X; x++) {
            for (int z = 0; z < Chunk.SIZE_Z; z++) {
                for (int y = 0; y < Chunk.SIZE_Y; y++) {
                    if (c.get(x,y,z) == Block.getTextureIndex(BlockConst.air))
                        continue;
                    int face =getFace(c,x,y,z);
                    if ((face & RIGHT_FACE) != 0)
                        faces++;
                    if ((face & LEFT_FACE) != 0)
                        faces++;
                    if ((face & TOP_FACE) != 0)
                        faces++;
                    if ((face & BOTTOM_FACE) != 0)
                        faces++;
                    if ((face & FRONT_FACE) !=0)
                        faces++;
                    if ((face & BACK_FACE) != 0)
                        faces++;
                    //faces += (int)(Math.log(getFace(c, x, y, z))/Math.log(2));
                }
            }
        }
        return faces;
    }

}
