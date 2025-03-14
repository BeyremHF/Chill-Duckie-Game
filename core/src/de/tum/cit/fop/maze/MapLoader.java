package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import java.io.IOException;
import java.util.*;

/**
 * The MapLoader class handles loading, managing, and rendering game maps for the game.
 * It creates multiple layers of the map including floor tiles, objects, enemies, and special items.
 * This class also handles collision detection and item collection mechanics.
 */
public class MapLoader {
    private static final int TILE_SIZE = 64;
    private static final int SPRITE_SIZE = 16;
    private final Map<Integer, TextureRegion> tileRegions;
    private int[][] baseLayer;  // Floor   layer
    private int[][] objectLayer;  // Walls, items, and objects layer
    private Vector2 startPosition;
    private List<Vector2> exitPositions = new ArrayList<>();
    private int mapWidth;
    private int mapHeight;
    private final Texture spriteSheet1;
    private final Texture spriteSheet2;
    private final Texture spriteSheet3;
    private final Texture spriteSheet4;
    private final Texture spriteSheet5;

    private final TextureRegion floorRegion;
    private boolean statueCollected = false;
    private float heartAnimationTime = 0f;
    private static final float HEART_ANIMATION_SPEED = 2f; // Speed of the pulse
    private static final float HEART_MIN_SCALE = 0.6f; // Minimum scale factor
    private static final float HEART_MAX_SCALE = 0.8f; // Maximum scale factor
    private List<Enemy> enemies;
    private List<Shadow> shadows;
    private static final String[] ENEMY_TYPES = {"ghost","blob", "spider", "bat"};

    /**
     * Initialises a new MapLoader instance with required textures and tile regions.
     * Sets up sprite sheets and initialises collections for enemies and shadows.
     */
    public MapLoader() {
        spriteSheet1 = new Texture(Gdx.files.internal("basictiles.png"));
        spriteSheet2 = new Texture(Gdx.files.internal("basictiles2.png"));
        spriteSheet3 = new Texture(Gdx.files.internal("things.png"));
        spriteSheet5 = new Texture(Gdx.files.internal("tombstone.png"));
        spriteSheet4 = new Texture(Gdx.files.internal("objects.png"));
        tileRegions = new HashMap<>();
        enemies = new ArrayList<>();
        shadows = new ArrayList<>();

        // Initialize texture regions
        floorRegion = new TextureRegion(spriteSheet2, 2 * SPRITE_SIZE, 1 * SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE);
        TextureRegion tombstoneRegion = new TextureRegion(spriteSheet5, 0 * SPRITE_SIZE, 0 * SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE);
        TextureRegion exitRegion = new TextureRegion(spriteSheet2, 2 * SPRITE_SIZE, 6 * SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE);
        TextureRegion keyRegion = new TextureRegion(spriteSheet2, 7 * SPRITE_SIZE, 5 * SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE);
        TextureRegion trapRegion = new TextureRegion(spriteSheet3, 9 * SPRITE_SIZE, 3 * SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE);
        TextureRegion wallRegion = new TextureRegion(spriteSheet2, 6 * SPRITE_SIZE, 3 * SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE);
        TextureRegion buffRegion = new TextureRegion(spriteSheet1, 5 * SPRITE_SIZE, 8 * SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE);
        TextureRegion entryRegion = new TextureRegion(spriteSheet2, 1 * SPRITE_SIZE, 7 * SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE);
        TextureRegion heartRegion = new TextureRegion(spriteSheet4, 63, 0, 15, 17);

        tileRegions.put(-1, floorRegion); // Floor
        tileRegions.put(0, wallRegion);   // Wall
        tileRegions.put(1, entryRegion);
        tileRegions.put(2, exitRegion);   // Exit
        tileRegions.put(3, trapRegion);// Trap
        tileRegions.put(5, keyRegion);    // Key
        tileRegions.put(6, heartRegion);  // Heart
        tileRegions.put(7, buffRegion);   // Buff
        tileRegions.put(9, tombstoneRegion);
    }

