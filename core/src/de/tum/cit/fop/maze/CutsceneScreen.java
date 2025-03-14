package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;

/**
 *  The CutsceneScreen class implements a narrative introduction screen for the maze game.
 *  It displays the game's storyline in a series of fade-in paragraphs that introduce
 *  the player to the game's narrative context.
 * <p>
 *  The screen manages the display of multiple text paragraphs with fade-in effects and user interaction through the space key.
 *  Players can either wait for the natural fade-in animation or skip to the next paragraph using the space key.
 */

public class CutsceneScreen implements Screen {
    private final MazeRunnerGame game;
    private final BitmapFont font; // Font used for rendering text
    private final String[] paragraphs = {  // The game storyline, divided into paragraphs and stored into an Array of strings
            "The main character Orpheus, and his beloved wife Eurydice, both lost their lives from a venomous snake bite. Until one stormy night, he was mysteriously resurrected and rose from his grave.",
            "Disoriented, he searched frantically for Eurydice's final resting place, but it was nowhere to be found. Now, Orpheus must traverse the treacherous cemetery, avoiding its supernatural monsters and deadly traps. ",
            "Use the arrow keys or WASD to move and Shift to sprint. Collect the statue and find the exit to proceed to the next level. Also look out for gems and hearts, they might come in handy to finally reunite Orpheus with his wife and escape the realm of the dead together."
    };

    private int currentParagraph = 0; // Index of the currently displayed paragraph
    private float fadeIn = 0; // Current fade-in progress (0.0 to 1.0)
    private final GlyphLayout layout; // Layout object for text rendering calculations
    private final float FADE_SPEED = 0.5f; // Speed of the fade-in animation
    private final float FONT_SCALE = 1.5f; // Scale factor for the main story text
    private final Color textColor = new Color(1, 1, 1, 1);   // Color object for text rendering with alpha support
    private final GameScreen nextScreen; // Reference to the next screen to be shown after the cutscene

    /**
     * Constructor for CutsceneScreen.
     * Constructs a new CutsceneScreen with the specified game instance and next screen.
     * Initializes the text rendering system with appropriate scaling and layout settings.
     *
     * <p>The constructor sets up:</p>
     * <ul>
     *   <li>Font configuration with proper scaling</li>
     *   <li>Text layout system for paragraph rendering</li>
     *   <li>Screen transition handling</li>
     * </ul>
     *
     * @param game The main game instance providing access to shared resources
     * @param nextScreen The game screen to transition to after the cutscene
     */

    public CutsceneScreen(MazeRunnerGame game, GameScreen nextScreen) {
        this.game = game;
        this.nextScreen = nextScreen;
        this.font = game.getSkin().getFont("font");
        this.font.getData().setScale(FONT_SCALE);
        this.layout = new GlyphLayout();
    }

    @Override
    public void render(float delta) {
        // Clear screen with black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Handle space key press
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (fadeIn < 1f) {
                fadeIn = 1f;
            } else {
                currentParagraph++;
                if (currentParagraph >= paragraphs.length) {
                    // Reset font scale and transition to next screen
                    font.getData().setScale(1f);
                    game.popScreen();
                    return;
                }
                fadeIn = 0;
            }
        }

        // Update fade-in progress
        fadeIn = Math.min(fadeIn + delta * FADE_SPEED, 1);

        game.getSpriteBatch().begin();

        // Calculate total height of all visible paragraphs
        float totalHeight = 0;
        GlyphLayout tempLayout = new GlyphLayout();
        float paragraphSpacing = 70; // Space between paragraphs

        for (int i = 0; i <= currentParagraph; i++) {
            tempLayout.setText(font, paragraphs[i], Color.WHITE, Gdx.graphics.getWidth() * 0.8f, Align.center, true);
            totalHeight += tempLayout.height;
            if (i < currentParagraph) {
                totalHeight += paragraphSpacing;
            }
        }

        // Calculate starting Y position to center all paragraphs vertically
        float startY = (Gdx.graphics.getHeight() + totalHeight) / 2;

        // Draw shown paragraphs
        float currentY = startY;
        for (int i = 0; i <= currentParagraph; i++) {
            float alpha = (i == currentParagraph) ? fadeIn : 1f;
            textColor.a = alpha;

            // Set the color with alpha before creating the layout
            layout.setText(font, paragraphs[i], textColor, Gdx.graphics.getWidth() * 0.8f, Align.center, true);

            // Correctly calculate X position based on layout width and screen width
            float x = (Gdx.graphics.getWidth() - Gdx.graphics.getWidth() * 0.8f) / 2;

            // Use the same color when drawing
            font.setColor(textColor);
            font.draw(game.getSpriteBatch(), layout, x, currentY);
            currentY -= layout.height + paragraphSpacing;
        }

        // Render space key prompt
        float originalScale = font.getData().scaleX;
        font.getData().setScale(0.8f);
        textColor.a = 1f;  // Ensure skip text is fully visible
        font.setColor(0.7f, 0.7f, 0.7f, 1f);
        String skipText = "Press \"Space\" to skip"; // Message to print
        layout.setText(font, skipText);
        font.draw(game.getSpriteBatch(), skipText, Gdx.graphics.getWidth() - layout.width - 20, 40);
        font.getData().setScale(originalScale);
        game.getSpriteBatch().end();
    }

    /**
     * Disposes of resources used by this screen.
     * This includes the font to prevent memory leaks.
     */

    @Override
    public void dispose() {
        font.getData().setScale(1f);
    }

    // The following methods are part of the Screen interface but are not used in this screen.
    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

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