package org.bomb.view;

import java.io.*;
import javax.sound.sampled.*;

public class Sound {
	private Clip audioClip;
	private boolean isPlaying = false;

	public Sound() {

	}

	public void playSound(String name) {//Pour les effets sonores
		play(name);
	}

	public void play(String name) {//Pour les th�mes
		try {
			InputStream inputStream = getClass().getResourceAsStream("/Audio/" + name + ".wav");
            assert inputStream != null;
            BufferedInputStream bufferedIn = new BufferedInputStream(inputStream);
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
			AudioFormat format = audioStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			audioClip = (Clip) AudioSystem.getLine(info);
			audioClip.open(audioStream);
			audioClip.start();
			isPlaying = true;
			audioClip.addLineListener(event -> {
				if (event.getType() == LineEvent.Type.STOP) {
					audioClip.close();
					isPlaying = false;
				}
			});
		} catch (UnsupportedAudioFileException | LineUnavailableException | IOException ex) {
			ex.printStackTrace();
		}
	}

	public boolean isStopped() {
		return !isPlaying;
	}

	//Mets fin au th�me
	public void soundEnd() {
		audioClip.stop();
	}
}