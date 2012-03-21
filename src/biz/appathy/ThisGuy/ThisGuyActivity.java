package biz.appathy.ThisGuy;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import java.util.*;

public class ThisGuyActivity extends Activity implements View.OnTouchListener {
    int numThumbs = 0;
    private ImageView backgroundImage = null;
    private TextView textView = null;
    private EditText editText = null;
    private Button button = null;

    /* sound variables */
    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundPoolMap;
    private Set<Integer> loaded_soundIds;
    private final Integer[] TWOTHUMBSOUNDS = {R.raw.thisguy1, R.raw.thisguy2, R.raw.thisguy3, R.raw.thisguy4};
    private final Integer[] ONETHUMBSOUNDS = {R.raw.thisguyi1, R.raw.thisguyi2};
    static final Random random = new Random();
    static final long ONETHUMBSOUNDTIME = 200;
    static final long TEXTRESIZETIME = 100;

    Timer oneThumbSoundPlayerTimer = new Timer();
    Timer textResizeTimer = new Timer();
    Handler textResizeHandler;
    boolean tooBigLastTime = false;

    private int device_height = 533;
    private boolean textChanged = false;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);

        initSounds();
        textView = (TextView) findViewById(R.id.who_has_text);
        editText = (EditText) findViewById(R.id.done_what);
        button = (Button) findViewById(R.id.clear_button);
        backgroundImage = (ImageView) findViewById(R.id.imageView);
        backgroundImage.setVisibility(View.INVISIBLE);
        backgroundImage.setImageResource(R.drawable.cam);
        backgroundImage.setImageResource(R.drawable.thisguy2);
        backgroundImage.setImageResource(R.drawable.whohas);
        //backgroundImage.setVisibility(View.VISIBLE);
        backgroundImage.setOnTouchListener(this);
        textView.setOnTouchListener(this);

        Display display = getWindowManager().getDefaultDisplay();
        try{
            Point size = new Point();
            display.getSize(size);
            device_height = size.y;
        } catch(NoSuchMethodError e) {
            Log.d("thisguy", "display doesn't have getSize method. Trying deprecated method");
            device_height = display.getHeight();
        }
        Log.d("size","device is " + device_height + " high.");

        textResizeHandler = new Handler() {
          @Override
            public void handleMessage(Message msg) {
              float textSize = (Float) msg.obj;
              if (button.getVisibility() == View.VISIBLE) {
                adjustClearButton();
              }
              editText.setTextSize(textSize);
              textView.setTextSize(textSize);
          }
        };
        textResizeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int textHeight = textView.getHeight();
                int editHeight = editText.getHeight();

                if (textHeight > 0 && editHeight > 0) {
                    float textSize = textView.getTextSize();
                    float newTextSize = 0;
                    if (textHeight + editHeight > device_height) {
                        newTextSize = textSize - 1;
                        tooBigLastTime = true;
                    } else if (textHeight + editHeight < device_height) {
                        if (tooBigLastTime && !textChanged) {
                           newTextSize = 0;
                        } else {
                            tooBigLastTime = false;
                            newTextSize = textSize + 1;
                            textChanged = false;
                        }
                    } else {
                        tooBigLastTime = false;
                    }
                    if (newTextSize > 0) {
                        Message msg = new Message();
                        msg.obj = newTextSize;
                        textResizeHandler.sendMessage(msg);
                    }
                }
            }
        }, TEXTRESIZETIME, TEXTRESIZETIME);
        
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("thisguykeys", "edittext clicked.");
                keyboardAppeaered();
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {    

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //To change body of implemented methods use File | Settings | File Templates.
                textChanged = true;
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.d("thisguykeys", "oneditoractionlistener: keyevent null:" + actionId);
                    keyboardDisappeared();
                    Log.d("thisguykeys", "textview visible? " + textView.getVisibility());
                    return false;
                }
                if (keyEvent.getKeyCode() == KeyEvent.ACTION_DOWN) {
                  Log.d("thisguykeys", "action done CALLED. oneditoractionlistener");
                    keyboardDisappeared();
                    return false;
                }
                Log.d("thisguykeys", "oneditoractionlistener:" + keyEvent.getKeyCode() + " int:" + actionId);
                return false;
            }
        });
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    Log.d("thisguykeys", "KEYCODE BACK CALLED. onkeylistener");
                    keyboardDisappeared();
                    return false;
                }
                Log.d("thisguykeys", "onkeylistener:" + keyEvent.getKeyCode() + " int:" + i);
                return false;
            }
        });

    }

    public void keyboardAppeaered() {
        Log.d("thisguy", "keyboard appeared.");
        textView.setVisibility(View.INVISIBLE);
        button.setVisibility(View.VISIBLE);
        adjustClearButton();
        ((FrameLayout.LayoutParams) editText.getLayoutParams()).gravity = Gravity.TOP;
        editText.requestLayout();
        button.requestLayout();
    }
    
    public void keyboardDisappeared() {
        Log.d("thisguy", "keyboard disappeared.");
        textView.setVisibility(View.VISIBLE);
        ((FrameLayout.LayoutParams) editText.getLayoutParams()).gravity = Gravity.BOTTOM;
        editText.setSelected(false);
        button.setVisibility(View.INVISIBLE);
        editText.requestLayout();
    }
    
    public void clearEditText(View view) {
        editText.setText("");
    }

    public void adjustClearButton() {
        Log.d("thisguy", "adjustclearbutton. editTextHeight:" + editText.getHeight());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, editText.getHeight(), 0, 0);
        lp.gravity = Gravity.RIGHT;
        button.setLayoutParams(lp);
    }

    /*@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("Keyboard", "Configuration Changed!");
        if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_NO) {
            Log.d("Keyboard", "keyboard not hidden!");
           textView.setVisibility(View.INVISIBLE);
        } else if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_YES ||
                newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_UNDEFINED) {
            Log.d("Keyboard", "keyboard hidden!");
           textView.setVisibility(View.VISIBLE);

        }
    }*/
    
    @Override
    public void onStop() {
        super.onStop();
        stopTextResizeTimer();
    }

    public void stopTextResizeTimer() {
        textResizeTimer.cancel();
        textResizeTimer.purge();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final int action = motionEvent.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
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
        switch (numThumbs) {
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
        backgroundImage.setVisibility(View.VISIBLE);
        textView.setVisibility(View.INVISIBLE);
        editText.setVisibility(View.INVISIBLE);
        playSound(random.nextInt(TWOTHUMBSOUNDS.length));
    }

    // Was it this guy?
    // One thumb.
    private void this_guy_maybe() {
        backgroundImage.setImageResource(R.drawable.cam);
        backgroundImage.setVisibility(View.VISIBLE);
        textView.setVisibility(View.INVISIBLE);
        editText.setVisibility(View.INVISIBLE);

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
        backgroundImage.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.VISIBLE);
        editText.setVisibility(View.VISIBLE);
        //backgroundImage.setImageResource(R.drawable.whohas);
    }

    // Alien/ friends
    // more than two tumbs!?
    private void more_than_2_thumbs() {
        oneThumbSoundPlayerTimer.cancel();
        backgroundImage.setImageResource(R.drawable.whohas);
        backgroundImage.setVisibility(View.VISIBLE);
        textView.setVisibility(View.INVISIBLE);
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
                Log.d("sound", "LOADED:" + sampleId);
                loaded_soundIds.add(sampleId);
            }
        });

    }

    private void addSound(int index, int soundID) {
        soundPoolMap.put(index, soundPool.load(getBaseContext(), soundID, 1));
    }

    /* not coded by me */
    /* from http://www.androidsnippets.com/playing-sound-fx-for-a-game*/
    public void playSound(int sound) {
        /* Updated: The next 4 lines calculate the current volume in a scale of 0.0 to 1.0 */
        AudioManager mgr = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;

        /* Play the sound with the correct volume */
        Log.d("sound", "playsound:" + soundPoolMap);
        Log.d("sound", "soundpoolmap value:" + soundPoolMap.get(sound));

        if (loaded_soundIds.contains(soundPoolMap.get(sound))) {
            soundPool.play(soundPoolMap.get(sound), volume, volume, 1, 0, 1f);
            Log.d("sound", "SOUND PLAYED:" + sound + " pool#" + soundPoolMap.get(sound));
        } else {
            Log.d("sound", "sound not played. DDDD:" + sound);
        }
    }
}
