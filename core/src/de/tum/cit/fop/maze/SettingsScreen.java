package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * SettingsScreen handles the game settings interface.
 * Provides controls for adjusting music volume, sound effects volume
 * and the option to toggle fullscreen mode, to the player's preference.
 */

public class SettingsScreen extends BaseScreen {
    private Label titleLabel;

    /**
     * Creates a new SettingsScreen instance.
     * Sets up the UI elements for adjusting game settings.
     *
     * @param game The main game class instance
     */

    public SettingsScreen(MazeRunnerGame game) {
        super(game);

        // Create main layout tables
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Table table1 = new Table();
        table1.setFillParent(true);
        table1.padTop(400);

        // Setup title label
        titleLabel = new Label("SETTINGS", game.getSkin(), "title");
        titleLabel.setPosition(
                Gdx.graphics.getWidth() / 2 - titleLabel.getWidth() / 2,
                Gdx.graphics.getHeight() - 100
        );
        stage.addActor(titleLabel);

        // Music Volume Slider
        Slider volumeSlider = new Slider(0, 1, 0.01f, false, game.getSkin());
        volumeSlider.setValue(game.getMusicVolume());
        table.add(new Label("Music Volume: ", game.getSkin())).padRight(100).padBottom(50);
        table.add(volumeSlider).width(400).padBottom(40).row();
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setMusicVolume(volumeSlider.getValue());
            }
        });

        // Sound Effects Volume Slider
        Slider soundEffectSlider = new Slider(0, 1, 0.01f, false, game.getSkin());
        soundEffectSlider.setValue(game.getSoundEffectVolume());
        table.add(new Label("Sound Effects Volume: ", game.getSkin())).padRight(100).padBottom(50);
        table.add(soundEffectSlider).width(400).padBottom(40).row();
        soundEffectSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setSoundEffectVolume(soundEffectSlider.getValue());
            }
        });

        // Add fullscreen toggle button
        TextButton borderlessButton = new TextButton("FULLSCREEN", game.getSkin());
        table1.add(borderlessButton).width(270).padBottom(30).row();
        borderlessButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (Gdx.graphics.isFullscreen()) {
                    Gdx.graphics.setWindowedMode(1400, 900);
                } else {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                }
            }
        });

        // Add back button
        TextButton backButton = new TextButton("BACK", game.getSkin());
        table1.add(backButton).width(270).padBottom(20).row();
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.popScreen();
            }
        });

        // Add tables to stage
        stage.addActor(table);
        stage.addActor(table1);
    }

    /**
     * Handles screen resize events by repositioning the title label.
     *
     * @param width The new screen width
     * @param height The new screen height
     */

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        titleLabel.setPosition(
                width / 2f - titleLabel.getWidth() / 2f,
                height - 250
        );
    }
}