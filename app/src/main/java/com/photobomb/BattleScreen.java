package com.photobomb;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class BattleScreen extends Activity {

    ImageView imageFromFrame;
    LinearLayout enlargeFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_screen);


        enlargeFrame = findViewById(R.id.enlargeView);
        imageFromFrame = findViewById(R.id.imageFromFrame);




    }

    public void doExitEnlarge(View view){

        enlargeFrame.setVisibility(View.INVISIBLE);

    }

    public  void doDisplayPic(View view){


        Drawable imageDrawable = ((ImageView) view).getDrawable();
        imageFromFrame.setImageDrawable(imageDrawable);
        enlargeFrame.setVisibility(View.VISIBLE);

    }


    class LoadBattleInfo extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
