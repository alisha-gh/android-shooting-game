package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen implements Screen {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture background;
    private MyGdxGame game;

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
    }
}
