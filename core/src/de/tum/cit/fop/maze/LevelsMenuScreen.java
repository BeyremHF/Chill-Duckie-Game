package de.tum.cit.fop.maze;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * LevelsMenuScreen displays the level selection menu for the Maze Runner game.
 * It allows the player to choose between different game levels and navigate back to the main menu too.
 */

public class LevelsMenuScreen extends BaseScreen {

    /**
     * Creates a new LevelsMenuScreen instance.
     * Sets up the UI elements including level selection buttons and layout.
     *
     * @param game The main game class instance
     */

    public LevelsMenuScreen(MazeRunnerGame game) {
        super(game);

        // Create and set up the table
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Add title
        table.add(new Label("CHOOSE YOUR LEVEL", game.getSkin(), "title")).padBottom(80).row();

        // Add buttons for each level
        TextButton level1Button = new TextButton("LEVEL 1", game.getSkin());
        table.add(level1Button).width(270).padBottom(18).row();
        level1Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameScreen gameScreen = new GameScreen(game, 1);
                game.setGameScreen(gameScreen);
                game.pushScreen(gameScreen);
                game.pushScreen(new CutsceneScreen(game, gameScreen));
            }
        });

        TextButton level2Button = new TextButton("LEVEL 2", game.getSkin());
        table.add(level2Button).width(270).padBottom(18).row();
        level2Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setGameScreen(new GameScreen(game, 2));
                game.pushScreen(game.getGameScreen());
            }
        });

        TextButton level3Button = new TextButton("LEVEL 3", game.getSkin());
        table.add(level3Button).width(270).padBottom(18).row();
        level3Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setGameScreen(new GameScreen(game, 3));
                game.pushScreen(game.getGameScreen());
            }
        });

        TextButton level4Button = new TextButton("LEVEL 4", game.getSkin());
        table.add(level4Button).width(270).padBottom(18).row();
        level4Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setGameScreen(new GameScreen(game, 4));
                game.pushScreen(game.getGameScreen());
            }
        });

        TextButton level5Button = new TextButton("LEVEL 5", game.getSkin());
        table.add(level5Button).width(270).padBottom(18).row();
        level5Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setGameScreen(new GameScreen(game, 5));
                game.pushScreen(game.getGameScreen());
            }
        });

        // Add a "Back" button
        TextButton backButton = new TextButton("BACK", game.getSkin());
        table.add(backButton).width(270).row();
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.popScreen();
            }
        });

        stage.addActor(table);
    }
}