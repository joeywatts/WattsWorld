package com.wattsworld;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;

public class WattsWorld extends SimpleApplication {

    private World world;

    @Override
    public void simpleInitApp() {
        Block.loadMaterial(assetManager);
        world = new World();
        stateManager.attach(world);
        viewPort.setBackgroundColor(new ColorRGBA(0, 191/255.0f, 1, 1));
        flyCam.setMoveSpeed(10f);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        super.simpleRender(rm);
        /* preload the material in an attempt to fix hang on adding chunks to scene */
        Block.getBlockMaterial().preload(rm);
    }

    public static void main(String[] args) {
        WattsWorld app = new WattsWorld();
        app.start();
    }
}