    /**
     * Loads a map from a properties file based on the level number.
     * Initialises map dimensions and creates the floor and object layers.
     *
     * @param levelNumber The level number to load (determines which properties file to use)
     */
    public void loadMap(int levelNumber) {
        String fileName = "level-" + levelNumber + ".properties";
        Properties properties = new Properties();

        try {
            properties.load(Gdx.files.internal("maps/" + fileName).reader());

            // Get map dimensions with level-specific defaults
            switch(levelNumber) {
                case 1: mapWidth = Integer.parseInt(properties.getProperty("Width", "15"));
                    mapHeight = Integer.parseInt(properties.getProperty("Height", "15")); break;
                case 2: mapWidth = Integer.parseInt(properties.getProperty("Width", "40"));
                    mapHeight = Integer.parseInt(properties.getProperty("Height", "40")); break;
                case 3: mapWidth = Integer.parseInt(properties.getProperty("Width", "40"));
                    mapHeight = Integer.parseInt(properties.getProperty("Height", "40")); break;
                case 4: mapWidth = Integer.parseInt(properties.getProperty("Width", "80"));
                    mapHeight = Integer.parseInt(properties.getProperty("Height", "80")); break;
                case 5: mapWidth = Integer.parseInt(properties.getProperty("Width", "20"));
                    mapHeight = Integer.parseInt(properties.getProperty("Height", "20")); break;
                default: mapWidth = Integer.parseInt(properties.getProperty("Width", "15"));
                    mapHeight = Integer.parseInt(properties.getProperty("Height", "15"));
            }

            // Initialize both layers
            initializeLayers();

            // Create the base floor layer
            createFloorLayer();

            // Create the object layer (walls, borders, and objects)
            createObjectLayer(properties);

        } catch (IOException e) {
            Gdx.app.error("MapLoader", "Error loading map: " + fileName, e);
            // Load a default empty map
            mapWidth = 15;
            mapHeight = 15;
            initializeLayers();
            createFloorLayer();
        }
    }

