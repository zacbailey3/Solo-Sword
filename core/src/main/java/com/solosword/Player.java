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
    public Direction facingDirection = Direction.DOWN;
    public int health = 5;
    public int maxHealth = 5;

    public void takeDamage(int damage) {
        health -= damage;

        if (health < 0) {
            health = 0;
        }
    }

    public boolean isDead() {
        return health <= 0;
    }

    //where to reset player when dead
    public void reset() {
        x = 140;
        y = 210;
        health = maxHealth;
    }

    public void handleInput(float deltaTime) {

        if (isDead()) {
            return;
        }

        float moveAmount = speed * deltaTime;

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += moveAmount;
            facingDirection = Direction.RIGHT;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= moveAmount;
            facingDirection = Direction.LEFT;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += moveAmount;
            facingDirection = Direction.UP;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= moveAmount;
            facingDirection = Direction.DOWN;
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
