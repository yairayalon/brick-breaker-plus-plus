package gameobjects;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;

public class NumericLifeCounter extends GameObject {

    private final Counter livesCounter;
    private int livesCounterDisplayedNum;
    private final GameObjectCollection gameObjectCollection;
    private final TextRenderable textRenderable;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     */
    public NumericLifeCounter(Counter livesCounter, Vector2 topLeftCorner, Vector2 dimensions,
                              GameObjectCollection gameObjectCollection) {
        super(topLeftCorner, dimensions, new TextRenderable(String.format("Lives remaining: %d",
                livesCounter.value())));
        this.livesCounterDisplayedNum = livesCounter.value();
        this.livesCounter = livesCounter;
        this.gameObjectCollection = gameObjectCollection;
        this.textRenderable = (TextRenderable) renderer().getRenderable();
    }

    /**
     * Should be called once per frame.
     *
     * @param deltaTime The time elapsed, in seconds, since the last frame. Can
     *                  be used to determine a new position/velocity by multiplying
     *                  this delta with the velocity/acceleration respectively
     *                  and adding to the position/velocity:
     *                  velocity += deltaTime*acceleration
     *                  pos += deltaTime*velocity
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (livesCounter.value() < livesCounterDisplayedNum) {
            textRenderable.setString(String.format("Lives remaining: %d", --livesCounterDisplayedNum));
        }
        if (livesCounter.value() <= 0) {
            gameObjectCollection.removeGameObject(this, Layer.BACKGROUND);
        }
    }

}
