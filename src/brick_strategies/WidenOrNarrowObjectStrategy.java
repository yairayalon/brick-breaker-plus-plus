package brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import gameobjects.WidenOrNarrowObject;

import java.util.Random;

public class WidenOrNarrowObjectStrategy extends RemoveBrickStrategyDecorator implements CollisionStrategy {

    private static final float WIDEN_OR_NARROW_OBJECT_VELOCITY = 180;

    private final ImageReader imageReader;

    public WidenOrNarrowObjectStrategy(CollisionStrategy toBeDecorated, ImageReader imageReader) {
        super(toBeDecorated);
        this.imageReader = imageReader;
    }

    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj, Counter counter) {
        super.onCollision(thisObj, otherObj, counter);
        createWidenOrNarrowObject(thisObj);
    }

    private void createWidenOrNarrowObject(GameObject thisObj) {
        Renderable widenOrNarrowObjectImage;
        boolean isWiden;
        Random rand = new Random();
        if (rand.nextBoolean()) {
            widenOrNarrowObjectImage = imageReader.readImage("assets/buffWiden.png",
                    false);
            isWiden = true;
        } else {
            widenOrNarrowObjectImage = imageReader.readImage("assets/buffNarrow.png",
                    false);
            isWiden = false;
        }
        GameObjectCollection gameObjectCollection = getGameObjectCollection();
        GameObject widenOrNarrowObject = new WidenOrNarrowObject(Vector2.ZERO, thisObj.getDimensions(),
                widenOrNarrowObjectImage, gameObjectCollection, isWiden);
        widenOrNarrowObject.setVelocity(new Vector2(0, WIDEN_OR_NARROW_OBJECT_VELOCITY));
        widenOrNarrowObject.setCenter(thisObj.getCenter());
        gameObjectCollection.addGameObject(widenOrNarrowObject);
    }

}
