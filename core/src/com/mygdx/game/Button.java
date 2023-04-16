package com.mygdx.game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Button {
    private Texture texture;
    private Rectangle bounds;
    private boolean pressed;

    public Button(Texture texture, float x, float y, float width, float height) {
        this.texture = texture;
        this.bounds = new Rectangle(x, y, width, height);
        this.pressed = false;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public boolean isPressed(float x, float y) {
        if (bounds.contains(x, y)) {
            pressed = true;
            return true;
        }
        return false;
    }

    public void reset() {
        pressed = false;
    }

    public boolean isPressed() {
        return pressed;
    }
}
