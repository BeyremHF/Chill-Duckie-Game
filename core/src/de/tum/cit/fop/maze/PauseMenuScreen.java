package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import static com.badlogic.gdx.Input.Keys.ESCAPE;

/**
 * PauseMenuScreen implements the pause menu functionality for the Maze Runner game.
 * It provides options to resume, restart, access the settings menu, access the levels menu, go back to the main menu, or quit the game.
 */

public class PauseMenuScreen extends BaseScreen {

    /**
     * Creates a new PauseMenuScreen instance.
     * Initializes and arranges all pause menu UI elements.
     *
     * @param game The main game class instance
     */

    public PauseMenuScreen(MazeRunnerGame game) {
        super(game);

        // Create a table for layout
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.setPosition(table.getX(), table.getY() + 120);

        // Add a label for the pause title
        Label pauseLabel = new Label("PAUSE", game.getSkin(), "title");
        table.add(pauseLabel).padBottom(50).row();

        // Create and add menu buttons
        TextButton resumeButton = new TextButton("RESUME", game.getSkin());
        table.add(resumeButton).width(270).padBottom(18).row();
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.popScreen();
            }
        });

        TextButton restartButton = new TextButton("RESTART", game.getSkin());
        table.add(restartButton).width(270).padBottom(18).row();
        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameScreen gameScreen = new GameScreen(game, game.getGameScreen().getCurrentLevel());
                game.setGameScreen(gameScreen);
                game.pushScreen(new CutsceneScreen(game, gameScreen));
                game.pushScreen(gameScreen);
            }
        });

        TextButton settingsButton = new TextButton("SETTINGS", game.getSkin());
        table.add(settingsButton).width(270).padBottom(18).row();
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToSettings();
            }
        });

        TextButton levelsButton = new TextButton("LEVELS", game.getSkin());
        table.add(levelsButton).width(270).padBottom(18).row();
        levelsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToLevelsMenu();
            }
        });

        TextButton mainMenuButton = new TextButton("MAIN MENU", game.getSkin());
        table.add(mainMenuButton).width(270).padBottom(18).row();
        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu();
            }
        });

        TextButton quitButton = new TextButton("QUIT", game.getSkin());
        table.add(quitButton).width(270).row();
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        // Check for ESC key press to unpause the game
        if (Gdx.input.isKeyJustPressed(ESCAPE)) {
            game.popScreen();
        }
    }
}