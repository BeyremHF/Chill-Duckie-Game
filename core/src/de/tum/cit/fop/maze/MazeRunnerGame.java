package de.tum.cit.fop.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;
import java.util.Stack;

/**
 * The core game class for the game.
 * This class extends LibGDX's Game class and serves as the main controller
 * for the entire game, managing screens, resources, and game state.
 *
 * Features:
 * - Screen management using a stack-based system
 * - Resource management (textures, animations, sounds)
 * - Audio control for music and sound effects
 * - Global game state handling
 */
public class MazeRunnerGame extends Game {
    // Screens
    private MenuScreen menuScreen;
    private GameScreen gameScreen;
    private LevelsMenuScreen levelsMenuScreen; // Changed to specific type

    private Stack<Screen> screenStack = new Stack<>(); // Stack to manage screens

    // Sprite Batch for rendering
    private SpriteBatch spriteBatch;

    // UI Skin
    private Skin skin;

    // Character animation downwards
    // Add these private fields to MazeRunnerGame class
    private Animation<TextureRegion> characterUpAnimation;
    private Animation<TextureRegion> characterDownAnimation;
    private Animation<TextureRegion> characterLeftAnimation;
    private Animation<TextureRegion> characterRightAnimation;

    private Music backgroundMusic; // Default volume
    private float musicVolume = 1f; // Default volume
    private float soundEffectVolume = 1f; // Default sound effect volume

    private Sound statueSound;
    private Sound deathSound;
    private Sound victorySound;
    private Sound damageSound;

    /**
     * Creates a new instance of MazeRunnerGame.
     *
     * @param fileChooser The native file chooser implementation for the current platform
     */
    public MazeRunnerGame(NativeFileChooser fileChooser) {
        super();
    }

    /**
     * Initializes the game.
     * Sets up essential resources including:
     * - SpriteBatch for rendering
     * - UI skin
     * - Character animations
     * - Background music
     * - Sound effects
     */
    @Override
    public void create() {
        spriteBatch = new SpriteBatch(); // Create SpriteBatch
        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json")); // Load UI skin
        this.loadCharacterAnimation(); // Load character animation

        //pushScreen(new Level1());

        //Initialize and play background music
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background1.mp3")); // Store the reference
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        // Load sound effects
        statueSound = Gdx.audio.newSound(Gdx.files.internal("SoundEffect1.mp3"));
        deathSound = Gdx.audio.newSound(Gdx.files.internal("SoundEffect5.mp3"));
        victorySound = Gdx.audio.newSound(Gdx.files.internal("SoundEffect8.mp3"));
        damageSound = Gdx.audio.newSound(Gdx.files.internal("SoundEffect9.mp3"));

        pushScreen(new SplashScreen(this)); // Show the logo
    }

    /**
     * Adds a new screen to the top of the screen stack and sets it as active.
     *
     * @param screen The screen to push onto the stack
     */
    public void pushScreen(Screen screen) {
        screenStack.push(screen);
        setScreen(screen);
    }

    /**
     * Removes the top screen from the stack and activates the previous screen.
     * If no screens remain, returns to the main menu.
     */
    public void popScreen() {
        if (!screenStack.isEmpty()) {
            Screen currentScreen = screenStack.pop();
            currentScreen.dispose();  // Dispose the current screen
            if (!screenStack.isEmpty()) {
                setScreen(screenStack.peek());  // Set the previous screen
            } else {
                goToMenu();  // If no screens left, go to main menu
            }
        }
    }

    /**
     * Transitions to the main menu screen.
     * Clears the screen stack and disposes of all active screens.
     */
    public void goToMenu() {
        // Clear the entire stack and dispose all screens properly
        while (!screenStack.isEmpty()) {
            Screen screen = screenStack.pop();
            if (screen != null) {
                screen.dispose();
            }
        }

        // Dispose of any existing menu screen
        if (menuScreen != null) {
            menuScreen.dispose();
        }

        // Create a fresh menu screen
        menuScreen = new MenuScreen(this);
        pushScreen(menuScreen);

        // Dispose and clear other screens
        if (gameScreen != null) {
            gameScreen.dispose();
            gameScreen = null;
        }
        if (levelsMenuScreen != null) {
            levelsMenuScreen.dispose();
            levelsMenuScreen = null;
        }
    }

    /**
     * Transitions to the levels menu screen.
     * Creates a new levels menu while preserving the screen stack.
     */
    public void goToLevelsMenu() {
        // Dispose old levels menu screen if it exists
        if (levelsMenuScreen != null) {
            levelsMenuScreen.dispose();
        }

        // Create and push new levels menu screen
        levelsMenuScreen = new LevelsMenuScreen(this);
        pushScreen(levelsMenuScreen); // Just push the new screen without clearing the stack
    }

