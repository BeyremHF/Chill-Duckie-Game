package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;

/**
 * The SplashScreen class is the screen displayed at the start of the game.
 * It shows the team logo with a fade-in and fade-out effect before transitioning to the main menu.
 */

public class SplashScreen implements Screen {
    private final MazeRunnerGame game;
    private final Texture logoTexture;
    private float alpha = 0f;
    private float timer = 0f;
    private static final float FADE_IN_TIME = 1f;
    private static final float STAY_TIME = 1.5f;
    private static final float FADE_OUT_TIME = 1f;
    private static final float TOTAL_TIME = FADE_IN_TIME + STAY_TIME + FADE_OUT_TIME;

    /**
     * Creates the SplashScreen instance.
     *
     * @param game The game instance to transition to the next screen.
     */

    public SplashScreen(MazeRunnerGame game) {
        this.game = game;
        this.logoTexture = new Texture(Gdx.files.internal("logo.png"));
    }

    /**
     * Renders the splash screen. Displays the logo with a fade-in effect, keeps it visible,
     * and then fades it out before transitioning to the main menu.
     *
     * @param delta The time elapsed since the last frame.
     */

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        timer += delta;

        // Calculate alpha based on current phase
        if (timer < FADE_IN_TIME) {
            // Fade in phase
            alpha = Interpolation.fade.apply(0, 1, timer / FADE_IN_TIME);
        } else if (timer < FADE_IN_TIME + STAY_TIME) {
            // Stay phase
            alpha = 1f;
        } else if (timer < TOTAL_TIME) {
            // Fade out phase
            float fadeOutProgress = (timer - (FADE_IN_TIME + STAY_TIME)) / FADE_OUT_TIME;
            alpha = Interpolation.fade.apply(1, 0, fadeOutProgress);
        } else {
            // Transition to menu screen
            game.goToMenu();
            return;
        }

        SpriteBatch batch = game.getSpriteBatch();
        batch.begin();

        // Calculate position to center the logo
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float logoWidth = logoTexture.getWidth();
        float logoHeight = logoTexture.getHeight();

        // Scale logo (adjust scale factor)
        float scale = Math.min(screenWidth / logoWidth, screenHeight / logoHeight) * 0.3f;
        float scaledWidth = logoWidth * scale;
        float scaledHeight = logoHeight * scale;

        // Center position
        float x = (screenWidth - scaledWidth) / 2;
        float y = (screenHeight - scaledHeight) / 2;

        // Set alpha for fade effect
        batch.setColor(1, 1, 1, alpha);

        // Draw the logo
        batch.draw(logoTexture, x, y, scaledWidth, scaledHeight);

        batch.end();
    }

    /**
     * Resizes the screen. Currently not used but can be implemented if needed.
     *
     * @param width The new width of the screen.
     * @param height The new height of the screen.
     */

    @Override
    public void resize(int width, int height) {
    }

    /**
     * Initializes the screen when shown. Resets the timer and alpha to their initial values.
     */

    @Override
    public void show() {
        // Reset timer and alpha when shown
        timer = 0f;
        alpha = 0f;
    }

    /**
     * Disposes of resources used by this screen.
     * This includes the logo texture to prevent memory leaks.
     */

    @Override
    public void dispose() {
        logoTexture.dispose();
    }

    // The following methods are part of the Screen interface but are not used in this screen.
    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}