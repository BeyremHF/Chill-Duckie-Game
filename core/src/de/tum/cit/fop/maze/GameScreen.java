package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.List;

/**
 * GameScreen class implements the main gameplay screen of the game.
 * This screen handles the core gameplay mechanics including character movement,
 * collision detection, rendering, and game state management.
 *
 * The screen manages:
 * - Character movement and animation
 * - Camera controls and zooming
 * - Lives system and damage handling
 * - Status effects (damage, healing, buffs)
 * - HUD elements (health, statue status, timer)
 * - Collision detection with walls, traps, and exits
 * - Game state transitions
 */
public class GameScreen implements Screen {
    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private final BitmapFont font;
    private static final float DEFAULT_FONT_SCALE = 1.0f;

    // Character position and movement
    private float characterX = 400; // Starting X position
    private float characterY = 300; // Starting Y position
    public Vector2 playerPosition = new Vector2(characterX, characterY);
    private float movementSpeed = 200; // Pixels per second

    // Character rendering constants
    private static final float CHARACTER_SIZE = 64; // Base size for character

    // Camera zoom settings
    private final float MIN_ZOOM = 0.5f;
    private final float MAX_ZOOM = 1.1f;
    private final float ZOOM_SPEED = 0.1f;
    private static float lastZoomLevel = 0.75f;  // Add this static field to persist zoom across instances

    // Animation state tracking
    private float stateTime = 0; // Tracks time for animations
    private Direction currentDirection = Direction.DOWN; // Tracks current facing direction
    private boolean isMoving = false; // Tracks if character is moving
    public boolean isFacingUp = false; // Tracks where the Player is facing to

    // Enum for tracking character direction
    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private final MapLoader mapLoader;
    private final int currentLevel;

    private static final int MAX_LIVES = 3;
    private int currentLives;
    private TextureRegion fullHeartTexture;
    private TextureRegion emptyHeartTexture;
    private final OrthographicCamera hudCamera;

    private static final float HEART_SIZE_PERCENTAGE = 0.05f; // 5% of screen height
    private static final float HEART_SPACING_PERCENTAGE = 0.01f; // 1% of screen height
    private static final float HUD_MARGIN_PERCENTAGE = 0.02f; // 2% of screen height

    private float heartSize;
    private float heartSpacing;
    private float hudMargin;

    // Damage effect
    private boolean isDamaged = false;
    private float damageTimer = 0;
    private static final float DAMAGE_DURATION = 1.0f; // Duration in seconds
    private static final float DAMAGE_RED_TINT = 0.7f; // How red the tint should be (0-1)

    // Healing effect
    private boolean isHealed = false;
    private float healTimer = 0;
    private static final float HEAL_DURATION = 1.0f; // Duration in seconds
    private static final float HEAL_GREEN_TINT = 0.7f; // How green the tint should be (0-1)

    //Slowing down effect
    private boolean isSlowedDown = false;
    private float slowTimer = 0;
    private static final float SLOW_DURATION = 1.0f;
    private static final float SLOW_CYAN_TINT = 0.7f;


    private float trapDamageCooldown = 0f;
    private static final float TRAP_DAMAGE_COOLDOWN_DURATION = 1.5f; // Seconds between trap damage
    private boolean isInvulnerable = false;

    private static final float SPRINT_MULTIPLIER = 1.28f; // Speed multiplier when sprinting

    private boolean hasBuffActive = false;
    private float buffTimer = 0;
    private static final float BUFF_DURATION = 3.0f;
    private static final float BUFF_GOLD_TINT = 0.7f;

    //Timer
    private float elapsedTime = 0;

    private DirectionIndicator directionIndicator;

    /**
     * Basic constructor that initialises the game screen with default level 1.
     *
     * @param game The main game instance that manages the game state
     */
    public GameScreen(MazeRunnerGame game) {
        this(game, 1); // Default to level 1
    }

    /**
     * Constructor that initialises the game screen with a specific level.
     *
     * @param game The main game instance that manages the game state
     * @param levelNumber The level number to load
     */
    public GameScreen(MazeRunnerGame game, int levelNumber) {
        this.game = game;
        this.currentLevel = levelNumber;

        // Initialize map loader and get start position
        mapLoader = new MapLoader();
        mapLoader.loadMap(levelNumber);

        // Set initial character position to map start position
        Vector2 startPos = mapLoader.getStartPosition();
        if (startPos != null) {
            characterX = startPos.x;
            characterY = startPos.y;
        }

        directionIndicator = new DirectionIndicator();

        // Create and configure the camera with proper aspect ratio
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, w, w * (h / w)); // Maintain aspect ratio
        camera.zoom = lastZoomLevel;  // Use the persistent zoom level

        // Get the font from the game's skin
        font = game.getSkin().getFont("font");

