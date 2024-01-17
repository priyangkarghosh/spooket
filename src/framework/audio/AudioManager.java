package framework.audio;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Class to manage audio in the game.
 *
 * @author priyangkar ghosh
 */
public class AudioManager {
	
	/** The clips. */
	private static final HashMap<String, File> clips = new HashMap<>() {
		{
			put("shoot", new File("assets\\audio\\shoot.wav"));
			put("music", new File("assets\\audio\\music.wav"));
			put("press", new File("assets\\audio\\press.wav"));
		}
	};
	
	/**
	 * Play.
	 *
	 * @param {String} clipName - the string used to identify the clip
	 * @param {boolean} loop - if the clip should loop
	 */
	public static void play(String clipName, boolean loop) {
		// gets the clip file
	    File audioFile = clips.get(clipName);
	    
		try {
			// gets its input stream
		    AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioFile);
		    
		    // creates a clip object
		    Clip audioClip = AudioSystem.getClip();
			audioClip.open(audioIn);
			
			// if the clip should loop, it loops it
		    if (loop) {
		    	audioClip.loop(Clip.LOOP_CONTINUOUSLY);
		    	return;
		    }
			
		    // otherwise it starts the clip
			audioClip.start();
			
			// waits until the clip is done
			// when it is, it just closes the clip
			audioClip.addLineListener(event -> {
			    if (event.getType() == LineEvent.Type.STOP) {
			        try {
			        	audioClip.close();
						audioIn.close();
					} 
			        
			        catch (IOException e) { }
			    }
			});
		} 
		
		catch (UnsupportedAudioFileException e) { } 
		
		catch (IOException e) { } 
		
		catch (LineUnavailableException e) { }
	}
}
