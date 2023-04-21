package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Button {

    float x;
    float y;
    float w;
    float h;
    boolean isDown = false;
    boolean isDownPrev = false;

    Texture textureUp;
    Texture textureDown;

    boolean justPressed = false;

    public Button(float x, float y, float w, float h, Texture textureUp, Texture textureDown) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        this.textureUp = textureUp;
        this.textureDown = textureDown;
    }

    public void setTexture(Texture texture) {
        this.textureDown = texture;
        this.textureUp = texture;
    }

    public void update(boolean checkTouch, int touchX, int touchY) {
        isDown = false;

        if (checkTouch) {
            int h2 = Gdx.graphics.getHeight();
            //Touch coordinates have origin in top-left instead of bottom left

            if (touchX >= x && touchX <= x + w && h2 - touchY >= y && h2 - touchY <= y + h) {
                isDown = true;
                this.justPressed = false;
                if (!isDownPrev) {
                    this.justPressed = true;
                }
                this.isDownPrev = true;
            }
        }
        else {
            this.justPressed = false;
            this.isDownPrev = false;
        }
    }

    public void draw(SpriteBatch batch) {
        if (! isDown) {
            batch.draw(textureUp, x, y, w, h);
        } else {
            batch.draw(textureDown, x, y, w, h);
        }
    }

    public boolean justPressed() {
        return justPressed;
    }
}
