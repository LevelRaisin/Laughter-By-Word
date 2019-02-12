package frame;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * <b>GameMusic</b> provides everything required to play background music.
 * <p>
 * This class works by interrupting the currently playing music in favor of a
 * new song.
 * <p>
 * Changes: The class now properly closes {@link Clip clips}, stopping
 * HeapOverflow.
 * <p>
 * Total Time Spent: 1 hour(s).
 * 
 * 
 * @author Daniel Zybine
 * @version 2.0.0.0
 *
 */
public class GameMusic {
	/**
	 * The song currently playing.
	 */
	private static Thread music;

	/**
	 * Whether the song should be playing or not.
	 */
	private volatile static boolean play;

	/**
	 * Starts and restarts a song until a endMusic call is made.
	 * 
	 * @param file
	 *            the file containing the music.
	 */
	private static synchronized void playSound(final File file) {
		music = new Thread(new Runnable() {
			public void run() {
				try {
					Clip clip = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
					clip.open(inputStream);
					clip.start();
					while (play) {
						if (!clip.isActive()) {
							clip.setMicrosecondPosition(0);
							clip.start();
						}
					}
					while (clip.isRunning() || clip.isActive()) {
						clip.stop();
					}
					clip.close();
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		});
	}

	/**
	 * Decides what sound to play based on the level passed.
	 * <p>
	 * 0 - Main Menu
	 * <p>
	 * 1-3 - Game Levels
	 * <p>
	 * 4 - Game Over
	 * 
	 * @param level
	 *            the level for which sound should be played.
	 */
	public static synchronized void playSound(int level) {
		play = true;
		switch (level) {
		case 0:
			GameMusic.playSound(new File("resources/music/MenuMusic.wav"));
			break;
		case 1:
			GameMusic.playSound(new File("resources/music/TempleMusic.wav"));
			break;
		case 2:
			GameMusic.playSound(new File("resources/music/ForestMusic.wav"));
			break;
		case 3:
			GameMusic.playSound(new File("resources/music/CaveMusic.wav"));
			break;
		case 4:
			GameMusic.playSound(new File("resources/music/GameOver.wav"));
			break;
		}
		music.start();
	}

	/**
	 * Stops music by setting play to false.
	 */
	public static synchronized void endMusic() {
		play = false;
	}
}
