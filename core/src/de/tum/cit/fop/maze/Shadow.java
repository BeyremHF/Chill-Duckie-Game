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
 * The Shadow class represents the third type of obstacles that reacts to the player's position
 * by changing its animation state. It has two states: sleeping and staring, which are triggered
 * based on the player's relative position to the shadow. If the Player is underneath the Shadow
 * and "looks" at it directly, it will be slowed down
 */
public class Shadow {

    private Vector2 position;
    private Sprite sprite;
    private Animation<TextureRegion> sleepAnimation;
    private Animation<TextureRegion> stareAnimation;
    private float stateTime;
    private static final int TILE_SIZE = 60;
    private static final float ANIMATION_SPEED = 0.4f;
    private static final float RANGE = 300f;
    public boolean isStaring = false;

    /**
     * Creates a new Shadow instance at the specified position.
     *
     * @param positionX The initial X-coordinate of the shadow in pixels
     * @param positionY The initial Y-coordinate of the shadow in pixels
     * @param shadowTexture The texture used for rendering the shadow sprite
     */
    public Shadow(float positionX, float positionY, Texture shadowTexture) {
        this.position = new Vector2(positionX, positionY);

        sprite = new Sprite(shadowTexture);
        sprite.setSize(TILE_SIZE, TILE_SIZE);

        this.stateTime = 0.2f;

    }

    /**
     * Initialises the shadow´s animations using sprite sheets.
     * Sets up both sleeping and staring animation states.
     */
    public void createShadowEnemy() {
        Texture spriteSheet = new Texture(Gdx.files.internal("shadow.png"));

        int frameWidth = 15;
        int frameHeight = 16;

        Array<TextureRegion> sleepFrames = new Array<>();
        Array<TextureRegion> stareFrames = new Array<>();


        sleepFrames.add(new TextureRegion(spriteSheet, 0 * frameWidth, 0 * frameHeight, frameWidth, frameHeight));
        sleepFrames.add(new TextureRegion(spriteSheet, 1 * frameWidth, 0 * frameHeight, frameWidth, frameHeight));

        stareFrames.add(new TextureRegion(spriteSheet, 2 * frameWidth, 0 * frameHeight, frameWidth, frameHeight));
        stareFrames.add(new TextureRegion(spriteSheet, 3 * frameWidth, 0 * frameHeight, frameWidth, frameHeight));
        stareFrames.add(new TextureRegion(spriteSheet, 4 * frameWidth, 0 * frameHeight, frameWidth, frameHeight));

        sleepAnimation = new Animation<>(ANIMATION_SPEED, sleepFrames);
        stareAnimation = new Animation<>(ANIMATION_SPEED, stareFrames);
    }

    /**
     * Renders the shadow on the screen using the appropriate animation frame
     * based on its current state (sleeping or staring).
     *
     * @param spriteBatch The SpriteBatch used for rendering graphics
     */
    public void render(SpriteBatch spriteBatch) {
        Animation<TextureRegion> currentAnimation = isStaring? stareAnimation : sleepAnimation;
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        sprite.setRegion(currentFrame);
        sprite.setPosition(position.x, position.y);
        sprite.draw(spriteBatch);
    }

    /**
     * Checks if the player is positioned above the shadow and within range.
     *
     * @param position The shadow's current position
     * @param playerPosition The player's current position
     * @return true if the player is above the shadow and within range, otherwise false
     */
    public boolean playerAboveShadow(Vector2 position,Vector2 playerPosition) {
        if (position.dst(playerPosition) <= RANGE && position.y > playerPosition.y) {
            return true;
        }
        return false;
    }

    /**
     * Updates the shadow's state based on the player's position and time elapsed.
     * Changes between sleeping and staring states depending on the player's location.
     *
     * @param deltaTime The time elapsed since the last update in seconds
     * @param playerPosition The current position of the player
     */
    public void update(float deltaTime, Vector2 playerPosition) {
        stateTime += deltaTime;
        if (playerAboveShadow(position,playerPosition)) {
            isStaring = true;
        } else {
            isStaring = false;
        }
    }

}