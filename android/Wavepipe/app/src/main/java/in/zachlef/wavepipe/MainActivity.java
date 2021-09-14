package in.zachlef.wavepipe;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.material.textfield.TextInputEditText;
import java.net.SocketException;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int bufsize = AudioTrack.getMinBufferSize(UDPStream.SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        AudioTrack audio = new AudioTrack.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build())
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(UDPStream.SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build())
                .setBufferSizeInBytes(bufsize)
                .build();
        audio.play();

        Button btn = findViewById(R.id.button);
        TextView statusText = findViewById(R.id.statusTextView);
        final UDPStream stream = new UDPStream("127.0.0.1",UDPStream.PORT);

        try {
            stream.initSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        final UDPStreamLoop streamLoop = new UDPStreamLoop(stream, audio);

        new Thread(streamLoop).start();

        btn.setOnClickListener(v -> {
            // set udpstream target IP
            stream.setTarget(((TextInputEditText) findViewById(R.id.ipaddr)).getText().toString(), UDPStream.PORT);
            if (streamLoop.isActive()) {
                btn.setText("Open");
                statusText.setText("No Stream");
                streamLoop.setCmd(UDPStream.Command.CLOSE);
            } else {
                btn.setText("Close");
                statusText.setText("Streaming");
                streamLoop.setCmd(UDPStream.Command.OPEN);
            }
        });
    }
}