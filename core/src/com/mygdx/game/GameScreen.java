package com.mygdx.game;
import static java.lang.String.valueOf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GameScreen implements Screen {
    private OrthographicCamera camera;
    private Texture background;
    private MyGdxGame game;
    public enum GameState { PLAYING, COMPLETE }
    GameState gameState = GameState.PLAYING;

    public static final float MOVEMENT_SPEED = 200.0f;
    public static final float GOAL_BOB_HEIGHT = 5.0f;

    //Game clock
    float dt;

    //Player Character
    Texture playerTexture;
    Sprite playerSprite;
    Vector2 playerDelta;
    Rectangle playerDeltaRectangle;
    Vector2 playerPosition;


    //Enemy Character
    Texture enemyTexture;
    Sprite enemySprite;
    Vector2 enemyDelta;
    Rectangle enemyDeltaRectangle;
    Vector2 enemyPosition;

    //Missile
    Texture missileTexture;
    Sprite missileSprite;
    Vector2 missileDelta;
    Rectangle missileDeltaRectangle;
    Vector2 missilePosition;

    //Map and rendering
    SpriteBatch spriteBatch;
    SpriteBatch uiBatch; //Second SpriteBatch without camera transforms, for drawing UI

    //UI textures
    Texture buttonSquareTexture;
    Texture buttonSquareDownTexture;
    Texture buttonLongTexture;
    Texture buttonLongDownTexture;
    Texture buttonAttackTexture;

    //UI Buttons
    Button moveLeftButton;
    Button moveRightButton;
    Button moveDownButton;
    Button moveUpButton;
    Button restartButton;
    Button attackButton;
    boolean restartActive;

    float backgroundX = 0;

    // constructor to keep a reference to the main Game class
    public GameScreen(MyGdxGame game) {
        this.game = game;
    }
    @Override
    public void show() {
        create();
    }

    @Override
    public void hide() {
        Gdx.app.log("GameScreen: ", "gameScreen hide called");
    }

    @Override
    public void render(float delta) {
        dt = Gdx.graphics.getDeltaTime();
        //Update the Game State
        update();

        //Clear the screen every frame before drawing.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA); //Allows transparent sprites/tiles
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        //playerSprite.setPosition(100,100);

        //playerSprite.setX(100); // Set player sprite's x-coordinate to 0 (left side of the screen)
        //playerSprite.setY(Gdx.graphics.getHeight() / 2 - playerSprite.getHeight() / 2); // Set player sprite's y-coordinate to the middle of the screen
        spriteBatch.draw(background, backgroundX, 0);  //first background
        spriteBatch.draw(background, backgroundX+background.getWidth(), 0); //second background

        playerSprite.setX(playerPosition.x);
        playerSprite.setY(playerPosition.y);
        playerSprite.draw(spriteBatch);

        enemySprite.setX(enemyPosition.x);
        enemySprite.setY(enemyPosition.y);
        enemySprite.draw(spriteBatch);

        missileSprite.setX(playerPosition.x + playerSprite.getWidth());
        missileSprite.setY(playerPosition.y);
        missileSprite.draw(spriteBatch);
        spriteBatch.end();

        backgroundX -= 1000 * dt;
        if(backgroundX < -background.getWidth()){
            backgroundX += background.getWidth();
        }
        //Draw UI
        uiBatch.begin();
        switch(gameState) {
            //if gameState is Running: Draw Controls
            case PLAYING: {
                moveLeftButton.draw(uiBatch);
                moveRightButton.draw(uiBatch);
                moveDownButton.draw(uiBatch);
                moveUpButton.draw(uiBatch);

            } break;
            //if gameState is Complete: Draw Restart button
            case COMPLETE: {
                restartButton.draw(uiBatch);
            } break;
        }
        uiBatch.end();
    }

    /**Method for all game logic. This method is called at the start of GameCore.render() below. */
    private void update() {
        //Touch Input Info
        boolean checkTouch = Gdx.input.isTouched();
        int touchX = Gdx.input.getX();
        int touchY = Gdx.input.getY();

        //Update Game State based on input
        switch (gameState) {

            case PLAYING:
                //Poll user for input
                moveLeftButton.update(checkTouch, touchX, touchY);
                moveRightButton.update(checkTouch, touchX, touchY);
                moveDownButton.update(checkTouch, touchX, touchY);
                moveUpButton.update(checkTouch, touchX, touchY);

                int moveX = 0;
                int moveY = 0;
                if (Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT) || moveLeftButton.isDown) {
                    moveLeftButton.isDown = true;
                    moveX -= 1;
                }
                if (Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT) || moveRightButton.isDown) {
                    moveRightButton.isDown = true;
                    moveX += 1;
                }
                if (Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN) || moveDownButton.isDown) {
                    moveDownButton.isDown = true;
                    moveY -= 1;
                }
                if (Gdx.input.isKeyPressed(Input.Keys.DPAD_UP) || moveUpButton.isDown) {
                    moveUpButton.isDown = true;
                    moveY += 1;
                }

                //TODO Determine Character Movement Distance
                playerDelta.x = moveX * MOVEMENT_SPEED * dt;
                playerDelta.y = moveY * MOVEMENT_SPEED * dt;

                //TODO Check movement against grid
                if (playerDelta.len2() > 0) { //Don't do anything if we're not moving

                    //TODO Determine bounds to check within
                    // Find top-right corner tile
                    int right = (int) Math.ceil(Math.max(playerSprite.getX() + playerSprite.getWidth(),playerSprite.getX() + playerSprite.getWidth() + playerDelta.x));
                    int top = (int) Math.ceil(Math.max(playerSprite.getY() + playerSprite.getHeight(),playerSprite.getY() + playerSprite.getHeight() + playerDelta.y));

                    // Find bottom-left corner tile
                    int left = (int) Math.floor(Math.min(playerSprite.getX(),playerSprite.getX() + playerDelta.x));
                    int bottom = (int) Math.floor(Math.min(playerSprite.getY(),playerSprite.getY() + playerDelta.y));

                    // Divide bounds by tile sizes to retrieve tile indices
                    //right /= tileLayer.getTileWidth();
                    //top /= tileLayer.getTileHeight();
                    //left /= tileLayer.getTileWidth();
                    //bottom /= tileLayer.getTileHeight();

                    //TODO Loop through selected tiles and correct by each axis
                    //EXTRA: Try counting down if moving left or down instead of counting up
                    for (int y = bottom; y <= top; y++) {
                        for (int x = left; x <= right; x++) {
                            //TiledMapTileLayer.Cell targetCell = tileLayer.getCell(x, y);
                            // If the cell is empty, ignore it
                            //if (targetCell == null) continue;
                            // Otherwise correct against tested squares
                            //tileRectangle.x = x * tileLayer.getTileWidth();
                            //tileRectangle.y = y * tileLayer.getTileHeight();

                            playerDeltaRectangle.x = playerSprite.getX() + playerDelta.x;
                            playerDeltaRectangle.y = playerSprite.getY();
                            //if (tileRectangle.overlaps(playerDeltaRectangle)) playerDelta.x = 0;

                            playerDeltaRectangle.x = playerSprite.getX();
                            playerDeltaRectangle.y = playerSprite.getY() + playerDelta.y;
                            //if (tileRectangle.overlaps(playerDeltaRectangle)) playerDelta.y = 0;
                        }
                    }

                    //TODO Move player and camera
                    playerSprite.translate(playerSprite.getX()+1000, 100);
                    playerSprite.translateX(500);
//                  camera.translate(playerDelta);
                }

                //TODO Check if player has met the winning condition
