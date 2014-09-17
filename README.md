WattsWorld   
==========
by *Joey Watts*

Inspired by *Minecraft*, WattsWorld is a 3D block world project written in Java using jMonkeyEngine 3.0. WattsWorld was my final project for AP Computer Science in my sophomore year of high school. Since my sophomore year, I've added a few improvements and cleaned up the code a little bit, but the overall rendering code remains intact from sophomore year (2012). The purpose of this project was to teach me about 3D game development.

Features
--------
* Full 3D block world rendering with fly camera to move around.
* World generation using 3D noise (using open-source J3D implementation)
* Infinite terrain using threading
* Multiple block textures **(added loading of resource pack zip files after sophomore year)**

Notes & Usage
-------------
* You must download a Minecraft resource pack to use WattsWorld. When you run the project, a file chooser will pop up and ask you to choose the resource pack zip file. You can modify this behavior in [this method](src/com/wattsworld/ResourcePack.java#L34).
* This project depends on JME3, so in order to build it, you must have the JME3 jars in your project's build path.

