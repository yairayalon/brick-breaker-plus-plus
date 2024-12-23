package brick_strategies;

import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import gameobjects.MockPaddle;

public class AddPaddleStrategy extends RemoveBrickStrategyDecorator implements CollisionStrategy {

    private static final float MOCK_PADDLE_HEIGHT = 20;
    private static final float MOCK_PADDLE_WIDTH = 150;
    private static final int NUM_COLLISIONS_FOR_MOCK_PADDLE_DISAPPEARANCE = 3;
    private static final int MOCK_PADDLE_MIN_DISTANCE_FROM_EDGE = 21;

    private final ImageReader imageReader;
    private final UserInputListener inputListener;
    private final Vector2 windowDimensions;

    public AddPaddleStrategy(CollisionStrategy toBeDecorated, ImageReader imageReader,
                             UserInputListener inputListener, Vector2 windowDimensions) {
        super(toBeDecorated);
        this.imageReader = imageReader;
        this.inputListener = inputListener;
        this.windowDimensions = windowDimensions;
    }

    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj, Counter counter) {
        super.onCollision(thisObj, otherObj, counter);
        if (!MockPaddle.isInstantiated) {
            createMockPaddle(thisObj);
        }
    }

    private void createMockPaddle(GameObject thisObj) {
        Renderable mockPaddleImage = imageReader.readImage("assets/paddle.png", true);
        Vector2 mockPaddleTopLeftCorner = new Vector2(thisObj.getTopLeftCorner().x(),
                windowDimensions.y() / 2);
        GameObject mockPaddle = new MockPaddle(mockPaddleTopLeftCorner, new Vector2(MOCK_PADDLE_WIDTH,
                MOCK_PADDLE_HEIGHT), mockPaddleImage, inputListener, windowDimensions,
                getGameObjectCollection(), MOCK_PADDLE_MIN_DISTANCE_FROM_EDGE,
                NUM_COLLISIONS_FOR_MOCK_PADDLE_DISAPPEARANCE);
        getGameObjectCollection().addGameObject(mockPaddle);
    }

}