    /**
     * Switches to the settings screen.
     */
    public void goToSettings() {
        pushScreen(new SettingsScreen(this)); // Push settings screen onto the stack
    }

    /**
     * Loads the character animation from the character.png file.
     */
    private void loadCharacterAnimation() {
        // Load the sprite sheet
        Texture walkSheet = new Texture(Gdx.files.internal("mobs.png"));

        // Frame dimensions
        int frameWidth = 16;
        int frameHeight = 16;
        int animationFrames = 3;

        // Create animations for each direction
        Array<TextureRegion> downFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> leftFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> rightFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> upFrames = new Array<>(TextureRegion.class);

        // Load frames for each direction
        for (int col = 0; col < animationFrames; col++) {
            // Down animation (row 0)
            downFrames.add(new TextureRegion(walkSheet, 144 + col * frameWidth, 0, frameWidth, frameHeight));

            // Left animation (row 1)
            leftFrames.add(new TextureRegion(walkSheet, 144 + col * frameWidth, frameHeight, frameWidth, frameHeight));

            // Right animation (row 2)
            rightFrames.add(new TextureRegion(walkSheet, 144 + col * frameWidth, frameHeight * 2, frameWidth, frameHeight));

            // Up animation (row 3)
            upFrames.add(new TextureRegion(walkSheet, 144 + col * frameWidth, frameHeight * 3, frameWidth, frameHeight));
        }

        // Create animations
        characterDownAnimation = new Animation<>(0.1f, downFrames);
        characterLeftAnimation = new Animation<>(0.1f, leftFrames);
        characterRightAnimation = new Animation<>(0.1f, rightFrames);
        characterUpAnimation = new Animation<>(0.1f, upFrames);
    }

    // Add getter and setter for gameScreen
    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }


    //Plays the statue collection sound effect.
    public void playStatueSound() {
        if (statueSound != null) {
            statueSound.play(soundEffectVolume);
        }
    }

    //Plays the death sound effect.
    public void playDeathSound() {
        if (deathSound != null) {
            deathSound.play(soundEffectVolume);
        }
    }

    //Plays the victory sound effect.
    public void playVictorySound() {
        if (victorySound != null) {
            victorySound.play(soundEffectVolume);
        }
    }

    //Plays the damage sound effect.
    public void playDamageSound() {
        if (damageSound != null) {
            damageSound.play(soundEffectVolume);
        }
    }

    /**
     * Cleans up and disposes of all game resources.
     * Handles disposal of:
     * - All screens in the stack
     * - SpriteBatch
     * - UI skin
     * - Music and sound effects
     */
    @Override
    public void dispose() {
        // Dispose of the current screen if it exists
        if (getScreen() != null) {
            getScreen().dispose(); // Dispose the current screen
        }

        // Dispose of all screens in the stack
        while (!screenStack.isEmpty()) {
            screenStack.pop().dispose(); // Dispose each screen in the stack
        }

        // Dispose of global resources
        if (spriteBatch != null) {
            spriteBatch.dispose(); // Dispose the spriteBatch
        }
        if (skin != null) {
            skin.dispose(); // Dispose the skin
        }
        if (backgroundMusic != null) {
            backgroundMusic.stop(); // Stop the music if it's playing
            backgroundMusic.dispose(); // Dispose the music resource
        }
        if (statueSound != null) {
            statueSound.dispose();
        }
        if (deathSound != null) {
            deathSound.dispose();
        }
        if (victorySound != null) {
            victorySound.dispose();
        }

    }

    // Getter and setter methods
    public Skin getSkin() {
        return skin;
    }

    public Animation<TextureRegion> getCharacterUpAnimation() {
        return characterUpAnimation;
    }

    public Animation<TextureRegion> getCharacterDownAnimation() {
        return characterDownAnimation;
    }

    public Animation<TextureRegion> getCharacterLeftAnimation() {
        return characterLeftAnimation;
    }

    public Animation<TextureRegion> getCharacterRightAnimation() {
        return characterRightAnimation;
    }

    public float getSoundEffectVolume() {
        return soundEffectVolume;
    }

    public void setSoundEffectVolume(float volume) {
        this.soundEffectVolume = volume;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = volume;
        if (backgroundMusic != null) {
            backgroundMusic.setVolume(musicVolume); // Set the volume of the music
        }
    }
}