package com.solosword;

// Tracks the current sword swing.
// This owns attack timing, hitbox size, hitbox position, and one-hit-per-swing state.
public class SwordAttack {
    // Attack state
    public boolean active = false;
    public float timer = 0;
    public boolean hasHitEnemy = false;

    // Hitbox
    public float x;
    public float y;
    public float width = 32;
    public float height = 32;

    private float duration = 0.2f;

    public void start() {
        active = true;
        timer = duration;
        hasHitEnemy = false;
    }

    public void update(float deltaTime) {
        if (!active) {
            return;
        }

        timer -= deltaTime;

        if (timer <= 0) {
            active = false;
        }
    }

    public void stop() {
        active = false;
        timer = 0;
        hasHitEnemy = false;
    }

    // Calculates the sword hitbox based on the player's current facing direction.
    // The red rectangle is temporary debug art, but the hitbox logic is real.
    public void updateHitBox(Player player) {
        x = player.x;
        y = player.y;

        if (player.facingDirection == Direction.RIGHT) {
            width = 40;
            height = 20;
            x = player.x + player.width;
            y = player.y + 6;
        }

        if (player.facingDirection == Direction.LEFT) {
            width = 40;
            height = 20;
            x = player.x - width;
            y = player.y + 6;
        }

        if (player.facingDirection == Direction.UP) {
            width = 20;
            height = 40;
            x = player.x + 6;
            y = player.y + player.height;
        }

        if (player.facingDirection == Direction.DOWN) {
            width = 20;
            height = 40;
            x = player.x + 6;
            y = player.y - height;
        }
    }
}
