package org.bomb.view;

import javax.sound.sampled.*;
import java.io.*;

public class Sound {
	private Clip audioClip;
	private boolean isPlaying = false;

	public Sound() {}

	public void playSound(String name) { // For sound effects
		play(name);
	}

	public void play(String name) { // For background music
		soundEnd();  // Stop and clean up any playing sound before starting a new one
		try {
			InputStream inputStream = getClass().getResourceAsStream("/Audio/" + name + ".wav");
			if (inputStream == null) {
				throw new FileNotFoundException("Audio file not found: " + name);
			}

			try (BufferedInputStream bufferedIn = new BufferedInputStream(inputStream);
				 AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn)) {

				AudioFormat format = audioStream.getFormat();
				DataLine.Info info = new DataLine.Info(Clip.class, format);
				audioClip = (Clip) AudioSystem.getLine(info);

				// Open and play the audio
				audioClip.open(audioStream);
				audioClip.start();
				isPlaying = true;

				// Ensure the clip is properly closed when finished
				audioClip.addLineListener(event -> {
					if (event.getType() == LineEvent.Type.STOP) {
						soundEnd(); // Properly close the clip
					}
				});

			} // `audioStream` and `bufferedIn` auto-close here

		} catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isStopped() {
		return !isPlaying;
	}

	// Stops and cleans up the audio clip
	public void soundEnd() {
		if (audioClip != null) {
			audioClip.stop();
			audioClip.close(); // Release system resources
			audioClip = null;  // Allow garbage collection
			isPlaying = false;
		}
	}
}