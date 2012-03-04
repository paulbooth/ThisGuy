package biz.appathy.ThisGuy;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class ThisGuyActivity extends Activity implements View.OnTouchListener
{
    int numThumbs = 0;
    private ImageView backgroundImage = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        backgroundImage = (ImageView) findViewById(R.id.imageView);
        backgroundImage.setVisibility(View.INVISIBLE);
        backgroundImage.setImageResource(R.drawable.thumb1);
        backgroundImage.setImageResource(R.drawable.thisguy2);
        backgroundImage.setImageResource(R.drawable.who        backgroundImage.setVisibility(View.VISIBLE);
has);
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
        backgroundImage.setImageResource(R.drawable.thisguy2);
    }

    // Was it this guy?
    // One thumb.
    private void this_guy_maybe() {
        backgroundImage.setImageResource(R.drawable.thumb1);
    }

    // Not this guy.
    // no thumbs
    private void not_this_guy() {
        backgroundImage.setImageResource(R.drawable.whohas);
    }

    // Alien/ friends
    // more than two tumbs!?
    private void more_than_2_thumbs() {
        backgroundImage.setImageResource(R.drawable.whohas);
         Toast toast = Toast.makeText(getApplicationContext(),
                 "Thumbs: " + numThumbs,
                 Toast.LENGTH_SHORT);
        toast.show();
    }

}
