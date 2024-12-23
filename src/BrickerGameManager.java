import brick_strategies.BrickStrategyFactory;
import gameobjects.*;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.*;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

public class BrickerGameManager extends GameManager {

    private static final float WINDOW_WIDTH = 700;
    private static final float WINDOW_HEIGHT = 500;

    private static final float CENTER_CONSTANT = 0.5f;

    public static final int BORDER_WIDTH = 20;

    private static final float BALL_DIAMETER = 35;
    private static final float BALL_VELOCITY = 200;
    private static final String BALL_TAG = "ball";

    private static final float PADDLE_HEIGHT = 20;
    private static final float PADDLE_WIDTH = 150;
    private static final int PADDLE_MIN_DISTANCE_FROM_EDGE = BORDER_WIDTH + 1;
    private static final float PADDLE_DISTANCE_FROM_BOTTOM = 30;

    private static final float BRICK_HEIGHT = 15;
    private static final int NUM_BRICKS_ROWS = 5;
    private static final int NUM_BRICKS_COLS = 8;
    private static final float DISTANCE_BETWEEN_BRICKS = 1;
    private static final float EXTREME_BRICK_DISTANCE_FROM_BORDER = 5;

    private static final float GRAPHIC_LIFE_HEIGHT = 30;
    private static final int NUM_GRAPHIC_LIVES = 4;
    private static final float EXTREME_GRAPHIC_LIFE_DISTANCE_FROM_BORDER = 5;
    private static final float DISTANCE_BETWEEN_GRAPHIC_LIVES = 3;

    private static final float NUMERIC_LIFE_COUNTER_HEIGHT = 20;
    private static final float NUMERIC_LIFE_COUNTER_DISTANCE_FROM_GRAPHIC_LIFE = 7;

    private static final String WIN_MSG = "You win!";
    private static final String LOSE_MSG = "You lose!";
    private static final String PLAY_AGAIN_MSG = " Play again?";

    private GameObject ball;
    private GameObject background;

    private Counter bricksCounter;
    private Counter livesCounter;

    private WindowController windowController;
    private Vector2 windowDimensions;

    /**
     * Creates a new window with the specified title and of the specified dimensions
     *
     * @param windowTitle      can be null to indicate the usage of the default window title
     * @param windowDimensions dimensions in pixels. can be null to indicate a
     */
    public BrickerGameManager(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
    }

