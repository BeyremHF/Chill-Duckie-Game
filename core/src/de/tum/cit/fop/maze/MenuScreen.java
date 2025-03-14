package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * The MenuScreen class is responsible for displaying the main menu of the game.
 * It extends the LibGDX Screen class and sets up the UI components for the menu.
 * Through this screen, you can navigate options to start the game, provides access to the levels menu, settings menu, and quit the game.
 * The screen also features a background image.
 */

public class MenuScreen extends BaseScreen {

    /**
     * Constructor for MenuScreen. Sets up the camera, viewport, stage, background, and UI elements including:
     *       - Title label "READY?"
     *       - START button to begin the game (level 1)
     *       - LEVELS button to access level selection menu
     *       - SETTINGS button to access the settings menu
     *       - QUIT button to exit the application
     * <p>
     * All UI elements are arranged in a centered vertical layout.
     *
     * @param game The main game class, used to access global resources and methods.
     */

    public MenuScreen(MazeRunnerGame game) {
        super(game);

        // Create a table for layout
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.setPosition(0, 90);

        // Add the "READY?" label as a title
        table.add(new Label("READY?", game.getSkin(), "title")).padBottom(80).row();

        // Create and add a button to start the game
        TextButton goToGameButton = new TextButton("START", game.getSkin());
        table.add(goToGameButton).width(270).padBottom(18).row();
        goToGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameScreen gameScreen = new GameScreen(game); // No need for the level number parameter, the default level is 1
                game.setGameScreen(gameScreen);
                game.pushScreen(gameScreen); // First push the game screen
                game.pushScreen(new CutsceneScreen(game, gameScreen)); // Then push cutscene on top
            }
        });

        // Create and add a button to access the levels menu
        TextButton goToGameButton1 = new TextButton("LEVELS", game.getSkin());
        table.add(goToGameButton1).width(270).padBottom(18).row();
        goToGameButton1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToLevelsMenu(); // Navigate to the LevelsMenuScreen
            }
        });

        // Create and add a button to access the settings screen
        TextButton goToGameButton2 = new TextButton("SETTINGS", game.getSkin());
        table.add(goToGameButton2).width(270).padBottom(18).row();
        goToGameButton2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToSettings(); // Navigate to the SettingsScreen
            }
        });

        // Create and add a button to quit the application
        TextButton goToGameButton3 = new TextButton("QUIT", game.getSkin());
        table.add(goToGameButton3).width(270).row();
        goToGameButton3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit(); // Close the application when the quit button is pressed
            }
        });

        // Add the table to the stage
        stage.addActor(table);
    }
}
