package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Random;

public class GameScreen implements Screen {
    private final MyGdxGame game;
    private OrthographicCamera camera;
    private Texture background;
    float backgroundX = 0;
    float screenWidth = Gdx.graphics.getWidth();
    float screenHeight = Gdx.graphics.getHeight();
    float screenRatio = screenWidth / screenHeight; // Calculate the screen ratio
    private float topUIPaddingY = screenHeight * 0.85f;

    public enum GameState { PLAYING, COMPLETE, PAUSE }
    GameState gameState = GameState.PLAYING;
    boolean restartActive;

    public static final float MOVEMENT_SPEED = 500.0f;
    float dt; //Game clock
    private float timer;
    private BitmapFont timerFont;

    //Player Character
    private Texture[] playerMovingTextures;
    private float playerMovingFrame = 0;
    private Texture[] playerDestroyingTextures;
    private float playerDestroyingFrame = 0;
    Sprite playerSprite;
    Vector2 playerVector;
    Vector2 playerPosition;
    boolean isDestroying = false;

    //Enemy Character
    private Texture[] enemyMovingTextures;
    private float enemyMovingFrame = 0;
    ArrayList<Sprite> enemySprites = new ArrayList<Sprite>();
    ArrayList<Vector2> enemies = new ArrayList<Vector2>();
    long lastEnemyCreatedTime = 0;

    //Missile
    Texture missileTexture;
    Sprite missileSprite;
    Vector2 missilePosition;
    ArrayList<Vector2> missiles = new ArrayList<Vector2>();

    //Batch
    SpriteBatch spriteBatch;
    SpriteBatch uiBatch; //Second SpriteBatch without camera transforms, for drawing UI

    //UI textures
    Texture buttonSquareTexture;
    Texture buttonSquareDownTexture;
    Texture buttonRestartTexture;
    Texture buttonRestartDownTexture;
    Texture buttonAttackTexture;
    Texture buttonAttackDownTexture;
    Texture buttonPauseTexture;
    Texture buttonResumeTexture;
    Texture buttonMuteTexture;
    Texture buttonUnmuteTexture;

    //UI Buttons
    Button moveLeftButton;
    Button moveRightButton;
    Button moveDownButton;
    Button moveUpButton;
    Button restartButton;
    Button attackButton;
    Button pauseButton;
    Button musicButton;

    //Misic
    private Music backgroundMusic;
    private boolean isMuted = false;

    //Scoring
    private int score = 0;
    private BitmapFont scoreFont;

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
        camera = new OrthographicCamera();
        background = new Texture(Gdx.files.internal("Backgrounds/07/Repeated.png"));
        float viewportWidth = background.getHeight() * screenRatio;
        camera.setToOrtho(false, viewportWidth, background.getHeight());

        //Textures
        playerMovingTextures = new Texture[14];
        for(int i = 0; i < 14; i++){
            playerMovingTextures[i] = new Texture((Gdx.files.internal("Player/Moving/skeleton-MovingNIdle_" + i + ".png")));
        }
        playerDestroyingTextures = new Texture[22];
        for(int i = 0; i < 22; i++){
            playerDestroyingTextures[i] = new Texture((Gdx.files.internal("Player/Destroyed/skeleton-Destroy_" + i + ".png")));
        }
        enemyMovingTextures = new Texture[18];
        for(int i = 0; i < 18; i++){
            enemyMovingTextures[i] = new Texture((Gdx.files.internal("Enemy/Moving/skeleton-Moving_" + i + ".png")));
        }
        buttonSquareTexture = new Texture("Buttons/buttonSquare_blue.png");
        buttonSquareDownTexture = new Texture("Buttons/buttonSquare_beige_pressed.png");
        buttonRestartTexture = new Texture("Buttons/play_purple.png");
        buttonRestartDownTexture = new Texture("Buttons/play_pressed_purple.png");
        missileTexture = new Texture("missile.png");
        buttonAttackTexture = new Texture("Buttons/shoot_btn.png");
        buttonAttackDownTexture = new Texture("Buttons/shoot_btn_pressed.png");
        buttonPauseTexture = new Texture("Buttons/pause_btn.png");
        buttonResumeTexture = new Texture("Buttons/resume_btn.png");
        buttonMuteTexture = new Texture("Buttons/mute_btn.png");
        buttonUnmuteTexture = new Texture("Buttons/unmute_btn.png");

