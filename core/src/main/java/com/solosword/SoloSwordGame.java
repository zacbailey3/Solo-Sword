package com.solosword;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class SoloSwordGame extends ApplicationAdapter {
    // Rendering
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;

    // Game objects
    private Player player;
    private SwordAttack swordAttack;
    private List<Enemy> enemies;

    // Level state
    private int gameLevel = 1;

    // Player damage cooldown
    private float playerDamageCooldown = 0;
    private float playerDamageCooldownDuration = 1.0f;

    //game level
    private boolean levelCleared = false;
    private float levelClearTimer = 0;
    private float levelClearDelay = 1.0f;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();

        player = new Player();
        swordAttack = new SwordAttack();
        enemies = new ArrayList<>();

        spawnLevel();
    }

    private void drawLevelText() {
        batch.begin();
        font.draw(batch, "Floor " + gameLevel, Gdx.graphics.getBackBufferWidth() - 90, Gdx.graphics.getHeight() - 15);
        batch.end();
    }

    private void drawLevelClearText() {
        batch.begin();
        font.draw(batch, "Level Clear", Gdx.graphics.getWidth() / 2f - 40, Gdx.graphics.getHeight() / 2f);
        batch.end();
    }

    private void drawExperienceText() {
        batch.begin();
        font.draw(
            batch,
            "XP " + player.experience + "/" + player.experienceToNextLevel,
            20,
            Gdx.graphics.getHeight() - 35
        );
        batch.end();
    }

    // Main game loop: input first, then update game state, then draw.
    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        handleInput(deltaTime);
        updateGame(deltaTime);

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        drawPlayer();
        drawEnemies();

        if (swordAttack.active) {
            drawSwordHitBox();
        }

        drawPlayerHealthBar();
        drawHealthText();

        drawCharacterLevelText();
        drawLevelText();

        drawExperienceText();



        if (player.isDead()) {
            drawGameOverPopup();
        }

        if (levelCleared) {
            drawLevelClearText();
        }
    }

    // Reads player controls, debug controls, restart input, and attack input.
    private void handleInput(float deltaTime) {
        if (player.isDead()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                player.reset();
                spawnLevel();
                swordAttack.stop();
            }

            return;
        }

        player.handleInput(deltaTime);
        player.faceMouseCursor();

        // Debug reset so we can test the current level without restarting the app.
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            spawnLevel();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            swordAttack.start();
        }

        swordAttack.update(deltaTime);
    }

    // Updates timers, enemy movement, collision, damage, and level progression.
    private void updateGame(float deltaTime) {
        if (player.isDead()) {
            return;
        }

        if (levelCleared) {
            levelClearTimer -= deltaTime;

            if (levelClearTimer <= 0) {
                gameLevel++;
                spawnLevel();
                levelCleared = false;
            }
            return;
        }

        if (playerDamageCooldown > 0) {
            playerDamageCooldown -= deltaTime;
        }

        if (swordAttack.active) {
            swordAttack.updateHitBox(player);
        }

        for (Enemy enemy : enemies) {
            if (enemy.alive && playerDamageCooldown <= 0 && rectanglesOverlap(
                player.x, player.y, player.width, player.height,
                enemy.x, enemy.y, enemy.width, enemy.height
            )) {
                player.takeDamage(enemy.contactDamage);
                enemy.pushAwayFromPlayer(player, 20);
                playerDamageCooldown = playerDamageCooldownDuration;
            }

            if (swordAttack.active && !swordAttack.hasHitEnemy && enemy.alive && rectanglesOverlap(
                swordAttack.x, swordAttack.y, swordAttack.width, swordAttack.height,
                enemy.x, enemy.y, enemy.width, enemy.height
            )) {
                enemy.takeDamage(player.getAttackDamage());

                if (!enemy.alive) {
                    player.gainExperience(enemy.experienceValue);
                }

                enemy.knockback(player.facingDirection, 20);
                swordAttack.hasHitEnemy = true;
            }

            enemy.chasePlayer(player, deltaTime);
        }

        if (allEnemiesDefeated()) {
            levelCleared = true;
            levelClearTimer = levelClearDelay;
        }
    }

    // Creates enemies for the current level.
    // For now, the number of enemies is equal to the level number.
    private void spawnLevel() {
        enemies.clear();

        //check if boss level
        if (gameLevel % 5 == 0) {
            int bossHealthMultiplier = gameLevel / 5;
            enemies.add(Enemy.createBoss(320, 220, bossHealthMultiplier));
            return;
        }

        for (int i = 0; i < gameLevel; i++) {
            float enemyX;
            float enemyY;

            do {
                enemyX = MathUtils.random(40f, Gdx.graphics.getWidth() - 80f);
                enemyY = MathUtils.random(40f, Gdx.graphics.getHeight() - 80f);
            } while (tooCloseToPlayer(enemyX, enemyY));

            enemies.add(new Enemy(enemyX, enemyY));
        }

    }

    //avoid spawning enemies too close to player
    private boolean tooCloseToPlayer(float x, float y) {
        float playerCenterX = player.x + player.width / 2;
        float playerCenterY = player.y + player.height / 2;

        float dx = x - playerCenterX;
        float dy = y - playerCenterY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        return distance < 150;
    }

    private void drawPlayer() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (player.isDead()) {
            shapeRenderer.setColor(Color.GRAY);
        } else {
            shapeRenderer.setColor(Color.BLACK);
        }

        shapeRenderer.rect(player.x, player.y, player.width, player.height);
        shapeRenderer.end();
    }

    private void drawEnemies() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Enemy enemy : enemies) {
            if (!enemy.alive) {
                continue;
            }

            float healthBarWidth = enemy.width;
            float healthBarHeight = 5;
            float healthPercent = (float) enemy.health / enemy.maxHealth;

            //color based on enemy type
            if (enemy.boss) {
                shapeRenderer.setColor(Color.RED);
            } else {
                shapeRenderer.setColor(Color.GREEN);
            }

            shapeRenderer.rect(enemy.x, enemy.y, enemy.width, enemy.height);
            shapeRenderer.setColor(Color.DARK_GRAY);
            shapeRenderer.rect(enemy.x, enemy.y + enemy.height + 6, healthBarWidth, healthBarHeight);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(enemy.x, enemy.y + enemy.height + 6, healthBarWidth * healthPercent, healthBarHeight);
        }

        shapeRenderer.end();
    }

    private void drawSwordHitBox() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.LIGHT_GRAY);
        shapeRenderer.rect(swordAttack.x, swordAttack.y, swordAttack.width, swordAttack.height);
        shapeRenderer.end();
    }

    //HEALTH BAR
    private void drawPlayerHealthBar() {
        float barX = 80;
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

    private void drawHealthText() {
        batch.begin();
        font.draw(
            batch,
            player.health + "/" + player.maxHealth,
            210,
            Gdx.graphics.getHeight() - 15
        );
        batch.end();
    }

    private void drawCharacterLevelText() {
        batch.begin();
        font.draw(batch, "Lvl " + player.characterLevel, 20, Gdx.graphics.getHeight() - 15);
        batch.end();
    }

    // Game over popup. Restart input is handled in handleInput().
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

    private boolean allEnemiesDefeated() {
        for (Enemy enemy : enemies) {
            if (enemy.alive) {
                return false;
            }
        }

        return true;
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

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }
}
