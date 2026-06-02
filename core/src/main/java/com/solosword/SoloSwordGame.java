package com.solosword;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class SoloSwordGame extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private boolean attacking = false;
    private float attackTimer = 0;
    private float swordX;
    private float swordY;
    private float swordWidth = 32;
    private float swordHeight = 32;
    private boolean enemyAlive = true;
    private Player player;
    private Enemy enemy;
    private boolean swordHasHitEnemy = false;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        player = new Player();
        enemy = new Enemy();
    }

    private void drawSwordHitBox() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(swordX, swordY, swordWidth, swordHeight);
        shapeRenderer.end();
    }

    // Calculates the sword hitbox based on the player's current facing direction.
    // The red rectangle is temporary debug art, but the hitbox logic is real.
    private void updateSwordHitBox() {
        swordX = player.x;
        swordY = player.y;

        if (player.facingDirection.equals("right")) {
            swordX = player.x + player.width;
            swordY = player.y;
        }

        if (player.facingDirection.equals("left")) {
            swordX = player.x - swordWidth;
            swordY = player.y;
        }

        if (player.facingDirection.equals("up")) {
            swordX = player.x;
            swordY = player.y + player.height;
        }

        if (player.facingDirection.equals("down")) {
            swordX = player.x;
            swordY = player.y - swordHeight;
        }
    }

    private void drawEnemy() {
        float healthBarWidth = enemy.width;
        float healthBarHeight = 5;
        float healthPercent = (float) enemy.health / enemy.maxHealth;

        if (!enemy.alive) {
            return;
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(enemy.x, enemy.y, enemy.width, enemy.height);

        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(enemy.x, enemy.y + enemy.height + 6, healthBarWidth, healthBarHeight);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(enemy.x, enemy.y + enemy.height + 6, healthBarWidth * healthPercent, healthBarHeight);

        shapeRenderer.end();
    }

    private void drawPlayer() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(player.x, player.y, player.width, player.height);
        shapeRenderer.end();
    }

    // Main game loop: input first, then update game state, then draw.
    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        handleInput(deltaTime);
        updateGame();

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        drawPlayer();
        drawEnemy();

        if (attacking) {
            drawSwordHitBox();
        }
    }

    // Updates game state that is not direct input.
    // This is where timers, hitboxes, collision, and enemy behavior belong.
    private void updateGame(){

        if (attacking) {
            updateSwordHitBox();

            if (!swordHasHitEnemy && enemy.alive && rectanglesOverlap(
                swordX, swordY, swordWidth, swordHeight,
                enemy.x, enemy.y, enemy.width, enemy.height
            )) {
                enemy.takeDamage(1);
                swordHasHitEnemy = true;
            }
        }

    }

    // Reads movement input and updates the player's position.
    // This stays inside Player because movement is player behavior.
    private void handleInput(float deltaTime) {
        player.handleInput(deltaTime);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            attacking = true;
            attackTimer = 0.2f;
            swordHasHitEnemy = false;
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
        shapeRenderer.dispose();
    }

    // Generic rectangle collision check.
    // Returns true when two rectangles overlap.
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
