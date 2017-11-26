package AITetris.Util;

import java.util.ListIterator;

import AITetris.Model.Properties;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MusicPlayer extends Thread {

    public enum Music {
	tetris, tetris1, tetris2, tetris3, tetris4, tetris_help, tetris_rimix, tetris_rimix1, tetris_title
    }

    private Properties properties;
    private JFXPanel panel = null;

    Media media = null;
    MediaPlayer player = null;

    boolean isPlay = false;

    String url = "https://alcoholcoding.com/music/tetris/";

    ObservableList<String> medias = FXCollections.observableArrayList(
	    Music.tetris + ".mp3",
	    Music.tetris1 + ".mp3", 
	    Music.tetris2 + ".mp3", 
	    Music.tetris3 + ".mp3", 
	    Music.tetris4 + ".mp3",
	    Music.tetris_help + ".mp3", 
	    Music.tetris_rimix + ".mp3", 
	    Music.tetris_rimix1 + ".mp3",
	    Music.tetris_title + ".mp3");
    ListIterator<String> listMusic;

    public MusicPlayer(Properties properties) {

	// JavaFx Media 플레이어 사용을 위한 가상 패널 생성
	try {
	    panel = new JFXPanel();

	    this.properties = properties;

	    listMusic = medias.listIterator();

	    start();

	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    private void initMediaPlayer(String path) {

	if (path.equals(""))
	    return;
	try {
	    if (player != null) {
		player.setOnEndOfMedia(null);
		player.stop();
		player = null;
	    }

	    player = new MediaPlayer(new Media(path));
	    player.setAutoPlay(true);
	    player.setVolume(properties.backgroundVolume);

	    player.setOnEndOfMedia(() -> {
		initMediaPlayer(url + listMusic.next());
	    });

	    player.play();

	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    @Override
    public void run() {
	super.run();

	while (true) {

	    try {

		if (player != null) {
		    if (isPlay)
			player.play();
		    else
			player.stop();

		    player.setVolume(properties.backgroundVolume);
		}

	    } catch (Exception e) {
		e.printStackTrace();
	    }

	}

    }

    public void changeMusic() {

	if (!listMusic.hasNext())
	    listMusic = medias.listIterator();

	initMediaPlayer(url + listMusic.next());
    }

    public void changeMusic(int select) {
	initMediaPlayer(url + medias.get(select));
    }

    public void changeMusic(Music select) {
	initMediaPlayer(url + select + ".mp3");
    }

    public void play() {
	isPlay = true;
    }

    public void pause() {
	isPlay = false;
    }

    public boolean isPlay() {
	return isPlay;
    }

}
