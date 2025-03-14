package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * The Enemy class handles the animation, movement, and interaction of enemies with the player
 * in the game. Each enemy has unique animations for turning and chasing states, and can
 * detect and pursue the player when within range.
 */

public class Enemy {

    private Vector2 position;
    private Sprite sprite;
    private Animation<TextureRegion> turnAnimation;
    private Animation<TextureRegion> chaseAnimation;
    private float stateTime;
    private static final int TILE_SIZE = 60;
    private static final float ANIMATION_SPEED = 0.4f;
    private static final float MOVEMENT_SPEED = 2.9f;
    private static final float RANGE = 320f;
    private boolean isChasing = false;

    /**
     * Creates a new Enemy instance at the specified position.
     *
     * @param positionX    The initial X-coordinate of the enemy in pixels
     * @param positionY    The initial Y-coordinate of the enemy in pixels
     * @param enemyTexture The texture used for rendering the enemy sprite
     * @param numFrames    The number of animation frames for the enemy
     */
    public Enemy(float positionX, float positionY, Texture enemyTexture, int numFrames) {
        this.position = new Vector2(positionX, positionY);


        sprite = new Sprite(enemyTexture);
        sprite.setSize(TILE_SIZE, TILE_SIZE);

        this.stateTime = 0.2f;

    }

    /**
     * Creates animations for different types of enemies based on the provided enemy type.
     * This method initialises the turning and chasing animations using sprite sheets.
     *
     * @param enemyType The type of enemy to create. Valid values are:
     *                 <ul>
     *                     <li>"ghost" - A ghost enemy </li>
     *                     <li>"blob" - A blob enemy </li>
     *                     <li>"spider" - A spider enemy </li>
     *                     <li>"bat" - A flying bat </li>
     *                 </ul>
     * @throws IllegalArgumentException if the provided enemyType is not recognised
     */
    public void createEnemy(String enemyType) {
        Texture spriteSheet = new Texture(Gdx.files.internal("mobs.png"));

        int frameWidth = 16;
        int frameHeight = 16;

        Array<TextureRegion> turnFrames = new Array<>();
        Array<TextureRegion> chaseFrames = new Array<>();

        // Determine which enemy type to render
        switch (enemyType) {
            case "ghost":
                turnFrames.add(new TextureRegion(spriteSheet, 6 * frameWidth, 4 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 7 * frameWidth, 4 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 6 * frameWidth, 5 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 7 * frameWidth, 5 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 6 * frameWidth, 7 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 7 * frameWidth, 7 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 6 * frameWidth, 6 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 7 * frameWidth, 6 * frameHeight, frameWidth, frameHeight));

                chaseFrames.add(new TextureRegion(spriteSheet, 6 * frameWidth, 4 * frameHeight, frameWidth, frameHeight));
                chaseFrames.add(new TextureRegion(spriteSheet, 7 * frameWidth, 4 * frameHeight, frameWidth, frameHeight));
                break;

            case "blob":
                turnFrames.add(new TextureRegion(spriteSheet, 0 * frameWidth, 4 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 1 * frameWidth, 4 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 0 * frameWidth, 5 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 1 * frameWidth, 5 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 0 * frameWidth, 7 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 1 * frameWidth, 7 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 0 * frameWidth, 6 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 1 * frameWidth, 6 * frameHeight, frameWidth, frameHeight));

                chaseFrames.add(new TextureRegion(spriteSheet, 0 * frameWidth, 4 * frameHeight, frameWidth, frameHeight));
                chaseFrames.add(new TextureRegion(spriteSheet, 1 * frameWidth, 4 * frameHeight, frameWidth, frameHeight));
                break;

            case "spider":
                turnFrames.add(new TextureRegion(spriteSheet, 9 * frameWidth, 4 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 10 * frameWidth, 4 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 9 * frameWidth, 5 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 10 * frameWidth, 5 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 9 * frameWidth, 7 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 10 * frameWidth, 7 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 9 * frameWidth, 6 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 10 * frameWidth, 6 * frameHeight, frameWidth, frameHeight));

                chaseFrames.add(new TextureRegion(spriteSheet, 9 * frameWidth, 4 * frameHeight, frameWidth, frameHeight));
                chaseFrames.add(new TextureRegion(spriteSheet, 10 * frameWidth, 4 * frameHeight, frameWidth, frameHeight));
                break;

            case "bat":
                turnFrames.add(new TextureRegion(spriteSheet, 3 * frameWidth, 4 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 4 * frameWidth, 4 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 3 * frameWidth, 5 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 4 * frameWidth, 5 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 3 * frameWidth, 7 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 4 * frameWidth, 7 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 3 * frameWidth, 6 * frameHeight, frameWidth, frameHeight));
                turnFrames.add(new TextureRegion(spriteSheet, 4 * frameWidth, 6 * frameHeight, frameWidth, frameHeight));

                chaseFrames.add(new TextureRegion(spriteSheet, 3 * frameWidth, 4 * frameHeight, frameWidth, frameHeight));
                chaseFrames.add(new TextureRegion(spriteSheet, 4 * frameWidth, 4 * frameHeight, frameWidth, frameHeight));
                break;

            default:
                throw new IllegalArgumentException("Unknown enemy type: " + enemyType);
        }

        // Create animations
        chaseAnimation = new Animation<>(ANIMATION_SPEED, chaseFrames);
        turnAnimation = new Animation<>(ANIMATION_SPEED, turnFrames);
    }


