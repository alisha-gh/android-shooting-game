package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen implements Screen {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture background;
    private MyGdxGame game;
    public enum GameState { PLAYING, COMPLETE }

    public static final float MOVEMENT_SPEED = 200.0f;
    public static final float GOAL_BOB_HEIGHT = 5.0f;
    //Map and rendering
    SpriteBatch spriteBatch;
    SpriteBatch uiBatch; //Second SpriteBatch without camera transforms, for drawing UI
    GameState gameState = GameState.PLAYING;
    BitmapFont bmfont;

    //UI textures
    Texture buttonSquareTexture;
    Texture buttonSquareDownTexture;
    Texture buttonLongTexture;
    Texture buttonLongDownTexture;

    //UI Buttons
    Button moveLeftButton;
    Button moveRightButton;
    Button moveDownButton;
    Button moveUpButton;
    Button restartButton;

    // constructor to keep a reference to the main Game class
    public GameScreen(MyGdxGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        Gdx.app.log("GameScreen: ", "gameScreen show called");
        create();
    }

    @Override
    public void hide() {
        Gdx.app.log("GameScreen: ", "gameScreen hide called");
    }

    @Override
    public void render(float delta) {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

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
        batch.dispose();
        if (background != null) {
            background.dispose();
        }
        //playerTexture.dispose();
        buttonSquareTexture.dispose();
        buttonSquareDownTexture.dispose();
        buttonLongTexture.dispose();
        buttonLongDownTexture.dispose();
    }

    // Helper method to set up the camera, batch, particle effect, and background image
    private void create() {
        Gdx.app.log("GameScreen: ", "gameScreen create");
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        float screenRatio = w / h; // Calculate the screen ratio
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("Backgrounds/07/Repeated.png"));
        camera.setToOrtho(false, background.getHeight() * screenRatio, background.getHeight());

        //Rendering
        spriteBatch = new SpriteBatch();
        uiBatch = new SpriteBatch();

        //Textures
        buttonSquareTexture = new Texture("buttons/buttonSquare_blue.png");
        buttonSquareDownTexture = new Texture("buttons/buttonSquare_beige_pressed.png");
        buttonLongTexture = new Texture("buttons/buttonLong_blue.png");
        buttonLongDownTexture = new Texture("buttons/buttonLong_beige_pressed.png");

        //Buttons
        float buttonSize = h * 0.1f;
        moveLeftButton = new Button(0.0f, buttonSize, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveRightButton = new Button(buttonSize*2, buttonSize, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveDownButton = new Button(buttonSize, 0.0f, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveUpButton = new Button(buttonSize, buttonSize*2, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        restartButton = new Button(w/2 - buttonSize*2, h * 0.2f, buttonSize*4, buttonSize, buttonLongTexture, buttonLongDownTexture);
    }
}
