package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * BaseScreen serves as the parent class for all screen classes in the game.
 * It implements common functionality shared across different screens.
 */

public abstract class BaseScreen implements Screen {
    protected final MazeRunnerGame game;
    protected final Stage stage; // The Stage object managing UI elements and input processing
    protected final Texture background; // The background texture displayed behind the menu elements

    public BaseScreen(MazeRunnerGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport(), game.getSpriteBatch()); // Create a stage for UI elements as well as a viewport with the camera
        this.background = new Texture(Gdx.files.internal("product1.jpg")); // Source for the background image
    }

    /**
     * Renders the menu screen.
     * <p>
     * This method:
     * 1. Clears the screen
     * 2. Renders the background image scaled to cover the entire screen
     * 3. Updates and draws all UI elements
     *
     * @param delta The time in seconds since the last render
     */

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Calculate the screen dimensions
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Calculate the image dimensions
        float imageWidth = background.getWidth();
        float imageHeight = background.getHeight();

        // Calculate the scale to cover the entire screen
        float scale = Math.max(screenWidth / imageWidth, screenHeight / imageHeight);

        // Scale the image to fit the screen while covering it completely
        float scaledWidth = imageWidth * scale;
        float scaledHeight = imageHeight * scale;

        // Center the image
        float x = (screenWidth - scaledWidth) / 2;
        float y = (screenHeight - scaledHeight) / 2;

        // Begin batch rendering
        SpriteBatch batch = (SpriteBatch) stage.getBatch();
        batch.begin();

        // Draw the background image
        batch.draw(background, x, y, scaledWidth, scaledHeight);

        batch.end();

        // Update and draw the stage
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    /**
     * Handles screen resize events by updating the viewport dimensions.
     * This ensures UI elements maintain proper scaling and positioning.
     *
     * @param width The new screen width in pixels
     * @param height The new screen height in pixels
     */

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // Update the stage viewport on resize

    }

    /**
     * Called when this screen becomes the current screen.
     * Sets up input processing for UI elements.
     */

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage); // Set the input processor so the stage can receive input events
    }

    /**
     * Disposes of resources used by this screen.
     * This includes the Stage and background Texture to prevent memory leaks.
     */

    @Override
    public void dispose() {
        stage.dispose(); // Dispose of the stage when screen is disposed
        background.dispose(); // Dispose of the background texture
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}
}