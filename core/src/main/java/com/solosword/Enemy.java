package com.solosword;

// Simple enemy state for now.
// Later this can grow into health, movement, AI, and animations.
public class Enemy {
    public float x = 350;
    public float y = 220;
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


}

