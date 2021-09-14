package in.zachlef.wavepipe;

import android.media.AudioTrack;

import java.io.IOException;

public class UDPStreamLoop implements Runnable {
    public UDPStreamLoop(final UDPStream stream, AudioTrack audio) {
        this.stream = stream;
        this.active = false;
        this.cmd = null;
        this.audio = audio;
    }

    private final UDPStream stream;
    private boolean active;
    private UDPStream.Command cmd;
    private AudioTrack audio;

    @Override
    public void run() {
        for (;;) {
            if (cmd != null) {
                try {
                    stream.sendCommand(cmd);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (cmd == UDPStream.Command.OPEN) {
                    active = true;
                    cmd = null;
                } else if (cmd == UDPStream.Command.CLOSE) {
                    active = false;
                    cmd = null;
                }
            }

            if (active) {
                try {
                    stream.receiveSample();
                    audio.write(stream.getBuf(), 0, UDPStream.PACKET_SIZE);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isActive() {
        return this.active;
    }

    public void setCmd(UDPStream.Command cmd) {
        this.cmd = cmd;
    }
}
