package de.tum.cit.fop.maze;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * VictoryScreen displays when a player completes a level.
 * Shows the player's score and provides navigation options for proceeding to the next level, access the level menu, or go back to the main menu.
 */

public class VictoryScreen extends BaseScreen {
    private static final int MAX_LEVEL = 5;
    private final int currentLevel;
    private final float elapsedTime;
    private final int score;

    /**
     * Creates a new VictoryScreen instance.
     * Sets up the victory screen UI with score display and navigation options.
     *
     * @param game The main game class instance
     * @param elapsedTime The time taken to complete the level
     */

    public VictoryScreen(MazeRunnerGame game, float elapsedTime) {
        super(game);
        this.elapsedTime = elapsedTime;
        this.currentLevel = game.getGameScreen() != null ? // Calculate the player's score based on the elapsed time
                game.getGameScreen().getCurrentLevel() : 1;
        this.score = (int)(100000 / elapsedTime);

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Add victory message and score
        table.add(new Label("VICTORY!", game.getSkin(), "title")).padBottom(80).row();
        table.add(new Label("Your Score: " + score, game.getSkin(), "title")).padBottom(80).row();

        // Add next level button if not on final level
        if (currentLevel < MAX_LEVEL) {
            TextButton nextLevelButton = new TextButton("NEXT LEVEL", game.getSkin());
            table.add(nextLevelButton).width(270).padBottom(18).row();
            nextLevelButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    GameScreen nextGameScreen = new GameScreen(game, currentLevel + 1);
                    game.setGameScreen(nextGameScreen);
                    game.pushScreen(nextGameScreen);
                }
            });
        }

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
}