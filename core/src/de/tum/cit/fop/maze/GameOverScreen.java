package de.tum.cit.fop.maze;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * The GameOverScreen class is responsible for the screen that pops up when the player fails a level.
 * It provides options to restart the current level, go to the levels menu, or return to the main menu.
 *
 * <p>Features:
 * <ul>
 *     <li>Restart current level option</li>
 *     <li>Navigation to levels menu and main menu</li>
 * </ul>
 */

public class GameOverScreen extends BaseScreen {

    private final float originalFontScale;

    /**
     * Creates a new GameOverScreen instance.
     *
     * @param game The main game instance
     */

    public GameOverScreen(MazeRunnerGame game) {
        super(game);
        this.originalFontScale = game.getSkin().getFont("font").getScaleX();
        game.getSkin().getFont("font").getData().setScale(1f);

        setupUI();
    }

    /**
     * Sets up the UI components including title and navigation buttons.
     */

    private void setupUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Add title
        table.add(new Label("GAME OVER!", game.getSkin(), "title")).padBottom(80).row();

        // Add restart button
        TextButton restartButton = new TextButton("RESTART", game.getSkin());
        table.add(restartButton).width(270).padBottom(18).row();
        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameScreen gameScreen = new GameScreen(game, game.getGameScreen().getCurrentLevel());
                game.setGameScreen(gameScreen);
                game.pushScreen(gameScreen);
            }
        });

        // Add levels button
        TextButton levelsButton = new TextButton("LEVELS", game.getSkin());
        table.add(levelsButton).width(270).padBottom(18).row();
        levelsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToLevelsMenu();
            }
        });

        // Add main menu button
        TextButton menuButton = new TextButton("MAIN MENU", game.getSkin());
        table.add(menuButton).width(270).row();
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu();
            }
        });

        stage.addActor(table);
    }

    /**
     * Disposes of resources used by this screen.
     */

    @Override
    public void dispose() {
        // Restore original font scale before disposal
        game.getSkin().getFont("font").getData().setScale(originalFontScale);
        super.dispose();
    }
}