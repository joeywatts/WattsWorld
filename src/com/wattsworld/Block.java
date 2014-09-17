package com.wattsworld;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;

public class Block {
    private static Material blockMaterial;
    public static void loadMaterial(AssetManager assetManager) {
        blockMaterial = new Material(assetManager,"MatDefs/UnshadedArray.j3md");
        blockMaterial.setTexture("ColorMap", ResourcePack.getResourcePack().getTerrainTexture());
        //blockMaterial.getAdditionalRenderState().setWireframe(true);
        //blockMaterial.setTextureParam(Texture.);
        blockMaterial.getAdditionalRenderState().setAlphaTest(true);
        blockMaterial.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
    }
    public static Material getBlockMaterial() {
        return blockMaterial;
    }
    public static int getTextureIndex(String tex) {
        return ResourcePack.getResourcePack().getTextureIndex(tex);
    }
}