        //Player
        playerSprite = new Sprite(playerMovingTextures[0]);
        playerSprite.setSize(320, 320);
        playerVector = new Vector2();
        playerPosition = new Vector2(100, screenHeight / 2 - playerSprite.getHeight() / 2); //set initial position

        //Missile
        missileSprite = new Sprite(missileTexture);
        missileSprite.setSize(160, 80);
        missilePosition = new Vector2(playerPosition.x + playerSprite.getWidth(), 1000);

        //Buttons
        float buttonSize = screenHeight * 0.1f;
        float space = 50.0f;
        moveLeftButton = new Button(space, buttonSize+space, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveRightButton = new Button(buttonSize*2+space, buttonSize+space, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveDownButton = new Button(buttonSize+space, space, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveUpButton = new Button(buttonSize+space, buttonSize*2+space, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        attackButton = new Button(screenWidth - 500, 200, buttonSize*2, buttonSize*2, buttonAttackTexture, buttonAttackDownTexture);
        restartButton = new Button(screenWidth/2 - buttonSize*2, screenHeight/2 - buttonSize, buttonSize*4, buttonSize*2, buttonRestartTexture, buttonRestartDownTexture);
        pauseButton = new Button(screenWidth - buttonSize*2, topUIPaddingY, buttonSize, buttonSize, buttonPauseTexture, buttonPauseTexture);
        musicButton = new Button(screenWidth - buttonSize*4, topUIPaddingY, buttonSize, buttonSize, buttonUnmuteTexture, buttonMuteTexture);

        //Background Music
//        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Music/neon-gaming-128925.mp3"));
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Music/pixelated-adventure-122039.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        //Create Score Font
        scoreFont = new BitmapFont();
        scoreFont.setColor(Color.WHITE);
        scoreFont.getData().setScale(6f);

        //Create Timer Font
        timerFont = new BitmapFont();
        timerFont.setColor(Color.WHITE);
        timerFont.getData().setScale(6f);

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

        //Player Animation
        playerMovingFrame += 10 * dt;
        if (playerMovingFrame >= playerMovingTextures.length){
            playerMovingFrame = 0; //reset
        }
        if(!isDestroying && gameState != GameState.COMPLETE){
            playerSprite.setTexture(playerMovingTextures[(int) playerMovingFrame]);
            playerSprite.setX(playerPosition.x);
            playerSprite.setY(playerPosition.y);
            playerSprite.draw(spriteBatch);
        }

        //Enemy Animation
        for (int i=0; i < enemies.size(); i++) {
            enemyMovingFrame += 10 * dt;
            if (enemyMovingFrame >= enemyMovingTextures.length) {
                enemyMovingFrame = 0; //reset
            }
            if (!enemies.isEmpty()) {
                enemySprites.get(i).setTexture(enemyMovingTextures[(int) enemyMovingFrame]);
                enemySprites.get(i).setX(enemies.get(i).x);
                enemySprites.get(i).setY(enemies.get(i).y);
                enemySprites.get(i).draw(spriteBatch);
            }
        }

        //Draw Missiles
        for (int i=0; i < this.missiles.size(); i++) {
            missileSprite.setX(missiles.get(i).x);
            missileSprite.setY(missiles.get(i).y);
            missileSprite.draw(spriteBatch);
        }

        //Player and Enemy Collision
        if (isDestroying){
            playerDestroyingFrame += 10 * dt;
            if (playerDestroyingFrame >= playerDestroyingTextures.length){
                playerDestroyingFrame = 0; //reset
                isDestroying = false;
            }
            playerSprite.setTexture(playerDestroyingTextures[(int) playerDestroyingFrame]);
            playerSprite.setX(playerPosition.x);
            playerSprite.setY(playerPosition.y);
            playerSprite.draw(spriteBatch);
        }

        //Show score
        scoreFont.draw(spriteBatch, "Score " + score, screenWidth*0.05f, topUIPaddingY+20.0f);
        int minutes = (int)timer / 60;
        int seconds = (int)timer % 60;
        String timeStr = String.format("%02d:%02d", minutes, seconds);
        timerFont.draw(spriteBatch, timeStr, screenWidth*0.5f-20.0f, topUIPaddingY+20.0f);
        spriteBatch.end();

        //Draw UI
        uiBatch.begin();
        moveLeftButton.draw(uiBatch);
        moveRightButton.draw(uiBatch);
        moveDownButton.draw(uiBatch);
        moveUpButton.draw(uiBatch);
        attackButton.draw(uiBatch);
        pauseButton.draw(uiBatch);
        musicButton.draw(uiBatch);
        uiBatch.end();
        //Complete
        if (gameState == GameState.COMPLETE || gameState == GameState.PAUSE) {
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
        musicButton.update(checkTouch, touchX, touchY);

        //Pause and Resume Button
        if (pauseButton.justPressed()) {
            if (gameState == GameState.PAUSE) { //Resume
                gameState = GameState.PLAYING;
                pauseButton.setTexture(buttonPauseTexture);
            } else if (gameState == GameState.PLAYING){ //Pause
                gameState = GameState.PAUSE;
                pauseButton.setTexture(buttonResumeTexture);
            }
        }

        //Mute and Unmute Button
        if (musicButton.justPressed()) {
            if (isMuted) { //is not playing music, pressed to unmute
                isMuted = false;
                musicButton.setTexture(buttonUnmuteTexture);
                backgroundMusic.play();
            }
            else { //is playing music, pressed to mute
                isMuted = true;
                musicButton.setTexture(buttonMuteTexture);
                backgroundMusic.pause();
            }
        }

        //Update Game State based on input
        switch (gameState) {
            case PLAYING: {
                timer += dt;

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
                int randomNum = random.nextInt(200);
                int randomY = random.nextInt((int)camera.viewportHeight-150);
                if (System.currentTimeMillis() > lastEnemyCreatedTime + 2000) {
                    lastEnemyCreatedTime = System.currentTimeMillis();
                    Vector2 newEnemy = new Vector2(camera.viewportWidth + randomNum, randomY);
                    enemies.add(newEnemy);
                    Sprite newEnemySprite = new Sprite(enemyMovingTextures[0]);
                    newEnemySprite.setSize(320,320);
                    newEnemySprite.setPosition(newEnemy.x, newEnemy.y);
                    enemySprites.add(newEnemySprite);
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
                for(Vector2 enemy : enemies){
                    if (enemy.dst(playerPosition) < 200){  //Collide
                        Gdx.app.log("Player and Enemy ", "Collision");
                        enemiesToRemove.add(enemy);
                        isDestroying = true;
                        gameState = GameState.COMPLETE;
                    }
                }

                //Detect Enemies and Missiles Collisions
                for(Vector2 missile : missiles){
                    for(Vector2 enemy : enemies){
                        if (missile.dst(new Vector2(enemy.x + (320 / 2), enemy.y + (320 / 2))) < 200) {
                            //200 is the enemy's size
                            enemiesToRemove.add(enemy);
                            missilesToRemove.add(missile);
                            score += 1;
                        }
                    }
                }
                Gdx.app.log("Player score ", String.valueOf(score));


                //Remove
                for (Vector2 missile : missilesToRemove) {
                    missiles.remove(missile);
                }
                for (Vector2 enemy : enemiesToRemove) {
                    enemies.remove(enemy);
                }
            }
            break;
            case COMPLETE: {} case PAUSE: {
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
        for (Texture playerMovingTexture : playerMovingTextures){
            playerMovingTexture.dispose();
        }
        for (Texture playerDestroyingTexture : playerDestroyingTextures){
            playerDestroyingTexture.dispose();
        }
        for (Texture enemyMovingTexture : enemyMovingTextures){
            enemyMovingTexture.dispose();
        }
        buttonSquareTexture.dispose();
        buttonSquareDownTexture.dispose();
        buttonRestartTexture.dispose();
        buttonRestartDownTexture.dispose();
        buttonAttackTexture.dispose();
        buttonAttackDownTexture.dispose();
        missileTexture.dispose();
        buttonPauseTexture.dispose();
        buttonResumeTexture.dispose();
        buttonMuteTexture.dispose();
        buttonUnmuteTexture.dispose();
        scoreFont.dispose();
        spriteBatch.dispose();
        uiBatch.dispose();
    }

    private void newGame(){
        Gdx.app.log("new","game");
        dt = 0.0f;
        playerSprite.setCenter(playerSprite.getWidth()/2,playerSprite.getWidth()/2);
        playerPosition = new Vector2(100, screenHeight / 2 - playerSprite.getHeight() / 2); //set initial position
        enemies.clear();
        missiles.clear();
        score = 0;
        timer = 0;
        gameState = GameState.PLAYING;
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
