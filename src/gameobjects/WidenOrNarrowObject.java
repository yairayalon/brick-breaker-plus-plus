package gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

public class WidenOrNarrowObject extends GameObject {
    private static final float WIDEN_CONST = 1.2f;
    private static final float NARROW_CONST = 0.8f;
    private final GameObjectCollection gameObjectCollection;
    private final boolean isWiden;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner        Position of the object, in window coordinates (pixels).
     *                             Note that (0,0) is the top-left corner of the window.
     * @param dimensions           Width and height in window coordinates.
     * @param renderable           The renderable representing the object. Can be null, in which case
     */
    public WidenOrNarrowObject(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                               GameObjectCollection gameObjectCollection, boolean isWiden) {
        super(topLeftCorner, dimensions, renderable);
        this.gameObjectCollection = gameObjectCollection;
        this.isWiden = isWiden;
    }

    /**
     * Called on the first frame of a collision.
     *
     * @param other     The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (isWiden) {
            other.setDimensions(other.getDimensions().mult(WIDEN_CONST));
        } else {
            other.setDimensions(other.getDimensions().mult(NARROW_CONST));
        }
        gameObjectCollection.removeGameObject(this);
    }

    /**
     * Should this object be allowed to collide the specified other object.
     * If both this object returns true for the other, and the other returns true
     * for this one, the collisions may occur when they overlap, meaning that their
     * respective onCollisionEnter/onCollisionStay/onCollisionExit will be called.
     * Note that this assumes that both objects have been added to the same
     * GameObjectCollection, and that its handleCollisions() method is invoked.
     *
     * @param other The other GameObject.
     * @return true if the objects should collide. This does not guarantee a collision
     * would actually collide if they overlap, since the other object has to confirm
     * this one as well.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        return other instanceof Paddle;
    }

}
