package com.mygdx.game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MenuScreen implements Screen {
    MyGdxGame game;
    private SpriteBatch batch;
    private Skin skin;
    private Stage stage;
    private boolean isActive;
    Texture buttonStartTexture;
    Texture buttonStartDownTexture;
    Button startButton;
    Texture buttonStopTexture;
    Texture buttonStopDownTexture;
    Button stopButton;
    public MenuScreen(MyGdxGame game){this.game = game;}
    public void create() {
            //---creates the components---
            batch = new SpriteBatch();
            skin = new Skin(Gdx.files.internal("gui/uiskin.json"));
            stage = new Stage();
            //---set up the screen---
            float screenWidth = Gdx.graphics.getWidth();
            float screenHeight = Gdx.graphics.getHeight();

            //---set up Start button---
            buttonStartTexture = new Texture("Buttons/play.png");
            buttonStartDownTexture = new Texture("Buttons/play_pressed.png");
            startButton = new Button(screenWidth /2 - 300f, screenHeight/2 - 150f, 600,300, buttonStartTexture,buttonStartDownTexture);

            //---set up Stop button---
            buttonStopTexture = new Texture("Buttons/stop.png");
            buttonStopDownTexture = new Texture("Buttons/stop_pressed.png");
            stopButton = new Button(150,  200, 150,100, buttonStopTexture,buttonStopDownTexture);

    }
    @Override
    public void show() {
        Gdx.app.log("MenuScreen", "menuScreen show called");
        create();
        Gdx.input.setInputProcessor(stage);
        isActive = true;
    }

    @Override
    public void render(float delta) {
        Gdx.app.log("MenuScreen: ","menuScreen render");
        Gdx.gl.glClearColor(0, 0, 0, 1); // Set the clear color (background color)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen

        if(isActive){
            stage.act();
            batch.begin();
            //Touch Input Info
            boolean checkTouch = Gdx.input.isTouched();
            int touchX = Gdx.input.getX();
            int touchY = Gdx.input.getY();
            //Poll user for input
            startButton.update(checkTouch, touchX, touchY);
            //Start game
            if(startButton.justPressed()){
                Gdx.app.log("MenuScreen render:", "Start button pressed");
                game.setScreen(game.gameScreen);
            }
            startButton.draw(batch);
            stopButton.draw(batch);
            batch.end();
            stage.draw();
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
    public void hide() {
        Gdx.app.log("MenuScreen: ","menuScreen hide called");
        Gdx.input.setInputProcessor(null);
        isActive = false;
    }

    @Override
    public void dispose() {

    }
}
