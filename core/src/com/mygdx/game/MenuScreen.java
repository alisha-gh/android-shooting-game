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
    private Stage stage;
    private boolean isActive;

    //Start Button
    private Texture buttonStartTexture;
    private Texture buttonStartDownTexture;
    private Button startButton;

    //Exit Button
    private Texture buttonExitTexture;
    Button exitButton;
    public MenuScreen(MyGdxGame game){this.game = game;}
    public void create() {
            //---creates the components---
            batch = new SpriteBatch();
            stage = new Stage();
            //---set up the screen---
            float screenWidth = Gdx.graphics.getWidth();
            float screenHeight = Gdx.graphics.getHeight();

            //---set up Start button---
            buttonStartTexture = new Texture("Buttons/play.png");
            buttonStartDownTexture = new Texture("Buttons/play_pressed.png");
            startButton = new Button(screenWidth/2 - 300f, screenHeight/2 - 200f, 600,400, buttonStartTexture,buttonStartDownTexture);

            //---set up Stop button---
            buttonExitTexture = new Texture("Buttons/exit_btn.png");
            exitButton = new Button(200,  150, 150,100, buttonExitTexture,buttonExitTexture);
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
        Gdx.gl.glClearColor(30/255f, 80/255f, 140/255f, 1); // Set the clear color (background color)
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
            exitButton.update(checkTouch, touchX, touchY);
            //Start game
            if(startButton.justPressed()){
                Gdx.app.log("MenuScreen render:", "Start button pressed");
                game.setScreen(game.gameScreen);
            }
            if(exitButton.justPressed()){
                Gdx.app.log("MenuScreen render:", "Stop button pressed");
                Gdx.app.exit();
            }
            startButton.draw(batch);
            exitButton.draw(batch);
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
