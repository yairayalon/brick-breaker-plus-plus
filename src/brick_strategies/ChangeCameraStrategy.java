package brick_strategies;

import danogl.GameObject;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Counter;
import danogl.util.Vector2;
import danogl.GameManager;
import gameobjects.Ball;
import gameobjects.BallCollisionCountdownAgent;

import java.util.Objects;

public class ChangeCameraStrategy extends RemoveBrickStrategyDecorator implements CollisionStrategy {

    private static final float WIDEN_CONSTANT = 1.2f;
    private static final int NUM_MAX_BALL_COLLS_TO_TURN_OFF_CAMERA_CHANGE = 4;

    private final WindowController windowController;
    private final GameManager gameManager;

    public ChangeCameraStrategy(CollisionStrategy toBeDecorated, WindowController windowController,
                                GameManager gameManager) {
        super(toBeDecorated);
        this.windowController = windowController;
        this.gameManager = gameManager;
    }

    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj, Counter counter) {
        super.onCollision(thisObj, otherObj, counter);
        if (gameManager.getCamera() == null && Objects.equals(otherObj.getTag(), "ball")) {
            turnOnCameraChange((Ball) otherObj);
        }
    }

    public void turnOffCameraChange() {
        gameManager.setCamera(null);
    }

    private void turnOnCameraChange(Ball ball) {
        gameManager.setCamera(
                new Camera(
                        ball,            //ball to follow
                        Vector2.ZERO,    //follow the center of the object
                        windowController.getWindowDimensions().mult(WIDEN_CONSTANT),  //widen the frame a bit
                        windowController.getWindowDimensions()   //share the window dimensions
                )
        );
        GameObject ballCollisionCountdownAgent = new BallCollisionCountdownAgent(ball, this,
                ball.getCollisionCount() + NUM_MAX_BALL_COLLS_TO_TURN_OFF_CAMERA_CHANGE);
        getGameObjectCollection().addGameObject(ballCollisionCountdownAgent);
    }

}