//                if (playerSprite.getBoundingRectangle().overlaps(goalSprite.getBoundingRectangle())) {
//                    //Player has won!
//                    gameState = GameState.COMPLETE;
//                }

                //TODO Calculate overhead layer opacity
//                if (playerSprite.getBoundingRectangle().overlaps(opacityTrigger)) {
//                    overheadOpacity -= dt * 5.0f;
//                } else {
//                    overheadOpacity += dt * 5.0f;
//                }
//                overheadOpacity = MathUtils.clamp(overheadOpacity, 0.0f, 1.0f);

                break;

            case COMPLETE:
                //Poll for input
                restartButton.update(checkTouch, touchX, touchY);

                if (Gdx.input.isKeyPressed(Input.Keys.DPAD_CENTER) || restartButton.isDown) {
                    restartButton.isDown = true;
                    restartActive = true;
                } else if (restartActive) {
                    newGame();
                }
                break;
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        if (background != null) {
            background.dispose();
        }
        playerTexture.dispose();
        buttonSquareTexture.dispose();
        buttonSquareDownTexture.dispose();
        buttonLongTexture.dispose();
        buttonLongDownTexture.dispose();
        enemyTexture.dispose();
    }

    // Helper method to set up the camera, batch, particle effect, and background image
    private void create() {
        Gdx.app.log("GameScreen: ", "gameScreen create");
        spriteBatch = new SpriteBatch();
        uiBatch = new SpriteBatch();

        //Camera
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float screenRatio = screenWidth / screenHeight; // Calculate the screen ratio
        camera = new OrthographicCamera();
        background = new Texture(Gdx.files.internal("Backgrounds/07/Repeated.png"));
        float viewportWidth = background.getHeight() * screenRatio;
        camera.setToOrtho(false, viewportWidth, background.getHeight());

        //Textures
        playerTexture = new Texture("Plane02/Moving/skeleton-MovingNIdle_0.png");
        enemyTexture = new Texture("Enemy/Moving/skeleton-Moving_0.png");
        buttonSquareTexture = new Texture("buttons/buttonSquare_blue.png");
        buttonSquareDownTexture = new Texture("buttons/buttonSquare_beige_pressed.png");
        buttonLongTexture = new Texture("buttons/buttonLong_blue.png");
        buttonLongDownTexture = new Texture("buttons/buttonLong_beige_pressed.png");
        missileTexture = new Texture("Missile.png");

        //Player
        playerSprite = new Sprite(playerTexture);
        playerSprite.setSize(480, 480);
        playerDelta = new Vector2();
        playerDeltaRectangle = new Rectangle(0, 0, playerSprite.getWidth(), playerSprite.getHeight());
        playerPosition = new Vector2(100, screenHeight / 2 - playerSprite.getHeight() / 2);

        //Enemy
        enemySprite = new Sprite(enemyTexture);
        enemySprite.setSize(400, 400);
        enemyDelta = new Vector2();
        enemyDeltaRectangle = new Rectangle(0, 0, playerSprite.getWidth(), playerSprite.getHeight());
        enemyPosition = new Vector2(viewportWidth - enemySprite.getWidth() * 2, screenHeight / 2 - playerSprite.getHeight() / 2);

        //Missile
        missileSprite = new Sprite(missileTexture);
        missileSprite.setSize(80, 80);
        missileDelta = new Vector2();
        missileDeltaRectangle = new Rectangle(0, 0, missileSprite.getWidth(), missileSprite.getHeight());
        missilePosition = new Vector2(playerPosition.x + playerSprite.getWidth(), 1000);

        //Buttons
        float buttonSize = screenHeight * 0.1f;
        moveLeftButton = new Button(0.0f, buttonSize, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveRightButton = new Button(buttonSize*2, buttonSize, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveDownButton = new Button(buttonSize, 0.0f, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveUpButton = new Button(buttonSize, buttonSize*2, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        restartButton = new Button(screenWidth/2 - buttonSize*2, screenHeight * 0.2f, buttonSize*4, buttonSize, buttonLongTexture, buttonLongDownTexture);

        newGame();
    }

    private void newGame(){
        gameState = GameState.PLAYING;

        dt = 0.0f;

        //Player start location
        playerSprite.setCenter(100,100); //TODO
        camera.translate(playerSprite.getX(), playerSprite.getY());

        restartActive = false;
    }
}
