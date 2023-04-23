package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Random;

public class GameScreen implements Screen {
    private final MyGdxGame game;
    //Screen and Camera
    private OrthographicCamera camera;
    float screenWidth = Gdx.graphics.getWidth();
    float screenHeight = Gdx.graphics.getHeight();
    float screenRatio = screenWidth / screenHeight; // Calculate the screen ratio
    private final float topUIPaddingY = screenHeight * 0.85f;

    //Background
    private Texture backgroundTextureLevel1;
    private Texture backgroundTextureLevel2;
    private Texture backgroundTexture;
    float backgroundX = 0;

    //Game State
    public enum GameState { PLAYING, COMPLETE, PAUSE }
    private GameState gameState = GameState.PLAYING;
    boolean restartActive;

    public static final float MOVEMENT_SPEED = 500.0f;
    private Random random = new Random();

    //Game clock
    float dt;
    private float timer;
    private BitmapFont timerFont;

    //Player Character
    private Texture[] playerMovingTextures;
    private float playerMovingFrame = 0;
    private Texture[] playerDestroyingTextures;
    private float playerDestroyingFrame = 0;
    private Sprite playerSprite;
    private Vector2 playerVector;
    private Vector2 playerPosition;
    boolean isDestroying = false;

    //Enemy Character
    private Texture[] enemyMovingTextures;
    private float enemyMovingFrame = 0;
    private ArrayList<Sprite> enemySprites = new ArrayList<>();
    private ArrayList<Vector2> enemies = new ArrayList<>();
    long lastEnemyCreatedTime = 0;
    ArrayList<Vector2> enemiesToRemove = new ArrayList<>();

    //Player Missile
    private Texture missileTexture;
    private Sprite missileSprite;
    private Vector2 missilePosition;
    private ArrayList<Vector2> missiles = new ArrayList<>();
    ArrayList<Vector2> missilesToRemove = new ArrayList<>();

    //Enemy Missile
    private Texture enemyMissileTexture;
    private Sprite enemyMissileSprite;
    private ArrayList<Vector2> enemyMissiles = new ArrayList<>();
    long lastEnemyShootTime = 0;
    ArrayList<Vector2> enemyMissilesToRemove = new ArrayList<>();

    //Batch
    private  SpriteBatch spriteBatch;
    private SpriteBatch uiBatch; //Second SpriteBatch without camera transforms, for drawing UI

    //UI textures
    private  Texture buttonSquareTexture;
    private Texture buttonSquareDownTexture;
    private Texture buttonRestartTexture;
    private Texture buttonRestartDownTexture;
    private Texture buttonShootTexture;
    private Texture buttonShootDownTexture;
    private Texture buttonPauseTexture;
    private Texture buttonResumeTexture;
    private Texture buttonMuteTexture;
    private Texture buttonUnmuteTexture;

    //UI Buttons
    private Button moveLeftButton;
    private  Button moveRightButton;
    private Button moveDownButton;
    private Button moveUpButton;
    private Button restartButton;
    private  Button shootButton;
    private  Button pauseButton;
    private   Button musicButton;

    //Music
    private Music backgroundMusicLevel1;
    private Music backgroundMusicLevel2;
    private Music backgroundMusic;
    private boolean isMuted = false;

    //Scoring
    private int score = 0;
    private BitmapFont scoreFont;
    private Sprite victorySprite;
    private boolean win = false;
    private int level = 1;

    //Sound Effect
    private  Sound shootSound;
    private  Sound collisionSound;

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

        //Camera and Background
        camera = new OrthographicCamera();
        backgroundTextureLevel1 = new Texture(Gdx.files.internal("Backgrounds/02/Repeated.png"));
        backgroundTextureLevel2 = new Texture(Gdx.files.internal("Backgrounds/03/Repeated.png"));
        backgroundTexture = backgroundTextureLevel1;
        float viewportWidth = backgroundTexture.getHeight() * screenRatio;
        camera.setToOrtho(false, viewportWidth, backgroundTexture.getHeight());

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
        enemyMissileTexture = new Texture("missile_enemy.png");
        buttonShootTexture = new Texture("Buttons/shoot_btn.png");
        buttonShootDownTexture = new Texture("Buttons/shoot_btn_pressed.png");
        buttonPauseTexture = new Texture("Buttons/pause_btn.png");
        buttonResumeTexture = new Texture("Buttons/resume_btn.png");
        buttonMuteTexture = new Texture("Buttons/mute_btn.png");
        buttonUnmuteTexture = new Texture("Buttons/unmute_btn.png");
        Texture victoryTexture = new Texture("victory.png");

