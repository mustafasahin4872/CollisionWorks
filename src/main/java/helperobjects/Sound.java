package helperobjects;

import javax.sound.sampled.*;
import java.io.File;

public class Sound {

    private final File file;
    private final boolean looping;
    private Clip clip;

    public Sound(String fileName) {
        this(fileName, false);
    }

    public Sound(String fileName, boolean looping) {
        this.file = new File(fileName);
        this.looping = looping;
        this.clip = null;
    }

    private Clip createClip() throws Exception {
        AudioInputStream audio = AudioSystem.getAudioInputStream(file);
        Clip newClip = AudioSystem.getClip();
        newClip.open(audio);
        return newClip;
    }

    public void playOnce() {
        try {
            if (clip != null && clip.isRunning()) clip.stop();
            clip = createClip();
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loop() {
        try {
            if (clip != null && clip.isRunning()) clip.stop();
            clip = createClip();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (looping) loop();
        else playOnce();
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}