    private void initializeLayers() {
        baseLayer = new int[mapWidth][mapHeight];
        objectLayer = new int[mapWidth][mapHeight];

        // Initialize object layer with empty spaces
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                objectLayer[x][y] = -1;
            }
        }
    }

    private void createFloorLayer() {
        // Fill the entire base layer with floor tiles
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                baseLayer[x][y] = -1; // Floor tile
            }
        }
    }

    private void createObjectLayer(Properties properties) {
        // First, create walls around the border
        for (int x = 0; x < mapWidth; x++) {
            objectLayer[x][0] = 0; // Bottom wall
            objectLayer[x][mapHeight - 1] = 0; // Top wall
        }
        for (int y = 0; y < mapHeight; y++) {
            objectLayer[0][y] = 0; // Left wall
            objectLayer[mapWidth - 1][y] = 0; // Right wall
        }

        // Then place all objects from the properties file
        for (String key : properties.stringPropertyNames()) {
            if (key.equals("Width") || key.equals("Height")) continue;

            try {
                String[] coordinates = key.split(",");
                int x = Integer.parseInt(coordinates[0]);
                int y = Integer.parseInt(coordinates[1]);

                if (x >= 0 && x < mapWidth && y >= 0 && y < mapHeight) {
                    int tileType = Integer.parseInt(properties.getProperty(key));
                    objectLayer[x][y] = tileType;

                    // Store special positions
                    if (tileType == 1) {
                        startPosition = new Vector2(x * TILE_SIZE, y * TILE_SIZE);
                    } else if (tileType == 2) { // Exit tile
                        exitPositions.add(new Vector2(x * TILE_SIZE, y * TILE_SIZE));
                    }

                    // create enemies
                    if (tileType == 4) {
                        Random random = new Random();
                        String enemyType = ENEMY_TYPES[random.nextInt(ENEMY_TYPES.length)]; // Randomly pick an enemy type

                        Enemy newEnemy = new Enemy(
                                x * TILE_SIZE,
                                y * TILE_SIZE,
                                new Texture(Gdx.files.internal("mobs.png")),
                                8
                        );

                        newEnemy.createEnemy(enemyType); // Set the enemy type
                        enemies.add(newEnemy);
                    }
                    if (tileType == 8) {
                        Shadow shadowEnemy = new Shadow(
                                x * TILE_SIZE,
                                y * TILE_SIZE,
                                new Texture(Gdx.files.internal("shadow.png"))
                        );

                        shadowEnemy.createShadowEnemy();
                        shadows.add(shadowEnemy);
                    }

                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                Gdx.app.error("MapLoader", "Invalid map data: " + key);
            }
        }
    }

    /**
     * Renders the entire map including floor tiles, objects, enemies, and shadows.
     * Handles special animations for items like hearts.
     *
     * @param game The main game instance containing the SpriteBatch
     * @param playerPosition The current position of the player
     */
    public void renderMap(MazeRunnerGame game, Vector2 playerPosition) {
        if (baseLayer == null || objectLayer == null) return;

        // Update heart animation time
        heartAnimationTime += Gdx.graphics.getDeltaTime();

        // Calculate current heart scale using a sine wave
        float scaleProgress = (float) Math.sin(heartAnimationTime * HEART_ANIMATION_SPEED);
        float currentHeartScale = ((HEART_MAX_SCALE - HEART_MIN_SCALE) * (scaleProgress + 1f) / 2f) + HEART_MIN_SCALE;

        // Render base layer (floor) first
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                game.getSpriteBatch().draw(
                        floorRegion,
                        x * TILE_SIZE,
                        y * TILE_SIZE,
                        TILE_SIZE,
                        TILE_SIZE
                );
            }
        }

        // Render object layer on top
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                int tileType = objectLayer[x][y];
                if (tileType >= 0 && tileRegions.containsKey(tileType)) {
                    if (tileType == 6) { // Heart tile
                        // Calculate center position for scaling
                        float centerX = x * TILE_SIZE + TILE_SIZE / 2f;
                        float centerY = y * TILE_SIZE + TILE_SIZE / 2f;

                        // Calculate scaled dimensions
                        float scaledWidth = TILE_SIZE * currentHeartScale;
                        float scaledHeight = TILE_SIZE * currentHeartScale;

                        // Draw heart with animation
                        game.getSpriteBatch().draw(
                                tileRegions.get(tileType),
                                centerX - scaledWidth / 2f, // Adjust position to maintain center
                                centerY - scaledHeight / 2f,
                                scaledWidth,
                                scaledHeight
                        );
                    } else {
                        // Draw other tiles normally
                        game.getSpriteBatch().draw(
                                tileRegions.get(tileType),
                                x * TILE_SIZE,
                                y * TILE_SIZE,
                                TILE_SIZE,
                                TILE_SIZE
                        );
                    }
                }
            }
        }
        // Render dynamic enemies
        for (Enemy enemy : enemies) {
            enemy.update(Gdx.graphics.getDeltaTime(), playerPosition, this);
            enemy.render(game.getSpriteBatch());
        }

        for (Shadow shadow : shadows) {
            shadow.update(Gdx.graphics.getDeltaTime(), playerPosition);
            shadow.render(game.getSpriteBatch());
        }
    }

    //Getters and Setters
    public Vector2 getStartPosition() {
        return startPosition;
    }

    public List<Vector2> getExitPositions() {
        return exitPositions;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Shadow> getShadows() {
        return shadows;
    }

    /**
     * Checks if a given position contains an exit tile.
     *
     * @param x The x-coordinate to check
     * @param y The y-coordinate to check
     * @return true if the position contains an exit tile, false otherwise
     */
    public boolean isExit(float x, float y) {
        int mapX = (int) (x / TILE_SIZE);
        int mapY = (int) (y / TILE_SIZE);

        if (mapX < 0 || mapX >= mapWidth || mapY < 0 || mapY >= mapHeight) {
            return false;
        }

        return objectLayer[mapX][mapY] == 2;
    }

    /**
     * Checks if a given position contains a trap tile.
     *
     * @param x The x-coordinate to check
     * @param y The y-coordinate to check
     * @return true if the position contains a trap tile, false otherwise
     */
    public boolean isTrap(float x, float y) {
        int mapX = (int) (x / TILE_SIZE);
        int mapY = (int) (y / TILE_SIZE);

        if (mapX < 0 || mapX >= mapWidth || mapY < 0 || mapY >= mapHeight) {
            return false;
        }

        return objectLayer[mapX][mapY] == 3; // 3 is the trap tile type
    }

    /**
     * Checks if a given position contains a statue/key tile.
     *
     * @param x The x-coordinate to check
     * @param y The y-coordinate to check
     * @return true if the position contains a statue tile, false otherwise
     */
    public boolean isStatue(float x, float y) {
        int mapX = (int) (x / TILE_SIZE);
        int mapY = (int) (y / TILE_SIZE);

        if (mapX < 0 || mapX >= mapWidth || mapY < 0 || mapY >= mapHeight) {
            return false;
        }

        return objectLayer[mapX][mapY] == 5; // 5 is the statue/key tile type
    }

    /**
     * Checks if a given position contains a wall tile.
     *
     * @param x The x-coordinate to check
     * @param y The y-coordinate to check
     * @return true if the position contains a wall or is out of bounds, false otherwise
     */
    public boolean isWall(float x, float y) {
        int mapX = (int) (x / TILE_SIZE);
        int mapY = (int) (y / TILE_SIZE);

        if (mapX < 0 || mapX >= mapWidth || mapY < 0 || mapY >= mapHeight) {
            return true; // Consider out of bounds as walls
        }

        return objectLayer[mapX][mapY] == 0 || objectLayer[mapX][mapY] == 9;
    }

    /**
     * Checks if a given position contains a heart powerup.
     *
     * @param x The x-coordinate to check in pixels
     * @param y The y-coordinate to check in pixels
     * @return true if the position contains a heart, false otherwise
     */
    public boolean isHeart(float x, float y) {
        int mapX = (int) (x / TILE_SIZE);
        int mapY = (int) (y / TILE_SIZE);
        return isValidPosition(mapX, mapY) && objectLayer[mapX][mapY] == 6;
    }

    /**
     * Checks if a given position contains a buff powerup.
     *
     * @param x The x-coordinate to check in pixels
     * @param y The y-coordinate to check in pixels
     * @return true if the position contains a buff, false otherwise
     */
    public boolean isBuff(float x, float y) {
        int mapX = (int) (x / TILE_SIZE);
        int mapY = (int) (y / TILE_SIZE);
        return isValidPosition(mapX, mapY) && objectLayer[mapX][mapY] == 7;
    }

    /**
     * Removes a buff powerup from the map at the specified position.
     * Only removes the buff if one exists at the given coordinates.
     *
     * @param x The x-coordinate of the buff to collect in pixels
     * @param y The y-coordinate of the buff to collect in pixels
     */
    public void collectBuff(float x, float y) {
        int mapX = (int) (x / TILE_SIZE);
        int mapY = (int) (y / TILE_SIZE);
        if (isBuff(x, y)) {
            objectLayer[mapX][mapY] = -1;
        }
    }

    /**
     * Checks if given coordinates are within the map boundaries.
     *
     * @param x The x-coordinate to check
     * @param y The y-coordinate to check
     * @return true if the position is within map boundaries, false otherwise
     */
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < mapWidth && y >= 0 && y < mapHeight;
    }

    /**
     * Removes a collected heart from the map at the specified position.
     *
     * @param x The x-coordinate of the heart to collect
     * @param y The y-coordinate of the heart to collect
     */
    public void collectHeart(float x, float y) {
        int mapX = (int) (x / TILE_SIZE);
        int mapY = (int) (y / TILE_SIZE);
        if (isHeart(x, y)) {
            objectLayer[mapX][mapY] = -1;
        }
    }

    /**
     * Removes a collected statue/key from the map and marks it as collected.
     *
     * @param x The x-coordinate of the statue to collect
     * @param y The y-coordinate of the statue to collect
     */
    public void collectStatue(float x, float y) {
        int mapX = (int) (x / TILE_SIZE);
        int mapY = (int) (y / TILE_SIZE);

        if (isStatue(x, y)) {
            statueCollected = true;
            objectLayer[mapX][mapY] = -1;
        }
    }

    /**
     * Checks if the player has collected the statue/key in the current level.
     *
     * @return true if the statue has been collected, false otherwise
     */
    public boolean isStatueCollected() {
        return statueCollected;
    }

    /**
     * Cleans up resources used by the MapLoader.
     * Disposes of all sprite sheets to prevent memory leaks.
     */
    public void dispose() {
        spriteSheet1.dispose();
        spriteSheet2.dispose();
        spriteSheet3.dispose();
    }
}