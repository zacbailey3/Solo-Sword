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
    private float playerSpeed = 200;
    private String facingDirection = "down";


    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
    }

    //framerate dependant movement
    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        handleInput(deltaTime);

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        batch.begin();
        batch.draw(image, playerX, playerY);
        batch.end();
    }

    private void handleInput(float deltaTime) {
        float moveAmount = playerSpeed * deltaTime;

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerX += moveAmount;
            facingDirection = "right";
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerX -= moveAmount;
            facingDirection = "left";
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            playerY += moveAmount;
            facingDirection = "up";
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            playerY -= moveAmount;
            facingDirection = "down";
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
