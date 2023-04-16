package com.mygdx.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class GameScreen implements Screen {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture background;
    private MyGdxGame game;

    private float screenRatio;

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
        // Calculate the dimensions of the background image while maintaining aspect ratio
        float backgroundWidth = Gdx.graphics.getWidth();
        float backgroundHeight = backgroundWidth / background.getWidth() * background.getHeight();
        // Draw the background image with the calculated dimensions
        batch.draw(background, 0, 0, backgroundWidth, backgroundHeight);
        batch.end();
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
        background.dispose();
    }

    // Helper method to set up the camera, batch, particle effect, and background image
    private void create() {
        Gdx.app.log("GameScreen: ", "gameScreen create");
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        screenRatio = w / h; // Calculate the screen ratio
        camera = new OrthographicCamera(w, h);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        background = new Texture(Gdx.files.internal("Backgrounds/07/Repeated.png"));
    }
}
