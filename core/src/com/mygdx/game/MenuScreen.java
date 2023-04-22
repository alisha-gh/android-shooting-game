package com.mygdx.game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
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
    public MenuScreen(MyGdxGame game){this.game = game;}
    public void create() {
            //---creates the components---
            batch = new SpriteBatch();
            skin = new Skin(Gdx.files.internal("gui/uiskin.json"));
            stage = new Stage();
            //---set up the button---
            float screenWidth = Gdx.graphics.getWidth();
            float screenHeight = Gdx.graphics.getHeight();
            final TextButton startButton = new TextButton("Start", skin, "default");
            startButton.setWidth(600f);
            startButton.setHeight(400f);
            startButton.setPosition(screenWidth /2 - 300f, screenHeight/2 - 200f); 	//remember to include the offset, which is half of the button size

            stage.addActor(startButton); //each button must be added to the state
            Gdx.input.setInputProcessor(stage); //the stage needs to be set to handle input
            //---click event. what happends when the button is clicked---
            startButton.addListener(new ClickListener() {
                @Override
                public void clicked (InputEvent event, float x, float y){
                    game.setScreen(game.gameScreen);
                }
            });
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

        if( isActive){
            stage.act();
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
