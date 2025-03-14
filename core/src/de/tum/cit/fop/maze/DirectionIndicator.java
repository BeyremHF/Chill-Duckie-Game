package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

/**
 * The DirectionIndicator class is responsible for rendering animated arrow indicator
 * pointing from the player's position toward the closest exit, after the statue has been collected.
 */

public class DirectionIndicator {
    private static final float ARROW_SIZE = 15f; // Size of each arrow
    private static final float ARROW_SPACING = 20f; // Spacing between consecutive arrows
    private static final float INITIAL_OFFSET = 60f; // Initial distance from the player to the first arrow
    private static final float ANIMATION_SPEED = 1f; // Speed of animation (frame transitions per second)
    private static final int MAX_ARROWS = 10; // Maximum number of arrows visible at a time
    private static final int TOTAL_FRAMES = 20; // Total number of animation frames in the arrow sprite sheet

    private final TextureRegion[] arrowFrames; // Frames of the arrow animation
    private float animationTime = 0f; // Current animation time (cycles from 0 to 1)
    private int currentFrame = 0; // Index of the current frame being displayed

    /**
     * Initializes the DirectionIndicator by loading the arrow texture and splitting it into frames.
     */

    public DirectionIndicator() {
        // Load the arrow sprite sheet
        Texture arrowSheet = new Texture(Gdx.files.internal("arrows.png"));
        arrowFrames = new TextureRegion[TOTAL_FRAMES];

        // Calculate the width and height of each frame in the sprite sheet
        int frameWidth = arrowSheet.getWidth() / TOTAL_FRAMES;
        int frameHeight = arrowSheet.getHeight();

        // Extract individual frames from the sprite sheet
        for (int i = 0; i < TOTAL_FRAMES; i++) {
            arrowFrames[i] = new TextureRegion(
                    arrowSheet,
                    i * frameWidth,  // x position of the frame in the sheet
                    0,                  // y position is fixed as the sprite sheet is a single row
                    frameWidth,
                    frameHeight
            );
        }
    }

    /**
     * Updates the animation by progressing the current frame based on the elapsed time.
     *
     * @param delta Time elapsed since the last update, in seconds.
     */

    public void update(float delta) {
        animationTime += delta * ANIMATION_SPEED; // Increment animation time

        // Reset animation time if it completes a cycle
        if (animationTime >= 1.0f) {
            animationTime = 0f;
        }

        // Update the current animation frame
        currentFrame = (currentFrame + 1) % TOTAL_FRAMES;
    }

    /**
     * Renders the animated arrow indicators pointing towards the closest exit.
     *
     * @param batch         The SpriteBatch used for drawing.
     * @param playerPos     The current position of the player.
     * @param exitPositions A list of exit positions in the maze.
     */

    public void render(SpriteBatch batch, Vector2 playerPos, List<Vector2> exitPositions) {
        // If batch is not drawing or there are no exits, return early
        if (batch == null || !batch.isDrawing() || exitPositions.isEmpty()) {
            return;
        }

        // Find the closest exit to the player
        Vector2 closestExit = findClosestExit(playerPos, exitPositions);

        // Calculate the direction vector from the player to the closest exit
        Vector2 direction = new Vector2(closestExit).sub(playerPos).nor();

        // Calculate the rotation angle of the arrows
        float angle = direction.angleDeg();

        // Determine the number of visible arrows based on animation time
        int visibleArrows = (int) (MAX_ARROWS * animationTime) + 1;

        // Store the original batch color
        float originalR = batch.getColor().r;
        float originalG = batch.getColor().g;
        float originalB = batch.getColor().b;
        float originalA = batch.getColor().a;

        // Render each arrow
        for (int i = 0; i < visibleArrows && i < MAX_ARROWS; i++) {
            // Calculate the distance of the arrow from the player
            float distance = INITIAL_OFFSET + (i * ARROW_SPACING);
            float x = playerPos.x + direction.x * distance;
            float y = playerPos.y + direction.y * distance;

            // Compute alpha (transparency) for fade effect
            float alpha = 1.0f - ((float) i / MAX_ARROWS);

            // Set the batch color with the computed alpha
            batch.setColor(originalR, originalG, originalB, alpha);

            // Draw the arrow with rotation and scaling
            batch.draw(arrowFrames[currentFrame],
                    x - ARROW_SIZE / 2, y - ARROW_SIZE / 2,   // Position
                    ARROW_SIZE / 2, ARROW_SIZE / 2,          // Origin for rotation
                    ARROW_SIZE, ARROW_SIZE,                         // Dimensions
                    1, 1,                                    // Scale
                    angle,                                          // Rotation angle
                    true);                                          // Flip flag (if needed)
        }

        // Restore the original batch color
        batch.setColor(originalR, originalG, originalB, originalA);
    }

    /**
     * Finds the closest exit to the player's position.
     *
     * @param playerPos     The player's current position.
     * @param exitPositions A list of exit positions.
     * @return The position of the closest exit.
     */

    private Vector2 findClosestExit(Vector2 playerPos, List<Vector2> exitPositions) {
        Vector2 closest = exitPositions.get(0);
        float minDistance = playerPos.dst(closest);

        // Iterate through all exits to find the closest one
        for (Vector2 exit : exitPositions) {
            float distance = playerPos.dst(exit);
            if (distance < minDistance) {
                minDistance = distance;
                closest = exit;
            }
        }
        return closest;
    }

    /**
     * Disposes of the texture resources to prevent memory leaks.
     */

    public void dispose() {
        if (arrowFrames != null && arrowFrames.length > 0 && arrowFrames[0].getTexture() != null) {
            arrowFrames[0].getTexture().dispose();
        }
    }
}
