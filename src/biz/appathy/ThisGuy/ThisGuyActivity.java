package biz.appathy.ThisGuy;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.*;

public class ThisGuyActivity extends Activity implements View.OnTouchListener
{
    int numThumbs = 0;
    private ImageView backgroundImage = null;

    /* sound variables */
    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundPoolMap;
    private Set<Integer> loaded_soundIds;
    private final Integer[] TWOTHUMBSOUNDS = {R.raw.thisguy1, R.raw.thisguy2, R.raw.thisguy3, R.raw.thisguy4};
    private final Integer[] ONETHUMBSOUNDS = {R.raw.thisguyi1, R.raw.thisguyi2};
    static final Random random = new Random();
    static final long ONETHUMBSOUNDTIME = 200;
    
    Timer oneThumbSoundPlayerTimer = new Timer();
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);

        initSounds();
        backgroundImage = (ImageView) findViewById(R.id.imageView);
        backgroundImage.setVisibility(View.INVISIBLE);
        backgroundImage.setImageResource(R.drawable.cam);
        backgroundImage.setImageResource(R.drawable.thisguy2);
        backgroundImage.setImageResource(R.drawable.whohas);
        backgroundImage.setVisibility(View.VISIBLE);
        backgroundImage.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final int action = motionEvent.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:{
                    numThumbs++;
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_UP: {
                    numThumbs--;
                break;
            }
            default:
                return false;
        }
        Log.d("Thumbs", "thumbs:" + numThumbs);
        switch (numThumbs){
             case 2:
                 this_guy_yeah();
                 break;
            case 1:
                this_guy_maybe();
                break;
            case 0:
                not_this_guy();
                break;
            default:
                more_than_2_thumbs();
                break;
        }  
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    // YEAH THIS GUY!
    // TWO THUMBS!!
    private void this_guy_yeah() {
        oneThumbSoundPlayerTimer.cancel();
        backgroundImage.setImageResource(R.drawable.thisguy2);
        playSound(random.nextInt(TWOTHUMBSOUNDS.length));
    }

    // Was it this guy?
    // One thumb.
    private void this_guy_maybe() {
        backgroundImage.setImageResource(R.drawable.cam);
        oneThumbSoundPlayerTimer.cancel();
        oneThumbSoundPlayerTimer.purge();
        oneThumbSoundPlayerTimer = new Timer();
        oneThumbSoundPlayerTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                playSound(TWOTHUMBSOUNDS.length + random.nextInt(ONETHUMBSOUNDS.length));
            }
        }, ONETHUMBSOUNDTIME);
    }

    // Not this guy.
    // no thumbs
    private void not_this_guy() {
        oneThumbSoundPlayerTimer.cancel();
        
        backgroundImage.setImageResource(R.drawable.whohas);
    }

    // Alien/ friends
    // more than two tumbs!?
    private void more_than_2_thumbs() {
        oneThumbSoundPlayerTimer.cancel();
        backgroundImage.setImageResource(R.drawable.whohas);
         Toast toast = Toast.makeText(getApplicationContext(),
                 "Thumbs: " + numThumbs,
                 Toast.LENGTH_SHORT);
        toast.show();
    }

    private void initSounds() {

        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPoolMap = new HashMap<Integer, Integer>();
        loaded_soundIds = new HashSet<Integer>();
        for (int i = 0; i < TWOTHUMBSOUNDS.length; i++) {
            addSound(i, TWOTHUMBSOUNDS[i]);
        }
        for (int i = 0; i < ONETHUMBSOUNDS.length; i++) {
            addSound(i + TWOTHUMBSOUNDS.length, ONETHUMBSOUNDS[i]);
        }
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                Log.d("sound","LOADED:" + sampleId);
                loaded_soundIds.add(sampleId);
            }
        });

    }

    private void addSound(int index, int soundID)
    {
        soundPoolMap.put(index, soundPool.load(getBaseContext(), soundID, 1));
    }
    /* not coded by me */
    /* from http://www.androidsnippets.com/playing-sound-fx-for-a-game*/
    public void playSound(int sound) {
        /* Updated: The next 4 lines calculate the current volume in a scale of 0.0 to 1.0 */
        AudioManager mgr = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;

        /* Play the sound with the correct volume */
        Log.d("sound","playsound:" + soundPoolMap);
        Log.d("sound","soundpoolmap value:" + soundPoolMap.get(sound));

        if (loaded_soundIds.contains(soundPoolMap.get(sound))) {
            soundPool.play(soundPoolMap.get(sound), volume, volume, 1, 0, 1f);
            Log.d("sound", "SOUND PLAYED:" + sound + " pool#" + soundPoolMap.get(sound));
        } else {
            Log.d("sound", "sound not played. DDDD:" + sound);
        }
    }
}
