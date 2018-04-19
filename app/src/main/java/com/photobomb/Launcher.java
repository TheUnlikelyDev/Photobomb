package com.photobomb;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class Launcher extends Activity {

    private ImageView logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        logo = findViewById(R.id.logo);
        doIntroAnimation();

    }

    private void doIntroAnimation(){
        logo.animate().setDuration(1500).alpha(1).setListener(new LogoAnimListener());
    }

    public void doActivityTransition(){
        doPreventReturn();
        startActivity(new Intent(this,SignIn.class));
        finish();
    }

    private void doPreventReturn(){


    }

    private class LogoAnimListener implements Animator.AnimatorListener{

        @Override
        public void onAnimationStart(Animator animator){}
        @Override
        public void onAnimationCancel(Animator animator){}
        @Override
        public void onAnimationRepeat(Animator animator){}
        @Override
        public void onAnimationEnd(Animator animator){
            Launcher.this.doActivityTransition();
        }

    }















}
