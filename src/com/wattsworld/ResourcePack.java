package com.wattsworld;


import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.TextureArray;
import com.jme3.texture.plugins.AWTLoader;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ResourcePack {
    private TextureArray terrainTextureArray;
    private static ResourcePack instance;
    private HashMap<String, Integer> textureMap;

    public static ResourcePack getResourcePack() {
        if (instance == null)
            return loadDefaultResourcePack();
        return instance;
    }

    public TextureArray getTerrainTexture() {
        return terrainTextureArray;
    }

    private static ResourcePack loadDefaultResourcePack() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("ZIP File (Resource Pack)", "zip"));
        fileChooser.showOpenDialog(null);
        return new ResourcePack(fileChooser.getSelectedFile().getPath());
    }

    public int getTextureIndex(String tex) {
        if (tex.equals(BlockConst.air))
            return -1;
        return textureMap.get(tex);
    }

    public ResourcePack(String file) {
        ArrayList<Image> terrainTextures = new ArrayList<Image>();
        textureMap = new HashMap<String, Integer>();
        try {
            /*PrintWriter pw = new PrintWriter(new FileWriter("src/com/wattsworld/BlockConst.java"));
            pw.println("package com.wattsworld;");
            pw.println("public class BlockConst {");*/

            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
            ZipEntry entry;
            AWTLoader awtLoader = new AWTLoader();
            while ((entry = zipInputStream.getNextEntry()) != null) {
                //System.out.println(entry.getName());
                if (entry.getName().startsWith("assets/minecraft/textures/blocks") && entry.getName().endsWith(".png")) {
                    if (entry.getName().substring("assets/minecraft/textures/blocks/".length()).contains("/"))
                        continue;
                    Image i = awtLoader.load(new BufferedInputStream(zipInputStream), false);
                    if (i.getFormat() != Image.Format.ABGR8)
                        continue;
                    if (i.getWidth() == 32 && i.getHeight() == 32) {
                        //System.out.println (terrainTextures.size() + " " + entry.getName().substring(entry.getName().lastIndexOf("/")+1));
                        String texName = entry.getName().substring(entry.getName().lastIndexOf("/")+1, entry.getName().length()-".png".length());
                        //System.out.println(texName + " " + i);
                        //pw.println("    public static final String " + texName + " = \"" + texName + "\";" );
                        textureMap.put(texName, terrainTextures.size());
                        terrainTextures.add(i);
                    }
                }
                zipInputStream.closeEntry();
            }
            /*
            pw.println("}");
            pw.flush();
            pw.close();*/
            zipInputStream.close();
            terrainTextureArray = new TextureArray(terrainTextures);
            terrainTextureArray.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
            terrainTextureArray.setMagFilter(Texture.MagFilter.Nearest);

        } catch (IOException e) {
            e.printStackTrace();
        }
        instance = this;
    }
}