    /**
     * Renders the enemy on the screen using the appropriate animation frame.
     *
     * @param spriteBatch The SpriteBatch used for rendering graphics
     */
    public void render(SpriteBatch spriteBatch) {
        Animation<TextureRegion> currentAnimation = isChasing ? chaseAnimation : turnAnimation;
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        sprite.setRegion(currentFrame);
        sprite.setPosition(position.x, position.y);
        sprite.draw(spriteBatch);
    }

    /**
     * Checks if the enemy is in contact with the player.
     *
     * @param playerPosition The current position of the player as a Vector2
     * @return true if the player is within 2/3 of a tile size of the enemy, false otherwise
     */

    public boolean isTouchingPlayer(Vector2 playerPosition) {
        float distance = position.dst(playerPosition);
        //the threshold is where we consider them touching
        float threshold = (float) (TILE_SIZE / 2);

        return distance < threshold;
    }

    /**
     * Updates the enemy's state and position based on the player's location and time elapsed.
     *
     * @param deltaTime      The time elapsed since the last update in seconds
     * @param playerPosition The current position of the player
     * @param mapLoader      The MapLoader instance used to check for collisions
     */
    public void update(float deltaTime, Vector2 playerPosition, MapLoader mapLoader) {
        if (playerIsinRange(playerPosition)) {
            move(playerPosition, mapLoader);
            isChasing = true;
        } else {
            isChasing = false;
        }
        stateTime += deltaTime;
    }

    /**
     * Determines if the player is within the enemy's detection range.
     *
     * @param playerPosition The current position of the player
     * @return true if the player is within RANGE units of the enemy, false otherwise
     */

    public boolean playerIsinRange(Vector2 playerPosition) {
        float distance = position.dst(playerPosition);
        return distance < RANGE;
    }


    /**
     * Controls the enemy's movement based on player position and map obstacles.
     *
     * @param playerPosition The current position of the player
     * @param mapLoader      The MapLoader used to check for collisions with map elements
     */

    public void move(Vector2 playerPosition, MapLoader mapLoader) {
        if (playerIsinRange(playerPosition)) {
            chasePlayer(playerPosition, mapLoader);
        }
    }

    /**
     * Lets the enemy follow the player while avoiding obstacles
     *
     * @param playerPosition the current position of the player
     * @param mapLoader used to check for map obstacles
     */

    private void chasePlayer(Vector2 playerPosition, MapLoader mapLoader) {
        Vector2 directionToPlayer = new Vector2(playerPosition.x - position.x, playerPosition.y - position.y);
        directionToPlayer.nor();
        float nextX = position.x + directionToPlayer.x * MOVEMENT_SPEED;
        float nextY = position.y + directionToPlayer.y * MOVEMENT_SPEED;

        if (!(mapLoader.isWall(nextX, nextY) || mapLoader.isTrap(nextX, nextY) || mapLoader.isStatue(nextX, nextY) || mapLoader.isExit(nextX, nextY))) {
            position.add(directionToPlayer.x * MOVEMENT_SPEED, directionToPlayer.y * MOVEMENT_SPEED);
        }
    }
}