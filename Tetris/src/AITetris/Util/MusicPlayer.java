package AITetris.Util;

import java.util.Collection;

import AITetris.Model.Properties;
import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MusicPlayer extends Thread {

    private Properties properties;

    Media media = null;
    MediaPlayer player = null;

    boolean isPlay = false;

    Collection<String> medias = FXCollections.observableArrayList("media/테트리스.mp3");

    public MusicPlayer(Properties properties) {

	// JavaFx Media 플레이어 사용을 위한 가상 패널 생성
	JFXPanel panel = new JFXPanel();

	this.properties = properties;

	initMediaPlayer(medias.iterator().next());

	start();

	player.setOnEndOfMedia(() -> {
	    initMediaPlayer("file:/c:/test.mp3");
	});
	
    }
    
    

    private void initMediaPlayer(String path) {

	if (path.equals(""))
	    return;
	try {
	    player = new MediaPlayer(new Media(path));
	    player.setAutoPlay(true);
	    player.setVolume(properties.backgroundVolume);
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    @Override
    public void run() {
	super.run();

	while (true) {

	    try {
		if (isPlay)
		    player.play();
		else
		    player.stop();

		player.setVolume(properties.backgroundVolume);
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	}

    }

    public void changeMusic() {
	media = new Media("file:/c:test.mp3");

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
