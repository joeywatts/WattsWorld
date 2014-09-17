package com.wattsworld;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.FlyByCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;

public class Player extends Node implements ActionListener, AnalogListener {
    private BetterCharacterControl control;
    private CameraNode camNode;
    private Vector3f velocity;
    private World world;
    private FlyByCamera camera;

    public static final float HEIGHT = 1.5f;
    public static final float WIDTH = .75f;
    public static final float MASS = 5f;
    public static final float MOUSE_SENSITIVITY = 1f;
    public Player(Camera camera, World world) {
        camNode = new CameraNode("Player",camera);
        camNode.setControlDir(CameraControl.ControlDirection.CameraToSpatial);
        this.setLocalTranslation(0,200,0);
        this.attachChild(camNode);
        velocity = new Vector3f();
        this.world = world;
        control = new BetterCharacterControl(HEIGHT, WIDTH, MASS);
        world.getWorldNode().attachChild(this);
        registerInputs();
    }

    private void registerInputs() {
        world.getInputManager().addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
        world.getInputManager().addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        world.getInputManager().addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        world.getInputManager().addMapping("Back", new KeyTrigger(KeyInput.KEY_S));
        world.getInputManager().addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        world.getInputManager().addListener(this, "Forward", "Left", "Right", "Back", "Jump");
        world.getInputManager().addMapping("RotateLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        world.getInputManager().addMapping("RotateRight", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        world.getInputManager().addMapping("RotateUp", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        world.getInputManager().addMapping("RotateDown", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        world.getInputManager().addListener(this, "RotateLeft", "RotateRight", "RotateUp", "RotateDown");
        world.getInputManager().setCursorVisible(false);
    }

    private boolean forward = false, back = false, right = false, left = false;
    private Vector3f walkDirection = new Vector3f();

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("Forward"))
            forward = isPressed;
        if (name.equals("Back"))
            back = isPressed;
        if (name.equals("Right"))
            right = isPressed;
        if (name.equals("Left"))
            left = isPressed;
        if (name.equals("Jump"))
            control.jump();
    }

    private Quaternion q = new Quaternion();
    private Vector3f viewDirection = new Vector3f();
    private float yaw = 0, pitch = 0;

    private void rotateCamera(float value, Vector3f axis) {
        Matrix3f mat = new Matrix3f();
        mat.fromAngleNormalAxis(MOUSE_SENSITIVITY * value, axis);

        Vector3f up = camNode.getCamera().getUp();
        Vector3f left = camNode.getCamera().getLeft();
        Vector3f dir = camNode.getCamera().getDirection();

        mat.mult(up, up);
        mat.mult(left, left);
        mat.mult(dir, dir);

        Quaternion q = new Quaternion();
        q.fromAxes(left, up, dir);
        q.normalizeLocal();
        camNode.setLocalRotation(q);
        //cam.setAxes(q);
    }
    private void updateRotation() {
        q.fromAngles(yaw, pitch, 0);
        camNode.getCamera().setRotation(q);
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (name.equals("RotateLeft")){
            pitch += value;
        }else if (name.equals("RotateRight")){
            pitch -= value;
        }else if (name.equals("RotateUp")){
            yaw -= value;
        }else if (name.equals("RotateDown")){
            yaw += value;
        }
        updateRotation();
    }

    public Vector3f getLocation() {
        return camNode.getCamera().getLocation();
    }
    public Vector3f getDirection() {
        return camNode.getCamera().getDirection();
    }
    public Vector3f getVelocity() {
        return control.getVelocity();
    }

    public void update(float tpf) {
        Vector3f camDir = camNode.getCamera().getDirection().clone().multLocal(0.25f);
        Vector3f camLeft = camNode.getCamera().getLeft().clone().multLocal(0.25f);
        camDir.y = 0;
        camLeft.y = 0;
        walkDirection.set(0,0,0);
        if (left) walkDirection.addLocal(camLeft);
        if (right) walkDirection.addLocal(camLeft.negate());
        if (forward) walkDirection.addLocal(camDir);
        if (back) walkDirection.addLocal(camDir.negate());
        walkDirection.multLocal(5f);
        control.setWalkDirection(walkDirection);
    }

    public float getX() {
        return -getLocation().getX();
    }

    public float getZ() {
        return -getLocation().getZ();
    }
}
