package com.solosword;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class SoloSwordGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    private float playerX = 140;
    private float playerY = 210;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        //movement input WASD
        //right
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerX += 2;
        }
        //left
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerX -= 2;
        }
        //up
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            playerY += 2;
        }
        //down
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            playerY -= 2;
        }

        batch.begin();
        batch.draw(image, playerX, playerY);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
