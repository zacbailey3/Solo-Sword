# SoloSword

SoloSword is an ongoing Java / LibGDX top-down action game project inspired by classic adventure combat games. The project is being built step by step as a learning-focused game development exercise.

The current goal is to build a small but playable action loop with movement, sword combat, enemies, leveling, and simple progression.

## Current Features

- Top-down WASD player movement
- Mouse-based facing direction
- Sword attack using `SPACE` or left mouse click
- Sword hitbox collision
- Enemy health and damage
- Enemy knockback
- Player health and game over state
- Restart after death
- Multiple enemies per level
- Level progression
- Boss enemy every 5 levels
- Character XP and character leveling
- Scaling XP requirement
- Randomized enemy movement speed
- Slightly randomized enemy pathing
- Safer enemy spawning away from the player
- Debug-style rectangle graphics while mechanics are being developed

## Tech Stack

- Java
- LibGDX
- Gradle
- IntelliJ IDEA

## Project Status

This is an active learning project. The game currently uses simple colored rectangles as placeholder visuals while core mechanics are developed and tested.

Future improvements may include:

- Real player and enemy sprites
- Animation
- Sound effects
- Better enemy types
- Improved boss behavior
- Tile maps or rooms
- Items and upgrades
- Menus and pause screen
- More polished UI

## Controls

| Action | Input |
| --- | --- |
| Move | WASD |
| Aim / Face Direction | Mouse cursor |
| Attack | SPACE or Left Mouse Click |
| Restart after Game Over | SPACE |
| Reset current level/debug enemies | R |

## Development Goal

The purpose of SoloSword is not just to make a game, but to learn how game systems are built from the ground up. Each mechanic is added gradually, with an emphasis on understanding game loops, state, collision, rendering, and code organization.
