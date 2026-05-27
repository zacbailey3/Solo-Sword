package com.solosword;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class SoloSwordGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    private String facingDirection = "down";
    private ShapeRenderer shapeRenderer;
    private boolean attacking = false;
    private float attackTimer = 0;

    private float playerX = 140;
    private float playerY = 210;
    private float playerSpeed = 200;
    private float playerWidth = 32;
    private float playerHeight = 32;

    private float enemyX = 350;
    private float enemyY = 220;
    private float enemyWidth = 32;
    private float enemyHeight = 32;

    private boolean enemyAlive = true;



    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
        shapeRenderer = new ShapeRenderer();
    }

    //create a sword hitbox
    private void drawSwordHitBox() {
        float swordX = playerX;
        float swordY = playerY;
        float swordWidth = 32;
        float swordHeight = 32;

        if (facingDirection.equals("right")) {
            swordX = playerX + playerWidth;
            swordY = playerY;
        }

        if (facingDirection.equals("left")) {
            swordX = playerX - swordWidth;
            swordY = playerY;
        }

        if (facingDirection.equals("up")) {
            swordX = playerX;
            swordY = playerY + playerHeight;
        }

        if (facingDirection.equals("down")) {
            swordX = playerX;
            swordY = playerY - swordHeight;
        }

        //if sword dimensions overlap with enemy, dead
        if (enemyAlive && rectanglesOverlap(
            swordX, swordY, swordWidth, swordHeight,
             enemyX, enemyY, enemyWidth, enemyHeight
        )) {
            enemyAlive = false;
        }

        //"sword" size/color
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(swordX, swordY, swordWidth, swordHeight);
        shapeRenderer.end();
    }
    //create enemy, check alive
    private void drawEnemy() {
        if (!enemyAlive) {
            return;
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(enemyX, enemyY, enemyWidth, enemyHeight);
        shapeRenderer.end();
    }

    private void updateGame(){

    }

    //framerate dependant movement
    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        handleInput(deltaTime);
        updateGame();

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(playerX, playerY, playerWidth, playerHeight);
        shapeRenderer.end();

        drawEnemy();

        if (attacking) {
            drawSwordHitBox();
        }
    }

    //directional movement and check Direction
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

        //if attacking
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            attacking = true;
            attackTimer = 0.2f;
        }

        if (attacking) {
            attackTimer -= deltaTime;

            if (attackTimer <= 0) {
                attacking = false;
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
        shapeRenderer.dispose();
    }

    //checking location to find if overlap
    private boolean rectanglesOverlap(
        float x1, float y1, float width1, float height1,
        float x2, float y2, float width2, float height2
    ) {
        return x1 < x2 + width2 &&
            x1 + width1 > x2 &&
            y1 < y2 + height2 &&
            y1 + height1 > y2;
    }
}
