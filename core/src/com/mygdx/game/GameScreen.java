package com.mygdx.game;
import static java.lang.String.valueOf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class GameScreen implements Screen {
    private OrthographicCamera camera;
    private Texture background;
    private MyGdxGame game;
    public enum GameState { PLAYING, COMPLETE, PAUSE }
    GameState gameState = GameState.PLAYING;
    boolean restartActive;

    public static final float MOVEMENT_SPEED = 400.0f;
    float dt; //Game clock

    //Player Character
    Texture playerTexture;
    Sprite playerSprite;
    Vector2 playerVector;
    Vector2 playerPosition;


    //Enemy Character
    Texture enemyTexture;
    Sprite enemySprite;
    Vector2 enemyPosition;

    //Missile
    Texture missileTexture;
    Sprite missileSprite;
    Vector2 missilePosition;

    SpriteBatch spriteBatch;
    SpriteBatch uiBatch; //Second SpriteBatch without camera transforms, for drawing UI

    //UI textures
    Texture buttonSquareTexture;
    Texture buttonSquareDownTexture;
    Texture buttonLongTexture;
    Texture buttonLongDownTexture;
    Texture buttonAttackTexture;
    Texture buttonAttackDownTexture;
    Texture buttonPauseTexture;
    Texture buttonPlayTexture;

    //UI Buttons
    Button moveLeftButton;
    Button moveRightButton;
    Button moveDownButton;
    Button moveUpButton;
    Button restartButton;
    Button attackButton;
    Button pauseButton;
    boolean shootButtonWasPressed = false;

    //A list of Missiles
    ArrayList<Vector2> missiles = new ArrayList<Vector2>();
    //A list of Enemies
    ArrayList<Vector2> enemies = new ArrayList<Vector2>();

    long lastEnemyCreatedTime = 0;

    float backgroundX = 0;

    private Music backgroundMusic;

    // constructor to keep a reference to the main Game class
    public GameScreen(MyGdxGame game) {
        this.game = game;
    }
    @Override
    public void show() {
        create();
    }

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
        missileTexture = new Texture("11.png");
        buttonAttackTexture = new Texture("buttons/shoot_btn.png");
        buttonAttackDownTexture = new Texture("buttons/shoot_btn_pressed.png");
        buttonPauseTexture = new Texture("buttons/pause-Btn.png");
        buttonPlayTexture = new Texture("buttons/play-Btn.png");

        //Player
        playerSprite = new Sprite(playerTexture);
        playerSprite.setSize(320, 320);
        playerVector = new Vector2();
        playerPosition = new Vector2(100, screenHeight / 2 - playerSprite.getHeight() / 2);

        //Enemy
        enemySprite = new Sprite(enemyTexture);
        enemySprite.setSize(360, 360);
        enemySprite.setCenter(enemySprite.getWidth()/2,enemySprite.getWidth()/2);
        enemyPosition = new Vector2(viewportWidth - enemySprite.getWidth() * 2, screenHeight / 2 - playerSprite.getHeight() / 2);

        //Missile
        missileSprite = new Sprite(missileTexture);
        missileSprite.setSize(160, 80);
        missilePosition = new Vector2(playerPosition.x + playerSprite.getWidth(), 1000);

        //Buttons
        float buttonSize = screenHeight * 0.1f;
        moveLeftButton = new Button(0.0f, buttonSize, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveRightButton = new Button(buttonSize*2, buttonSize, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveDownButton = new Button(buttonSize, 0.0f, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveUpButton = new Button(buttonSize, buttonSize*2, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        attackButton = new Button(screenWidth - 500, 200, buttonSize*2, buttonSize*2, buttonAttackTexture, buttonAttackDownTexture);
        pauseButton = new Button(screenWidth - buttonSize*2, screenHeight - buttonSize*2, buttonSize, buttonSize, buttonPauseTexture, buttonPauseTexture);
        restartButton = new Button(screenWidth/2 - buttonSize*2, screenHeight * 0.2f, buttonSize*4, buttonSize, buttonLongTexture, buttonLongDownTexture);

        //Background Music
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Music/neon-gaming-128925.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        newGame();
    }

    @Override
    public void render(float delta) {
        dt = Gdx.graphics.getDeltaTime();
        update();

        //Clear the screen every frame before drawing.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA); //Allows transparent sprites/tiles
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        spriteBatch.draw(background, backgroundX, 0);  //first background
        spriteBatch.draw(background, backgroundX+background.getWidth(), 0); //second background

        playerSprite.setX(playerPosition.x);
        playerSprite.setY(playerPosition.y);
        playerSprite.draw(spriteBatch);

        for (int i=0; i < enemies.size(); i++) {
            enemySprite.setX(enemies.get(i).x);
            enemySprite.setY(enemies.get(i).y);
            enemySprite.draw(spriteBatch);
        }

        for (int i=0; i < this.missiles.size(); i++) {
            missileSprite.setX(missiles.get(i).x);
            missileSprite.setY(missiles.get(i).y);
            missileSprite.draw(spriteBatch);
        }
        spriteBatch.end();

        //Draw UI
        uiBatch.begin();
        moveLeftButton.draw(uiBatch);
        moveRightButton.draw(uiBatch);
        moveDownButton.draw(uiBatch);
        moveUpButton.draw(uiBatch);
        attackButton.draw(uiBatch);
        pauseButton.draw(uiBatch);
        uiBatch.end();
        //Complete
        if (gameState == GameState.COMPLETE) {
            uiBatch.begin();
            restartButton.draw(uiBatch);
            uiBatch.end();
        }
    }

    /**Method for all game logic. This method is called at the start of GameCore.render() below. */
    private void update() {
        //Touch Input Info
        boolean checkTouch = Gdx.input.isTouched();
        int touchX = Gdx.input.getX();
        int touchY = Gdx.input.getY();
        //Poll user for input
        moveLeftButton.update(checkTouch, touchX, touchY);
        moveRightButton.update(checkTouch, touchX, touchY);
        moveDownButton.update(checkTouch, touchX, touchY);
        moveUpButton.update(checkTouch, touchX, touchY);
        attackButton.update(checkTouch, touchX, touchY);
        pauseButton.update(checkTouch, touchX, touchY);

        //Pause and Resume Button
        if (pauseButton.justPressed()) {
            if (gameState == GameState.PAUSE) {
                gameState = GameState.PLAYING;
                pauseButton.setTexture(buttonPauseTexture);
                Gdx.app.log("Pause Button is Pressed to play", String.valueOf(pauseButton.isDown));
            }
            else {
                gameState = GameState.PAUSE;
                pauseButton.setTexture(buttonPlayTexture);
                Gdx.app.log("Pause Button is Pressed to pause", String.valueOf(pauseButton.isDown));
            }
        }

        //Update Game State based on input
        switch (gameState) {
            case PLAYING: {
                int moveX = 0;
                int moveY = 0;
                if (moveLeftButton.isDown) {
                    moveLeftButton.isDown = true;
                    moveX -= 1;
                }
                if (moveRightButton.isDown) {
                    moveRightButton.isDown = true;
                    moveX += 1;
                }
                if (moveDownButton.isDown) {
                    moveDownButton.isDown = true;
                    moveY -= 1;
                }
                if (moveUpButton.isDown) {
                    moveUpButton.isDown = true;
                    moveY += 1;
                }
                if (attackButton.justPressed()) {
                    attackButton.isDown = true;
                    Gdx.app.log("Attack Button is Pressed", String.valueOf(attackButton.isDown));
                    attackButton.isDownPrev = true;
                    this.missiles.add(new Vector2(playerPosition.x + playerSprite.getWidth(), playerPosition.y));
                }

                //Move Background
                backgroundX -= 800 * dt;
                //Reposition the background when it goes out of scope
                if(backgroundX < -background.getWidth()){
                    backgroundX += background.getWidth();
                }

                //Determine Character Movement Distance
                playerVector.x = moveX * MOVEMENT_SPEED * dt;
                playerVector.y = moveY * MOVEMENT_SPEED * dt;
                //Check movement against grid
                if (playerVector.len2() > 0) { //Don't do anything if we're not moving
                    //Move player
                    playerPosition.x += playerVector.x;
                    playerPosition.y += playerVector.y;
                    playerSprite.translate(playerVector.x, playerVector.y);
                }

                //Generate Enemies every second
                Random random = new Random();
                int randomNum = random.nextInt(10);
                if (System.currentTimeMillis() > lastEnemyCreatedTime + 2000) {
                    lastEnemyCreatedTime = System.currentTimeMillis();
                    enemies.add(new Vector2(camera.viewportWidth + enemySprite.getWidth() * 2 * randomNum, camera.viewportHeight/randomNum));
                }

                ArrayList<Vector2> enemiesToRemove = new ArrayList<Vector2>();
                for(int i = 0; i < enemies.size(); i++){
                    float xPos = enemies.get(i).x;
                    //float difficulty = enemies.size()/2;
                    float difficulty = 1;
                    if(xPos < camera.viewportWidth/2){
                        enemies.get(i).add(new Vector2(-600*dt*difficulty, 0));
                    }else{
                        enemies.get(i).add(new Vector2(-500*dt*difficulty, 0));
                    }
                    if (enemies.get(i).x < -400) { //Remove the enemy when it's out of camera
                        enemiesToRemove.add(enemies.get(i));
                    }
                }
                ArrayList<Vector2> missilesToRemove = new ArrayList<Vector2>();
                for (int i = 0; i < missiles.size(); i++) {
                    //Move Missile
                    missiles.get(i).add(500 * dt, 0);
                    if (missiles.get(i).x > camera.viewportWidth + 200) { //Remove the missiles when it's out of camera
                        missilesToRemove.add(missiles.get(i));
                    }
                }

                //Detect Player and Enemies Collisions
//                for(Vector2 enemy : enemies){
//                    Gdx.app.log("Player Vector ", String.valueOf(playerPosition));
//                    if (enemy.dst(playerPosition) < 500){
//                        Gdx.app.log("Player and Enemy ", "Collision");
//                        enemiesToRemove.add(enemy);
//                        gameState = GameState.COMPLETE;
//                    }
//                }
                //Detect Enemies and Missiles Collisions
                for(Vector2 missile : missiles){
                    for(Vector2 enemy : enemies){
                        if (missile.dst(new Vector2(enemy.x + (enemyTexture.getWidth() / 2), enemy.y + (enemyTexture.getHeight() / 2))) < 200) {
                            //200 is the enemy's size
                            enemiesToRemove.add(enemy);
                            missilesToRemove.add(missile);
                        }
                    }
                }

                //Remove
                for (Vector2 missile : missilesToRemove) {
                    missiles.remove(missile);
                }
                for (Vector2 enemy : enemiesToRemove) {
                    enemies.remove(enemy);
                }
            }
            break;
            case COMPLETE: {
                //Poll for input
                restartButton.update(checkTouch, touchX, touchY);

                if (restartButton.isDown) {
                    restartButton.isDown = true;
                    restartActive = true;
                } else if (restartActive) {
                    newGame();
                }
            }
                break;
        }
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
        missileTexture.dispose();
        buttonPlayTexture.dispose();
    }

    private void newGame(){
        gameState = GameState.PLAYING;
        dt = 0.0f;

        playerSprite.setCenter(playerSprite.getWidth()/2,playerSprite.getWidth()/2);
        restartActive = false;
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
    public void hide() {
        Gdx.app.log("GameScreen: ", "gameScreen hide called");
    }
}
