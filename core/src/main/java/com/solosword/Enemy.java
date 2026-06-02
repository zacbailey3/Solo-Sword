package com.solosword;

// Simple enemy state for now.
// Later this can grow into health, movement, AI, and animations.
    public class Enemy {
        public float startX = 350;
        public float startY = 220;
        public float x = startX;
        public float y = startY;
        public float width = 32;
        public float height = 32;
        public boolean alive = true;
        public int health = 3;
        public int maxHealth = 3;

        //enemy taking damage checking alive
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

    //reset enemy when gone
    public void reset() {
        x = startX;
        y = startY;
        health = maxHealth;
        alive = true;
    }

}

