package brick_strategies;

import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import gameobjects.Puck;

import java.util.Random;

public class PuckStrategy extends RemoveBrickStrategyDecorator implements CollisionStrategy {

    private static final int NUM_PUCKS_TO_CREATE = 1;
    private static final float PUCK_VELOCITY = 180;

    private final Renderable puckImage;
    private final Sound collisionSound;

    public PuckStrategy(CollisionStrategy toBeDecorated, ImageReader imageReader, SoundReader soundReader) {
        super(toBeDecorated);
        // sound and image are initialized here in order to prevent error message because of reading files
        // overload
        this.collisionSound = soundReader.readSound("assets/Bubble5_4.wav");
        this.puckImage = imageReader.readImage("assets/mockBall.png", true);
    }

    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj, Counter counter) {
        super.onCollision(thisObj, otherObj, counter);
        createPucks(thisObj);
    }

    private void createPucks(GameObject thisObj) {
        float puckVelX = PUCK_VELOCITY;
        Random rand = new Random();
        if (rand.nextBoolean()) {
            puckVelX *= -1;
        }
        Vector2 puckVel = new Vector2(puckVelX, PUCK_VELOCITY);

        Vector2 brickDimensions = thisObj.getDimensions();
        float longerBrickEdge = Math.max(brickDimensions.x(), brickDimensions.y());
        float puckDiameter = longerBrickEdge / 3;
        Vector2[] pucksCenters = getPucksCenters(thisObj, puckDiameter);

        for (int i = 0; i < NUM_PUCKS_TO_CREATE; i++) {
            GameObject puck = new Puck(Vector2.ZERO, new Vector2(puckDiameter, puckDiameter), puckImage,
                    collisionSound);
            puck.setVelocity(puckVel);
            puck.setCenter(pucksCenters[i]);
            getGameObjectCollection().addGameObject(puck);
        }
    }

    private Vector2[] getPucksCenters(GameObject brick, float puckDiameter) {
        Vector2[] pucksCenters = new Vector2[NUM_PUCKS_TO_CREATE];
        Vector2 brickTopLeftCorner = brick.getTopLeftCorner();
        Vector2 brickDimensions = brick.getDimensions();
        float firstPuckCenterX = brickTopLeftCorner.x() + brickDimensions.x() / NUM_PUCKS_TO_CREATE / 2;
        float puckCenterY = brickTopLeftCorner.y() + brickDimensions.y() / 2 + puckDiameter / 2;
        pucksCenters[0] = new Vector2(firstPuckCenterX, puckCenterY);
        for (int i = 1; i < NUM_PUCKS_TO_CREATE; i++) {
            pucksCenters[i] = new Vector2(firstPuckCenterX + i * puckDiameter, puckCenterY);
        }
        return pucksCenters;
    }

}
