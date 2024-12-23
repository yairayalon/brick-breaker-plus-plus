package brick_strategies;

import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import danogl.GameManager;

import java.util.Objects;
import java.util.Random;

public class BrickStrategyFactory {
    private static final String REMOVE_BRICK_STRATEGY_STRING = "removeBrickStrategy";
    private static final String PUCK_STRATEGY_STRING = "puckStrategy";
    private static final String ADD_PADDLE_STRATEGY_STRING = "addPaddleStrategy";
    private static final String CHANGE_CAMERA_STRATEGY_STRING = "changeCameraStrategy";
    private static final String WIDEN_OR_NARROW_OBJECT_STRATEGY_STRING = "widenOrNarrowObjectStrategy";
    private static final String DOUBLE_STRATEGY_STRING = "doubleStrategy";

    private final GameObjectCollection gameObjectCollection;
    private final GameManager gameManager;
    private final ImageReader imageReader;
    private final SoundReader soundReader;
    private final UserInputListener inputListener;
    private final WindowController windowController;
    private final Vector2 windowDimensions;
    private final String[] collisionStrategiesStrings;


    public BrickStrategyFactory(GameObjectCollection gameObjectCollection, GameManager gameManager,
                                ImageReader imageReader, SoundReader soundReader,
                                UserInputListener inputListener, WindowController windowController,
                                Vector2 windowDimensions) {
        this.gameObjectCollection = gameObjectCollection;
        this.gameManager = gameManager;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        this.inputListener = inputListener;
        this.windowController = windowController;
        this.windowDimensions = windowDimensions;
        this.collisionStrategiesStrings = new String[]{REMOVE_BRICK_STRATEGY_STRING, PUCK_STRATEGY_STRING,
                ADD_PADDLE_STRATEGY_STRING, CHANGE_CAMERA_STRATEGY_STRING,
                WIDEN_OR_NARROW_OBJECT_STRATEGY_STRING, DOUBLE_STRATEGY_STRING};
    }

    public CollisionStrategy getStrategy() {
        CollisionStrategy toBeDecorated = new RemoveBrickStrategy(gameObjectCollection);
        Random rand = new Random();
        String strategyString =
                collisionStrategiesStrings[rand.nextInt(collisionStrategiesStrings.length)];
        if (!Objects.equals(strategyString, DOUBLE_STRATEGY_STRING)) {
            return getStrategyAccordingString(strategyString, toBeDecorated);
        } else {        // double strategy chosen once
            String firstStrategyString =
                    collisionStrategiesStrings[rand.nextInt(collisionStrategiesStrings.length - 1) + 1];
            String secondStrategyString =
                    collisionStrategiesStrings[rand.nextInt(collisionStrategiesStrings.length - 1) + 1];

            if (!Objects.equals(firstStrategyString, DOUBLE_STRATEGY_STRING) && !Objects.equals(secondStrategyString, DOUBLE_STRATEGY_STRING)) {
                return getStrategyAccordingString(secondStrategyString,
                        getStrategyAccordingString(firstStrategyString, toBeDecorated));
            } else {        // double strategy chosen twice
                firstStrategyString =
                        collisionStrategiesStrings[rand.nextInt(collisionStrategiesStrings.length - 2) + 1];
                secondStrategyString =
                        collisionStrategiesStrings[rand.nextInt(collisionStrategiesStrings.length - 2) + 1];
                String thirdStrategyString =
                        collisionStrategiesStrings[rand.nextInt(collisionStrategiesStrings.length - 2) + 1];

                return getStrategyAccordingString(thirdStrategyString,
                        getStrategyAccordingString(secondStrategyString,
                                getStrategyAccordingString(firstStrategyString, toBeDecorated)));
            }
        }
    }

    private CollisionStrategy getStrategyAccordingString(String strategyString,
                                                         CollisionStrategy toBeDecorated) {
        switch (strategyString) {
            case REMOVE_BRICK_STRATEGY_STRING:
                return new RemoveBrickStrategy(gameObjectCollection);
            case PUCK_STRATEGY_STRING:
                return new PuckStrategy(toBeDecorated, imageReader,
                        soundReader);
            case ADD_PADDLE_STRATEGY_STRING:
                return new AddPaddleStrategy(toBeDecorated, imageReader,
                        inputListener, windowDimensions);
            case CHANGE_CAMERA_STRATEGY_STRING:
                return new ChangeCameraStrategy(toBeDecorated,
                        windowController, gameManager);
            case WIDEN_OR_NARROW_OBJECT_STRATEGY_STRING:
                return new WidenOrNarrowObjectStrategy(toBeDecorated,
                        imageReader);
            default:
                return null;
        }
    }

}
