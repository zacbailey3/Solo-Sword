package com.solosword;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;


// Simple player state for now.
// These fields are public while we are learning and refactoring quickly.
public class Player {
    public float x = 140;
    public float y = 210;
    public float width = 32;
    public float height = 32;
    public float speed = 200;
    public String facingDirection = "down";

    public void handleInput(float deltaTime) {
        float moveAmount = speed * deltaTime;

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += moveAmount;
            facingDirection = "right";
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= moveAmount;
            facingDirection = "left";
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += moveAmount;
            facingDirection = "up";
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= moveAmount;
            facingDirection = "down";
        }

        keepInsideScreen();

    }

    //keep from running off screen
    private void keepInsideScreen() {
        if (x < 0) {
            x = 0;
        }
        if (x + width > Gdx.graphics.getWidth()) {
            x = Gdx.graphics.getWidth() - width;
        }
        if (y < 0) {
            y = 0;
        }
        if (y + height > Gdx.graphics.getHeight()) {
            y = Gdx.graphics.getHeight() - height;
        }
    }

}