        //Player
        playerSprite = new Sprite(playerMovingTextures[0]);
        playerSprite.setSize(320, 320);
        playerVector = new Vector2();
        playerPosition = new Vector2(100, screenHeight / 2 - playerSprite.getHeight() / 2); //set initial position

        //Missile
        missileSprite = new Sprite(missileTexture);
        missileSprite.setSize(160, 80);
        missilePosition = new Vector2(playerPosition.x + playerSprite.getWidth(), 1000);

        //Missile
        enemyMissileSprite = new Sprite(enemyMissileTexture);
        enemyMissileSprite.setSize(160, 80);
        //enemyMissilePosition = new Vector2(enemyMissilePosition.x + enemyMissileSprite.getWidth(), 1000);

        //Buttons
        float buttonSize = screenHeight * 0.1f;
        float space = 50.0f;
        moveLeftButton = new Button(space, buttonSize+space, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveRightButton = new Button(buttonSize*2+space, buttonSize+space, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveDownButton = new Button(buttonSize+space, space, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveUpButton = new Button(buttonSize+space, buttonSize*2+space, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        shootButton = new Button(screenWidth - 500, 200, buttonSize*2, buttonSize*2, buttonShootTexture, buttonShootDownTexture);
        restartButton = new Button(screenWidth/2 - buttonSize*2, screenHeight/2 - buttonSize, buttonSize*4, buttonSize*2+50, buttonRestartTexture, buttonRestartDownTexture);
        pauseButton = new Button(screenWidth - buttonSize*2, topUIPaddingY, buttonSize, buttonSize, buttonPauseTexture, buttonPauseTexture);
        musicButton = new Button(screenWidth - buttonSize*4, topUIPaddingY, buttonSize, buttonSize, buttonUnmuteTexture, buttonMuteTexture);

        //Background Music
        backgroundMusicLevel2 = Gdx.audio.newMusic(Gdx.files.internal("Music/neon-gaming-128925.mp3"));
        backgroundMusicLevel1 = Gdx.audio.newMusic(Gdx.files.internal("Music/pixelated-adventure-122039.mp3"));
        backgroundMusicLevel1.setLooping(true);
        backgroundMusicLevel2.setLooping(true);
        backgroundMusic = backgroundMusicLevel1;
        backgroundMusic.play();

        //Shooting sound effect
        shootSound = Gdx.audio.newSound(Gdx.files.internal("Sound/shoot.mp3"));
        collisionSound = Gdx.audio.newSound(Gdx.files.internal("Sound/explosion.mp3"));

        //Create Score Font
        scoreFont = new BitmapFont();
        scoreFont.setColor(Color.WHITE);
        scoreFont.getData().setScale(6f);

        //Create Timer Font
        timerFont = new BitmapFont();
        timerFont.setColor(Color.WHITE);
        timerFont.getData().setScale(6f);

        //Victory Sprite
        victorySprite = new Sprite(victoryTexture);
        victorySprite.setSize(screenWidth*0.7f, victorySprite.getWidth() * victorySprite.getHeight() / victorySprite.getWidth());

        newGame();
    }

    @Override
    public void render(float delta) {
        dt = Gdx.graphics.getDeltaTime();
        //Clear the screen every frame before drawing.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA); //Allows transparent sprites/tiles
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update();
        checkLevel();

        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        backgroundTexture = level == 1 ? backgroundTextureLevel1 : backgroundTextureLevel2;  //set background based on level
        spriteBatch.draw(backgroundTexture, backgroundX, 0);  //first background
        spriteBatch.draw(backgroundTexture, backgroundX+ backgroundTexture.getWidth()-10, 0); //second background

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

        //Draw Enemy Missiles
        for (int i=0; i < this.enemyMissiles.size(); i++) {
            enemyMissileSprite.setX(enemyMissiles.get(i).x);
            enemyMissileSprite.setY(enemyMissiles.get(i).y);
            enemyMissileSprite.draw(spriteBatch);
        }

        //Player Collision Animation
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

        //Show score and Timer
        scoreFont.draw(spriteBatch, "Score " + score, screenWidth*0.05f, topUIPaddingY+20.0f);
        int minutes = (int)timer / 60;
        int seconds = (int)timer % 60;
        String timeStr = String.format("%02d:%02d", minutes, seconds);
        GlyphLayout layout = new GlyphLayout();
        layout.setText(timerFont, timeStr);
        float textWidth = layout.width;
        timerFont.draw(spriteBatch, timeStr, (screenWidth-textWidth)/2, topUIPaddingY+20.0f);

        //Show Victory
        if(win){
            victorySprite.setX((screenWidth-victorySprite.getWidth())/2);
            victorySprite.setY((screenHeight-victorySprite.getHeight())/2);
            victorySprite.draw(spriteBatch);
            restartButton.y = screenHeight/2 - 400;
        }

        spriteBatch.end();

        //Draw UI
        uiBatch.begin();
        moveLeftButton.draw(uiBatch);
        moveRightButton.draw(uiBatch);
        moveDownButton.draw(uiBatch);
        moveUpButton.draw(uiBatch);
        shootButton.draw(uiBatch);
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
        shootButton.update(checkTouch, touchX, touchY);
        pauseButton.update(checkTouch, touchX, touchY);
        musicButton.update(checkTouch, touchX, touchY);

        pauseControl();
        musicControl();

        switch (gameState) {
            case PLAYING: {
                timer += dt;
                scrollBackground();
                playerMovementControl();
                playerShootControl();
                enemies();
                enemyMissiles();
                playerMissiles();
                detectCollisions();
                removeObjects();
            }
            break;

            case COMPLETE: {} case PAUSE: {
                //Poll for input
                restartButton.update(checkTouch, touchX, touchY);
                if (restartButton.isDown) {
                    restartActive = true;
                } else if (restartActive) {
                    newGame();
                }
            }
            break;
        }
    }

    private void checkLevel() {
        if(score > 50 || timer > 120){
            win = true;
            gameState = GameState.COMPLETE;
        }
        if((int)timer == 20){
            level = 2;
            backgroundMusic.stop();  //stop current music
            backgroundMusic = backgroundMusicLevel2; //change to level 2 music
            backgroundMusic.play();
        }
    }

    private void musicControl(){
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
        //Update mute
        if(isMuted){
            backgroundMusic.pause();
        }
    }

    private void pauseControl(){
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
        if (gameState == GameState.PLAYING){
            pauseButton.setTexture(buttonPauseTexture);
        }
    }

    private void scrollBackground(){
        //Move Background
        backgroundX -= 800 * dt;
        //Reposition the background when it goes out of scope
        if(backgroundX < -backgroundTexture.getWidth()){
            backgroundX += backgroundTexture.getWidth();
        }
    }

    private void playerMovementControl(){
        //Movement Button
        int moveX = 0;
        int moveY = 0;
        if (moveLeftButton.isDown) {
            moveX -= 1;
        }
        if (moveRightButton.isDown) {
            moveX += 1;
        }
        if (moveDownButton.isDown) {
            moveY -= 1;
        }
        if (moveUpButton.isDown) {
            moveY += 1;
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
    }

    private void playerShootControl(){
        //Shoot Button
        if (shootButton.justPressed()) {
            shootButton.isDown = true;
            shootButton.isDownPrev = true;
            this.missiles.add(new Vector2(playerPosition.x + playerSprite.getWidth(), playerPosition.y));
            shootSound.play();
        }
    }

    private void enemies(){
        //Generate Enemies
        int randomNum = random.nextInt(200);
        int randomY = random.nextInt((int)camera.viewportHeight-150);
        int frequency = level == 1 ? 2000 : 800;
        if (System.currentTimeMillis() > lastEnemyCreatedTime + frequency) {
            lastEnemyCreatedTime = System.currentTimeMillis();
            Vector2 newEnemy = new Vector2(camera.viewportWidth + randomNum, randomY);
            enemies.add(newEnemy);
            Sprite newEnemySprite = new Sprite(enemyMovingTextures[0]);
            newEnemySprite.setSize(320,320);
            newEnemySprite.setPosition(newEnemy.x, newEnemy.y);
            enemySprites.add(newEnemySprite);
        }
        //Move Enemies
        for(Vector2 enemy : enemies){
            float xPos = enemy.x;
            float speed = level == 1 ? 500 : 700;
            enemy.add(new Vector2(-speed*dt, 0));
            //Increase speed toward left screen
            if(xPos < camera.viewportWidth/2){
                enemy.add(new Vector2(-(speed+100)*dt, 0));
            }else{
                enemy.add(new Vector2(-speed*dt, 0));
            }
            if (enemy.x < -400) { //Remove the enemy when it's out of camera
                enemiesToRemove.add(enemy);
            }
        }
    }

    private void enemyMissiles(){
        //Generate Enemy Missiles
        if (System.currentTimeMillis() > lastEnemyShootTime + 1500 && (int)timer >= 3 && !enemies.isEmpty()) {
            lastEnemyShootTime = System.currentTimeMillis();
            int randomEnemyIndex = random.nextInt(enemies.size());
            Vector2 newEnemyMissile = new Vector2(enemies.get(randomEnemyIndex).x - enemySprites.get(randomEnemyIndex).getWidth()/2, enemies.get(randomEnemyIndex).y);
            enemyMissiles.add(newEnemyMissile);
        }
        //Move Enemy Missiles
        for (Vector2 enemyMissile : enemyMissiles){
            float speed = level == 1 ? 1500 : 1700;
            enemyMissile.add(-speed*dt, 0);
            if (enemyMissile.x > camera.viewportWidth - 200) { //Remove the missiles when it's out of camera
                enemyMissilesToRemove.add(enemyMissile);
            }
        }
    }

    private void playerMissiles(){
        //Move Missiles
        for (Vector2 missile : missiles) {
            //Move Missile
            missile.add(500 * dt, 0);
            if (missile.x > camera.viewportWidth + 200) { //Remove the missiles when it's out of camera
                missilesToRemove.add(missile);
            }
        }
    }

    private void detectCollisions(){
        //Detect Player and Enemies Collisions
        for(Vector2 enemy : enemies){
            if (enemy.dst(playerPosition) < 200){  //Collide
                enemiesToRemove.add(enemy);
                isDestroying = true;
                gameState = GameState.COMPLETE;
                collisionSound.play();
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

        //Detect Player and enemy Missiles Collisions
        for(Vector2 enemyMissile : enemyMissiles){
            if (enemyMissile.dst(playerPosition) < 200){  //Collide
                enemyMissilesToRemove.add(enemyMissile);
                isDestroying = true;
                gameState = GameState.COMPLETE;
                collisionSound.play();
            }
        }
    }

    private void removeObjects(){
        //Remove
        for (Vector2 missile : missilesToRemove) {
            missiles.remove(missile);
        }
        for (Vector2 enemy : enemiesToRemove) {
            enemies.remove(enemy);
        }
        for (Vector2 enemyMissile : enemyMissilesToRemove) {
            enemyMissiles.remove(enemyMissile);
        }
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        if (backgroundTextureLevel1 != null) {
            backgroundTextureLevel1.dispose();
        }
        if (backgroundTextureLevel2 != null) {
            backgroundTextureLevel2.dispose();
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
        buttonShootTexture.dispose();
        buttonShootDownTexture.dispose();
        missileTexture.dispose();
        buttonPauseTexture.dispose();
        buttonResumeTexture.dispose();
        buttonMuteTexture.dispose();
        buttonUnmuteTexture.dispose();
        scoreFont.dispose();
        spriteBatch.dispose();
        uiBatch.dispose();
        backgroundMusicLevel1.dispose();
        backgroundMusicLevel2.dispose();
        shootSound.dispose();
        collisionSound.dispose();
    }

    private void newGame(){
        dt = 0.0f;
        playerSprite.setCenter(playerSprite.getWidth()/2,playerSprite.getWidth()/2);
        playerPosition = new Vector2(100, screenHeight / 2 - playerSprite.getHeight() / 2); //set initial position
        enemies.clear();
        missiles.clear();
        score = 0;
        timer = 0;
        win = false;
        backgroundMusic.stop(); //stop current music
        backgroundMusic = backgroundMusicLevel1; //set to level 1 music
        backgroundMusic.play();
        level = 1;
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