    /**
     * The method will be called once when a GameGUIComponent is created,
     * and again after every invocation of windowController.resetGame().
     *
     * @param imageReader      Contains a single method: readImage, which reads an image from disk.
     *                         See its documentation for help.
     * @param soundReader      Contains a single method: readSound, which reads a wav file from
     *                         disk. See its documentation for help.
     * @param inputListener    Contains a single method: isKeyPressed, which returns whether
     *                         a given key is currently pressed by the user or not. See its
     *                         documentation.
     * @param windowController Contains an array of helpful, self-explanatory methods
     *                         concerning the window.
     * @see ImageReader
     * @see SoundReader
     * @see UserInputListener
     * @see WindowController
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        // initialization
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.windowController = windowController;
        windowDimensions = windowController.getWindowDimensions();

        // create ball
        createBall(imageReader, soundReader);

        // create paddle
        createPaddle(imageReader, inputListener);

        // create borders
        createBorders();

        BrickStrategyFactory brickStrategyFactory = new BrickStrategyFactory(gameObjects(), this, imageReader,
                soundReader, inputListener, windowController, windowDimensions);

        // create bricks
        createBricks(imageReader, brickStrategyFactory);

        // create background
        createBackground(imageReader);

        // create graphic lives
        createGraphicLives(imageReader);

        // create numerical life counter
        createNumericLifeCounter();

        // set camera coordinate space
        background.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
    }

    private void createBall(ImageReader imageReader, SoundReader soundReader) {
        Renderable ballImage = imageReader.readImage("assets/ball.png", true);
        Sound collisionSound = soundReader.readSound("assets/blop_cut_silenced.wav");
        ball = new Ball(Vector2.ZERO, new Vector2(BALL_DIAMETER, BALL_DIAMETER), ballImage, collisionSound);
        ball.setTag(BALL_TAG);
        repositionBall(ball);
        gameObjects().addGameObject(ball);
    }

    public void repositionBall(GameObject ball) {
        float ballVelX = BALL_VELOCITY;
        float ballVelY = BALL_VELOCITY;
        Random rand = new Random();
        if (rand.nextBoolean()) {
            ballVelX *= -1;
        }
        if (rand.nextBoolean()) {
            ballVelY *= -1;
        }
        ball.setVelocity(new Vector2(ballVelX, ballVelY));
        ball.setCenter(windowDimensions.mult(CENTER_CONSTANT));
    }

    private void createPaddle(ImageReader imageReader, UserInputListener inputListener) {
        Renderable paddleImage = imageReader.readImage("assets/paddle.png", true);
        GameObject paddle = new Paddle(Vector2.ZERO, new Vector2(PADDLE_WIDTH, PADDLE_HEIGHT), paddleImage,
                inputListener, windowDimensions, PADDLE_MIN_DISTANCE_FROM_EDGE);
        paddle.setCenter(new Vector2(windowDimensions.x() / 2,
                windowDimensions.y() - PADDLE_DISTANCE_FROM_BOTTOM));
        gameObjects().addGameObject(paddle);
    }

    private void createBorders() {
        // left, right and then top borders
        Vector2[][] borders_attributes = {{Vector2.ZERO, new Vector2(BORDER_WIDTH, windowDimensions.y())},
                {new Vector2(windowDimensions.x() - BORDER_WIDTH, 0), new Vector2(BORDER_WIDTH,
                        windowDimensions.y())},
                {new Vector2(BORDER_WIDTH, 0),
                        new Vector2(windowDimensions.x() - 2 * BORDER_WIDTH, BORDER_WIDTH)}};

        for (Vector2[] border_attributes : borders_attributes) {
            createBorder(border_attributes[0], border_attributes[1]);
        }
    }

    private void createBorder(Vector2 topLeftCorner, Vector2 dimensions) {
        gameObjects().addGameObject(
                new GameObject(
                        //anchored at top-left corner of the screen
                        topLeftCorner,
                        //height of border is the height of the screen
                        new Vector2(dimensions.x(), dimensions.y()),
                        //this game object is invisible; it doesn’t have a Renderable
                        new RectangleRenderable(Color.CYAN)
                ));
    }

    private void createBricks(ImageReader imageReader, BrickStrategyFactory brickStrategyFactory) {
        float bricksWidthToSpan =
                windowDimensions.x() - 2 * BORDER_WIDTH - 2 * EXTREME_BRICK_DISTANCE_FROM_BORDER -
                        (NUM_BRICKS_COLS - 1) * DISTANCE_BETWEEN_BRICKS;
        float brickWidth = bricksWidthToSpan / NUM_BRICKS_COLS;
        Vector2 brickDimensions = new Vector2(brickWidth, BRICK_HEIGHT);
        Renderable brickImage = imageReader.readImage("assets/brick.png", false);
        GameObjectCollection gameObjectCollection = gameObjects();
        bricksCounter = new Counter();
        // leftest top brick
        Vector2 initTopLeftCorner = new Vector2(BORDER_WIDTH + EXTREME_BRICK_DISTANCE_FROM_BORDER,
                BORDER_WIDTH + EXTREME_BRICK_DISTANCE_FROM_BORDER);

        for (int i = 0; i < NUM_BRICKS_ROWS; i++) {
            Vector2 curTopLeftCorner = initTopLeftCorner.add(new Vector2(0,
                    i * (BRICK_HEIGHT + DISTANCE_BETWEEN_BRICKS)));
            for (int j = 0; j < NUM_BRICKS_COLS; j++) {
                GameObject newBrick =
                        new Brick(curTopLeftCorner.add(
                                new Vector2(j * (brickWidth + DISTANCE_BETWEEN_BRICKS), 0)),
                                brickDimensions, brickImage, brickStrategyFactory.getStrategy(),
                                bricksCounter);
                bricksCounter.increment();
                gameObjectCollection.addGameObject(newBrick, Layer.STATIC_OBJECTS);
            }
        }
    }

    private void createBackground(ImageReader imageReader) {
        background = new GameObject(
                Vector2.ZERO,
                windowDimensions,
                imageReader.readImage("assets/DARK_BG2_Small.jpeg", false));
        gameObjects().addGameObject(background, Layer.BACKGROUND);
    }

    private void createGraphicLives(ImageReader imageReader) {
        float graphicLifeCounterWidth = windowDimensions.x() / 15;
        Vector2 graphicLifeDimensions = new Vector2(graphicLifeCounterWidth, GRAPHIC_LIFE_HEIGHT);
        Renderable graphicLiveImage = imageReader.readImage("assets/heart.png", false);
        Vector2 curTopLeftCorner = new Vector2(BORDER_WIDTH + EXTREME_GRAPHIC_LIFE_DISTANCE_FROM_BORDER,
                windowDimensions.y() - GRAPHIC_LIFE_HEIGHT - EXTREME_GRAPHIC_LIFE_DISTANCE_FROM_BORDER);
        GameObjectCollection gameObjectCollection = gameObjects();
        livesCounter = new Counter(NUM_GRAPHIC_LIVES);

        for (int i = 0; i < NUM_GRAPHIC_LIVES; i++) {
            GraphicLifeCounter graphicLifeCounter =
                    new GraphicLifeCounter(curTopLeftCorner.add(
                            new Vector2(i * (graphicLifeCounterWidth + DISTANCE_BETWEEN_GRAPHIC_LIVES), 0)),
                            graphicLifeDimensions, livesCounter, graphicLiveImage,
                            gameObjectCollection, i + 1);
            gameObjectCollection.addGameObject(graphicLifeCounter, Layer.BACKGROUND);
        }
    }

    private void createNumericLifeCounter() {
        Vector2 topLeftCorner =
                new Vector2(BORDER_WIDTH + EXTREME_GRAPHIC_LIFE_DISTANCE_FROM_BORDER,
                        windowDimensions.y() - GRAPHIC_LIFE_HEIGHT - EXTREME_GRAPHIC_LIFE_DISTANCE_FROM_BORDER).
                        subtract(new Vector2(0, NUMERIC_LIFE_COUNTER_HEIGHT +
                                NUMERIC_LIFE_COUNTER_DISTANCE_FROM_GRAPHIC_LIFE));
        Vector2 numericLifeCounterDimensions = new Vector2(windowDimensions.x() / 8,
                NUMERIC_LIFE_COUNTER_HEIGHT);
        GameObject numericLifeCounter = new NumericLifeCounter(livesCounter, topLeftCorner,
                numericLifeCounterDimensions, gameObjects());
        gameObjects().addGameObject(numericLifeCounter, Layer.BACKGROUND);
    }

    /**
     * Called once per frame. Any logic is put here. Rendering, on the other hand,
     * should only be done within 'render'.
     * Note that the time that passes between subsequent calls to this method is not constant.
     *
     * @param deltaTime The time, in seconds, that passed since the last invocation
     *                  of this method (i.e., since the last frame). This is useful
     *                  for either accumulating the total time that passed since some
     *                  event, or for physics integration (i.e., multiply this by
     *                  the acceleration to get an estimate of the added velocity or
     *                  by the velocity to get an estimate of the difference in position).
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        removeObjectsOutOfBounds();
        checkForGameEnd();
    }

    private void removeObjectsOutOfBounds() {
        GameObjectCollection gameObjectCollection = gameObjects();
        for (GameObject gameObject : gameObjectCollection) {
            if (!Objects.equals(gameObject.getTag(), BALL_TAG) && gameObject.getCenter().y() > windowDimensions.y()) {
                gameObjectCollection.removeGameObject(gameObject);
            }
        }
    }

    private void checkForGameEnd() {
        double ballHeight = ball.getCenter().y();

        String prompt = "";
        if (bricksCounter.value() <= 0) {
            // win
            prompt = WIN_MSG;
        }
        if (ballHeight > windowDimensions.y()) {
            livesCounter.decrement();
            if (livesCounter.value() <= 0) {
                // lose
                prompt = LOSE_MSG;
            } else {
                repositionBall(ball);
            }
        }
        if (!prompt.isEmpty()) {
            prompt += PLAY_AGAIN_MSG;
            if (windowController.openYesNoDialog(prompt)) {
                MockPaddle.isInstantiated = false;
                windowController.resetGame();
            } else {
                windowController.closeWindow();
            }
        }
    }

    public static void main(String[] args) {
        new BrickerGameManager("BrickBreaker++", new Vector2(WINDOW_WIDTH, WINDOW_HEIGHT)).run();
    }

}
