package com.solosword;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;

// Simple enemy state for now.
// Later this can grow into health, movement, AI, and animations.
    public class Enemy {
    // Starting position
    public float startX;
    public float startY;

    // Current position and size
    public float x;
    public float y;
    public float width = 32;
    public float height = 32;

    //boss
    public boolean boss = false;
    public int contactDamage = 1;

    // Health
    public boolean alive = true;
    public int health = 3;
    public int maxHealth = 3;

    // Movement
    public float speed = 80;

    public int experienceValue = 1;

    //movement "randomness"
    private float wanderX = 0;
    private float wanderY = 0;
    private float wanderTimer = 0;

        // Setup
        public Enemy (float startX, float startY) {


            this.startX = startX;
            this.startY = startY;
            this.x = startX;
            this.y = startY;
            speed = MathUtils.random(60f, 120f);
        }

        public static Enemy createBoss(float startX, float startY, int healthMultiplier) {
            Enemy boss = new Enemy(startX, startY);

            boss.boss = true;
            boss.width = 64;
            boss.height = 64;
            boss.speed = 140;
            boss.health = 10 * healthMultiplier;
            boss.maxHealth = boss.health;
            boss.contactDamage = 2;
            boss.experienceValue = 5;

            return boss;
        }

         //reset enemy when gone
        public void reset() {
            x = startX;
            y = startY;
            health = maxHealth;
            alive = true;
        }

        // Health behavior
        public void takeDamage(int damage) {
            health -= damage;

            if (health <= 0) {
                alive = false;
            }
        }

    public void knockback(Direction direction, float amount) {
        if (direction == Direction.RIGHT) {
            x += amount;
        }

        if (direction == Direction.LEFT) {
            x -= amount;
        }

        if (direction == Direction.UP) {
            y += amount;
        }

        if (direction == Direction.DOWN) {
            y -= amount;
        }
    }

    // Movement and knockback
    //enemy collision with player
    public void pushAwayFromPlayer(Player player, float amount) {
        Vector2 direction = new Vector2(x - player.x, y - player.y);

        if (direction.len() > 0) {
            direction.nor();
            x += direction.x * amount;
            y += direction.y * amount;
        }
    }

    //enemy movement consistent when persuing player
    public void chasePlayer(Player player, float deltaTime) {
        if (!alive) {
            return;
        }

        wanderTimer -= deltaTime;

        if (wanderTimer <= 0) {
            wanderTimer = MathUtils.random(0.3f, 0.8f);
            wanderX = MathUtils.random(-0.7f, 0.7f);
            wanderY = MathUtils.random(-0.7f, 0.7f);
        }

        Vector2 direction = new Vector2(player.x - x, player.y - y);

        if (direction.len() > 0) {
            direction.nor();

            x += direction.x * speed * deltaTime;
            y += direction.y * speed * deltaTime;
        }
    }

}

