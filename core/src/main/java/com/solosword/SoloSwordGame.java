package com.solosword;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private float playerDamageCooldown = 0;
    private float playerDamageCooldownDuration = 1.0f;
    private SpriteBatch batch;
    private BitmapFont font;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        player = new Player();
        enemy = new Enemy();
        batch = new SpriteBatch();
        font = new BitmapFont();
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

        if (player.facingDirection == Direction.RIGHT) {
            swordWidth = 40;
            swordHeight= 20;
            swordX = player.x + player.width;
            swordY = player.y + 6;
        }

        if (player.facingDirection == Direction.LEFT) {
            swordWidth = 40;
            swordHeight= 20;
            swordX = player.x - swordWidth;
            swordY = player.y + 6;
        }

        if (player.facingDirection == Direction.UP) {
            swordWidth = 20;
            swordHeight= 40;
            swordX = player.x + 6;
            swordY = player.y + player.height;
        }

        if (player.facingDirection == Direction.DOWN) {
            swordWidth = 20;
            swordHeight= 40;
            swordX = player.x + 6;
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

        if (player.isDead()) {
            shapeRenderer.setColor(Color.GRAY);
        } else {
            shapeRenderer.setColor(Color.BLACK);
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(player.x, player.y, player.width, player.height);
        shapeRenderer.end();
    }

    private void drawPlayerHealthBar() {
        float barX = 20;
        float barY = Gdx.graphics.getHeight() - 25;
        float barWidth = 120;
        float barHeight = 10;
        float healthPercent = (float) player.health / player.maxHealth;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(barX, barY, barWidth * healthPercent, barHeight);

        shapeRenderer.end();
    }

    // Main game loop: input first, then update game state, then draw.
    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        handleInput(deltaTime);
        updateGame(deltaTime);

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        drawPlayer();
        drawEnemy();
        drawPlayerHealthBar();

        if (player.isDead()) {
            drawGameOverPopup();
        }

        if (attacking) {
            drawSwordHitBox();
        }
    }

    // Updates game state that is not direct input.
    // This is where timers, hitboxes, collision, and enemy behavior belong.
    private void updateGame(float deltaTime){

        //how often player is damaged
        if (playerDamageCooldown > 0) {
            playerDamageCooldown -= Gdx.graphics.getDeltaTime();
        }

        if (enemy.alive && playerDamageCooldown <= 0 && rectanglesOverlap(
            player.x, player.y, player.width, player.height,
            enemy.x, enemy.y, enemy.width, enemy.height
        )) {
            player.takeDamage(1);
            enemy.pushAwayFromPlayer(player, 20);
            playerDamageCooldown = playerDamageCooldownDuration;
        }

        if (attacking) {
            updateSwordHitBox();

            if (!swordHasHitEnemy && enemy.alive && rectanglesOverlap(
                swordX, swordY, swordWidth, swordHeight,
                enemy.x, enemy.y, enemy.width, enemy.height
            )) {
                enemy.takeDamage(1);
                enemy.knockback(player.facingDirection, 20);
                swordHasHitEnemy = true;
            }
        }

        enemy.chasePlayer(player, deltaTime);

    }

    // Reads movement input and updates the player's position.
    // This stays inside Player because movement is player behavior.
    private void handleInput(float deltaTime) {
        player.handleInput(deltaTime);

        if (player.isDead()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                player.reset();
                enemy.reset();
                attacking = false;
            }

            return;
        }

        //reset button to test combat without restarting app
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            enemy.reset();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            attacking = true;
            attackTimer = 0.2f;
            swordHasHitEnemy = false;
        }

        //prevent attacking when dead
        if (!player.isDead() && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
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
        batch.dispose();
        font.dispose();
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

    //Game over popup
    private void drawGameOverPopup() {
        float boxWidth = 300;
        float boxHeight = 120;
        float boxX = (Gdx.graphics.getWidth() - boxWidth) / 2;
        float boxY = (Gdx.graphics.getHeight() - boxHeight) / 2;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(boxX, boxY, boxWidth, boxHeight);
        shapeRenderer.end();

        batch.begin();
        font.draw(batch, "GAME OVER", boxX + 105, boxY + 80);
        font.draw(batch, "Press SPACE to restart", boxX + 65, boxY + 45);
        batch.end();
    }
}
