package com.mygdx.game;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class MyGdxGame extends Game implements ApplicationListener  {
	public static GameScreen gameScreen;
	public static MenuScreen menuScreen;

	@Override
	public void create () {
		gameScreen = new GameScreen(this);
		menuScreen = new MenuScreen(this);
		setScreen(menuScreen);
	}
	@Override
	public void dispose() {
		super.dispose();
	}
	@Override
	// this method calls the super class render
	// which in turn calls the render of the actual screen being used
	public void render() {
		super.render();
	}
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}
	@Override
	public void pause() {
		super.pause();
	}
	@Override
	public void resume() {
		super.resume();
	}
}