        // Set default font scale when creating game screen
        font.getData().setScale(DEFAULT_FONT_SCALE);

        // Set up input processor for scroll handling
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                float newZoom = camera.zoom + (amountY * ZOOM_SPEED);
                lastZoomLevel = Math.min(MAX_ZOOM, Math.max(MIN_ZOOM, newZoom));  // Store zoom in static field
                camera.zoom = lastZoomLevel;
                return true;
            }
        });

        // Initialize lives system
        currentLives = MAX_LIVES;

        // Load heart textures from sprite sheet
        Texture objectsTexture = new Texture(Gdx.files.internal("objects.png"));
        fullHeartTexture = new TextureRegion(objectsTexture, 63, 0, 15, 17);
        emptyHeartTexture = new TextureRegion(objectsTexture, 127, 0, 15, 17);

        // Create separate camera for HUD
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.update();

        updateHUDSizes();
    }


    /**
     * Updates the sizes of HUD elements based on the current screen dimensions.
     * Calculates heart size, spacing, and margins as percentages of screen height.
     */
    private void updateHUDSizes() {
        float screenHeight = Gdx.graphics.getHeight();
        heartSize = screenHeight * HEART_SIZE_PERCENTAGE;
        heartSpacing = screenHeight * HEART_SPACING_PERCENTAGE;
        hudMargin = screenHeight * HUD_MARGIN_PERCENTAGE;
    }

    /**
     * Renders a single frame of the game.
     * Handles all rendering operations including:
     * - Map rendering
     * - Character animation and movement
     * - Status effects
     * - HUD elements
     * - Timer updates
     * - Input processing
     *
     * @param delta The time in seconds since the last render
     */
    @Override
    public void render(float delta) {
        // Clear the screen first
        ScreenUtils.clear(0, 0, 0, 1);

        //Update the elapsed time
        elapsedTime += delta;

        // Update animation state time
        stateTime += delta;

        // Update damage, heal, and buff timers...
        if (isDamaged) {
            damageTimer += delta;
            if (damageTimer >= DAMAGE_DURATION) {
                isDamaged = false;
                damageTimer = 0;
            }
        }

        if (isSlowedDown) {
            slowTimer += delta;
            if (slowTimer >= SLOW_DURATION) {
                isSlowedDown = false;
                slowTimer = 0;
            }
        }

        // Update heal timer if character is healed
        if (isHealed) {
            healTimer += delta;
            if (healTimer >= HEAL_DURATION) {
                isHealed = false;
                healTimer = 0;
            }
        }

        if (hasBuffActive) {
            buffTimer += delta;
            if (buffTimer >= BUFF_DURATION) {
                hasBuffActive = false;
                buffTimer = 0;
                isInvulnerable = false;
            }
        }

        // Handle character movement and update animation state
        handleInput(delta);

        // Update game camera position to follow character
        camera.position.set(characterX, characterY, 0);
        camera.update();

        // First render the game world with game camera
        game.getSpriteBatch().setProjectionMatrix(camera.combined);
        game.getSpriteBatch().begin();

        // Render map
        mapLoader.renderMap(game, playerPosition);

        // Update direction indicator
        directionIndicator.update(delta);

        // Update and render direction indicator if statue is collected
        if (mapLoader.isStatueCollected()) {
            directionIndicator.update(delta);
            directionIndicator.render(
                    game.getSpriteBatch(),
                    new Vector2(characterX, characterY),
                    mapLoader.getExitPositions()
            );
        }

        // Get and draw the character animation frame
        TextureRegion currentFrame = getAnimationFrame();

        // Apply tints for effects
        if (isDamaged) {
            float tintFactor = 1 - (damageTimer / DAMAGE_DURATION);
            game.getSpriteBatch().setColor(1f, 1f - (DAMAGE_RED_TINT * tintFactor), 1f - (DAMAGE_RED_TINT * tintFactor), 1f);
        } else if (isHealed) {
            float tintFactor = 1 - (healTimer / HEAL_DURATION);
            game.getSpriteBatch().setColor(1f - (HEAL_GREEN_TINT * tintFactor), 1f, 1f - (HEAL_GREEN_TINT * tintFactor), 1f);
        } else if (hasBuffActive) {
            float tintFactor = 1 - (buffTimer / BUFF_DURATION);
            game.getSpriteBatch().setColor(1f, 1f - (BUFF_GOLD_TINT * tintFactor), 0f, 1f);
        } else if (isSlowedDown) {
            float tintFactor = 1 - (slowTimer / SLOW_DURATION);
            game.getSpriteBatch().setColor(0f, 1f - (BUFF_GOLD_TINT * tintFactor), 1f, 1f);
        }

        // Draw character
        game.getSpriteBatch().draw(
                currentFrame,
                characterX - CHARACTER_SIZE / 2,
                characterY - CHARACTER_SIZE / 2,
                CHARACTER_SIZE,
                CHARACTER_SIZE
        );

        // Reset color back to normal
        game.getSpriteBatch().setColor(1, 1, 1, 1);

        game.getSpriteBatch().end();

        // Then render all HUD elements with HUD camera
        game.getSpriteBatch().setProjectionMatrix(hudCamera.combined);
        game.getSpriteBatch().begin();

        // Render lives
        renderLives();

        // Render statue status
        String statueStatus = "STATUE: " + (mapLoader.isStatueCollected() ? "COLLECTED" : "NOT COLLECTED");
        BitmapFont font = game.getSkin().getFont("font");
        font.setColor(mapLoader.isStatueCollected() ? 0.0f : 1.0f,
                mapLoader.isStatueCollected() ? 1.0f : 0.0f,
                0.0f, 1.0f);

        // Position the statue status text in the top-left corner with padding
        float padding = 20; // Padding from screen edges
        float textX = padding;
        float textY = hudCamera.viewportHeight - padding;

        font.draw(game.getSpriteBatch(), statueStatus, textX, textY);
        font.setColor(1.0f, 1.0f, 1.0f, 1.0f); // Reset color

        game.getSpriteBatch().end();

        // Then render the HUD with HUD camera
        game.getSpriteBatch().setProjectionMatrix(hudCamera.combined);
        game.getSpriteBatch().begin();
        renderLives();

        // Display timer in HUD
        String timerText = String.format("Time: %.1f s", elapsedTime);
        font.draw(game.getSpriteBatch(), timerText,
                hudCamera.viewportWidth - 200,
                hudCamera.viewportHeight - hudMargin);

        game.getSpriteBatch().end();

        // Check for damage and heal input
        if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
            takeDamage();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            heal();
        }

        // Update trap damage cooldown
        if (isInvulnerable && !hasBuffActive) {
            trapDamageCooldown -= delta;
            if (trapDamageCooldown <= 0) {
                isInvulnerable = false;
            }
        }
    }

    /**
     * Renders the lives display in the HUD.
     * Draws heart icons representing the current and maximum lives.
     */
    private void renderLives() {
        float x = hudMargin;
        float y = hudMargin;

        for (int i = 0; i < MAX_LIVES; i++) {
            TextureRegion heartTexture = i < currentLives ? fullHeartTexture : emptyHeartTexture;
            game.getSpriteBatch().draw(
                    heartTexture,
                    x,
                    y,
                    heartSize,
                    heartSize
            );
            x += heartSize + heartSpacing;
        }
    }

    /**
     * Handles damage taken by the player.
     * Reduces lives, applies damage effects, and handles game over condition.
     * Also manages invulnerability frames and sound effects.
     */
    public void takeDamage() {
        if (!isInvulnerable && currentLives > 0) {
            currentLives--;
            isDamaged = true;
            damageTimer = 0;

            // Play damage sound only if character is still alive after taking damage
            if (currentLives != 0) {
                game.playDamageSound();
            }

            // Only set invulnerability if we're not already buffed
            if (!hasBuffActive) {
                isInvulnerable = true;
                trapDamageCooldown = TRAP_DAMAGE_COOLDOWN_DURATION;
            }

            if (currentLives == 0) {
                game.playDeathSound(); // Play death sound
                game.pushScreen(new GameOverScreen(game));
            }
        }
    }

    /**
     * Handles healing of the player.
     * Increases lives if below maximum and applies healing visual effect.
     */
    public void heal() {
        if (currentLives < MAX_LIVES) {
            currentLives++;
            isHealed = true;
            healTimer = 0;
        }
    }

    /**
     * Activates a buff effect on the player.
     * Provides temporary invulnerability and visual effects.
     */
    private void activateBuff() {
        hasBuffActive = true;
        buffTimer = 0;
        isInvulnerable = true;
        trapDamageCooldown = 0; // Reset any existing damage cooldown
    }


    /**
     * Processes keyboard input and updates character position.
     * Handles movement, collision detection, and interaction with game elements.
     *
     * @param delta Time since last frame in seconds
     */
    private void handleInput(float delta) {
        // Check if shift is pressed for sprint
        float currentSpeed = movementSpeed * (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)
                ? SPRINT_MULTIPLIER : 1.0f);

        float movement = currentSpeed * delta;
        float newX = characterX;
        float newY = characterY;

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.pushScreen(new PauseMenuScreen(game));
            return;
        }

        if (currentLives == 0) {
            game.pushScreen(new GameOverScreen(game));
            return;
        }

        isMoving = false;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            newX = characterX - movement;
            currentDirection = Direction.LEFT;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            newX = characterX + movement;
            currentDirection = Direction.RIGHT;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            newY = characterY - movement;
            currentDirection = Direction.DOWN;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            newY = characterY + movement;
            currentDirection = Direction.UP;
            isMoving = true;
            isFacingUp = true;}
        else {
            isFacingUp = false;
        }

        boolean wouldBeAtExit = mapLoader.isExit(newX, characterY) || mapLoader.isExit(characterX, newY);
        boolean canPassExit = mapLoader.isStatueCollected();

        playerPosition.set(newX, newY);

        if (!mapLoader.isWall(newX, characterY) && (!wouldBeAtExit || canPassExit)) {
            characterX = newX;
        }
        if (!mapLoader.isWall(characterX, newY) && (!wouldBeAtExit || canPassExit)) {
            characterY = newY;
        }

        // Check if character has stepped on a trap
        if (mapLoader.isTrap(characterX, characterY)) {
            takeDamage();
        }

        if (mapLoader.isHeart(characterX, characterY) && currentLives < MAX_LIVES) {
            heal();
            mapLoader.collectHeart(characterX, characterY);
        }

        // Check if character has collected the statue
        if (mapLoader.isStatue(characterX, characterY)) {
            mapLoader.collectStatue(characterX, characterY);
            game.playStatueSound(); // Play statue collection sound
        }

        // Check if character has reached the exit
        if (mapLoader.isExit(characterX, characterY) && mapLoader.isStatueCollected()) {
            game.playVictorySound(); // Play victory sound
            game.pushScreen(new VictoryScreen(game, elapsedTime)); // Pass elapsedTime to VictoryScreen constructor
        }

        List<Enemy> enemies = mapLoader.getEnemies();

        for (Enemy enemy : enemies){
            if (enemy.isTouchingPlayer(playerPosition)) {
               takeDamage();
            }
        }
        List<Shadow> shadows = mapLoader.getShadows();

        boolean shadowIsWatching = false;


        for (Shadow shadow : shadows){
            if(isFacingUp && shadow.isStaring) {
                slowDown();
                shadowIsWatching = true;
                break;
            }
        }

        if(!shadowIsWatching){
            resetSpeed();
        }

        // Check if character has stepped on a buff tile
        if (mapLoader.isBuff(characterX, characterY)) {
            activateBuff();
            mapLoader.collectBuff(characterX, characterY);
        }
    }

    /**
     * Resets the player's movement speed to default value.
     */
    public void resetSpeed(){
        movementSpeed = 200f;
        isSlowedDown = false;
    }

    /**
     * Reduces player's movement speed (used for shadow effect).
     */
    public void slowDown(){
        movementSpeed = 100f;
        isSlowedDown = true;
    }


    public int getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Gets the current animation frame for the character based on direction and movement state.
     *
     * @return The current TextureRegion to render for the character
     */
    private TextureRegion getAnimationFrame() {
        Animation<TextureRegion> currentAnimation;

        switch (currentDirection) {
            case UP:
                currentAnimation = game.getCharacterUpAnimation();
                break;
            case DOWN:
                currentAnimation = game.getCharacterDownAnimation();
                break;
            case LEFT:
                currentAnimation = game.getCharacterLeftAnimation();
                break;
            case RIGHT:
                currentAnimation = game.getCharacterRightAnimation();
                break;
            default:
                currentAnimation = game.getCharacterDownAnimation();
        }

        return currentAnimation.getKeyFrame(isMoving ? stateTime : 0, true);
    }

    /**
     * Handles screen resize events.
     * Updates camera and HUD dimensions to maintain proper scaling.
     *
     * @param width The new screen width
     * @param height The new screen height
     */
    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = width * (height / (float)width);
        camera.zoom = lastZoomLevel;  // Maintain zoom level during resize
        camera.update();

        hudCamera.viewportWidth = width;
        hudCamera.viewportHeight = height;
        hudCamera.position.set(width / 2f, height / 2f, 0);
        hudCamera.update();

        updateHUDSizes();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    /**
     * Called when this screen becomes the current screen.
     * Sets up input processing and resets font scaling.
     */
    @Override
    public void show() {
        // Update input processor with current zoom level
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                float newZoom = camera.zoom + (amountY * ZOOM_SPEED);
                lastZoomLevel = Math.min(MAX_ZOOM, Math.max(MIN_ZOOM, newZoom));
                camera.zoom = lastZoomLevel;
                return true;
            }
        });

        // Ensure font scale is reset when screen is shown
        font.getData().setScale(DEFAULT_FONT_SCALE);
    }

    @Override
    public void hide() {}

    //dispose of resources when the screen is destroyes
    @Override
    public void dispose() {
        mapLoader.dispose();
        directionIndicator.dispose();
    }
}