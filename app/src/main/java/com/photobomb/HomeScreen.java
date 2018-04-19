package com.photobomb;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.icu.text.Normalizer2;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Config;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;

public class HomeScreen extends Activity {


    TextView usernameText;
    TextView scoreText;
    ImageView profilePic;
    ImageView userframe;

    FrameLayout profilePicChangerLayout;
    ImageView profilePicPreview;


    Button arenaBut;
    Button battleBut;

    ImageView loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        this.usernameText = findViewById(R.id.usernameTV);
        this.scoreText = findViewById(R.id.scoreTV);
        this.profilePic = (ImageView) findViewById(R.id.profilePic);
        this.arenaBut = findViewById(R.id.textView);
        this.battleBut = findViewById(R.id.battleIV);
        this.profilePicPreview = findViewById(R.id.profilePicPre);
        this.loading = findViewById(R.id.loadingFrame);
        this.userframe = findViewById(R.id.userframe);
        profilePicChangerLayout = findViewById(R.id.frameLayoutProPic);


        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/atomicage.ttf");
        usernameText.setTypeface(typeface);
        scoreText.setTypeface(typeface);
        arenaBut.setTypeface(typeface);
        battleBut.setTypeface(typeface);

      // new LoadInfoToHomeTask().execute();
        new CropProfilePicTask().execute();

     loading.setVisibility(View.INVISIBLE);


    }

    public void setUserInfomation(Object[] userInfoObjects) {

        usernameText.setText((String) userInfoObjects[0]);
        scoreText.setText("Score: " +(String) userInfoObjects[1]);

        if (userInfoObjects[2] != null) {
            Bitmap bm = (Bitmap) userInfoObjects[2];
            //Bitmap newB = Bitmap.createScaledBitmap(bm,150,150,false);
            profilePic.setImageBitmap(bm);
            profilePicPreview.setImageBitmap(bm);
        }
        new CropProfilePicTask().execute();

    }


    public void doFriendButtonPressed(View view){

        startActivity(new Intent(this,AddFriends.class));


    }

    public void doExitPreview(View view){
        profilePicChangerLayout.setVisibility(View.INVISIBLE);
    }

    public void doProfileFramePressed(View view){

        profilePicChangerLayout.setVisibility(View.VISIBLE);


    }

   public void doChangeProfilePictureButtonPressed(View view){
       /**
        * Need to access the gallery via intent then store the result in a file in the application then load this into a preview
        * screen that will allow user to select between the photo
        */
       Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
       photoPickerIntent.setType("image/*");
       startActivityForResult(photoPickerIntent, 1);


   }
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                profilePicPreview.setImageBitmap(selectedImage);
                profilePic.setImageBitmap(selectedImage);
                new CropProfilePicTask().execute();
               // new SaveImageToServer().execute(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }



    public void doBattleButtonPressed(View view){
        doTranstionToBattleScreen();



}

private void doTranstionToBattleScreen(){

        startActivity(new Intent(this,BattleScreen.class));


}



    public void doArenaButtonPressed(View view){
    startActivity(new Intent(this, ArenaScreen.class));
}

    class SaveImageToServer extends AsyncTask<Bitmap,Void,Void> {

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {
            Bitmap newProfilePic = bitmaps[0];
            Socket clientSocket = null;
            try {









                 Log.d("a", "doInBackground:try ");
                 Log.d("a", InetAddress.getByName(NetworkUtils.SERVER_IP) + " ," + NetworkUtils.SERVER_PORT);
                 clientSocket = new Socket(InetAddress.getByName(NetworkUtils.SERVER_IP), NetworkUtils.SERVER_PORT);
                 Log.d("a", "doInBackground:try 1 ");
                 OutputStream out = clientSocket.getOutputStream();
                 BufferedWriter write = new BufferedWriter(new OutputStreamWriter(out));
                 write.write("SAVE_IMAGE\n");
                 write.write( usernameText.getText().toString() +"\n");
                 write.flush();


                 Bitmap bmp = newProfilePic;
                 ByteArrayOutputStream stream = new ByteArrayOutputStream();
                 bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                 byte[] byteArray = stream.toByteArray();//assert png format file
                 Log.d("a", "ByteArraySize:  " + byteArray.length);


                 out.write(byteArray);//********Investigate if possible to reuse connection stream if already wrapped it
                 out.flush();

                 clientSocket.close();




            } catch (IOException exception) {
               exception.printStackTrace();
                Log.d("a", "doInBackground:catch ");
            } finally {
                if (clientSocket != null) {
                    try {
                        clientSocket.close();
                    } catch (IOException exception) {
                    }
                }
            }
            return null;
        }
    }




    class CropProfilePicTask extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... voids) {

            Bitmap bitmap = ((BitmapDrawable) profilePic.getDrawable()).getBitmap();
            return getCircularBitmap(bitmap);


        }

        private Bitmap getCircularBitmap(Bitmap bitmap) {
            Bitmap output;

            if (bitmap.getWidth() > bitmap.getHeight()) {
                output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            } else {
                output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            float r = 0;

            if (bitmap.getWidth() > bitmap.getHeight()) {
                r = bitmap.getHeight() / 2;
            } else {
                r = bitmap.getWidth() / 2;
            }

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(r, r, r, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            profilePic.setImageBitmap(bitmap);
            HomeScreen.this.loading.setVisibility(View.INVISIBLE);
        }
    }

    class LoadInfoToHomeTask extends AsyncTask<Void, Void, Object[]> {

        @Override
        protected Object[] doInBackground(Void... voids) {

            String directory = getFilesDir().getAbsolutePath() + "//UserInfo";

            File userInfo = new File(directory + "//info.txt");
            File profilePicture = new File(directory + "//profile_picture.png");

            Object[] info = retrieveUserInfo(userInfo, profilePicture);


            return info;


        }

        private Object[] retrieveUserInfo(File userInfo, File profilePicture) {
            Object[] info = new Object[3];
            if (userInfo.exists()) {
                try {
                    BufferedReader read = new BufferedReader(new FileReader(userInfo));
                    info[0] = read.readLine();
                    info[1] = read.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (profilePicture.exists()) {
                Log.d("a", "retrieveUserInfo: profilePicuture file exists :" + profilePicture.getTotalSpace());

               BitmapFactory.Options options = new BitmapFactory.Options();
                //options.inJustDecodeBounds = true;
                  BitmapFactory.decodeFile(profilePicture.getAbsolutePath(),options);
                  //options.inSampleSize =  calculateInSampleSize(options, profilePic.getWidth(), profilePic.getHeight());
                options.inSampleSize =2 ;
                Log.d("a", "retrieveUserInfo: inSampleSie = : " + options.inSampleSize);
                 options.inJustDecodeBounds = false;
                Bitmap profileBM = BitmapFactory.decodeFile(profilePicture.getAbsolutePath(), options);

                info[2] = profileBM;
            }
            return  info;
        }


        private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;
                Log.d("a", "height/2 " + height);
                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                Log.d("a", "calculateInSampleSize: halfHeight/inSampleSize, halfWidth/inSampleSize :" + halfHeight +"/" + inSampleSize +"," + halfWidth + "/"+ inSampleSize);
                while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }


        @Override
        protected void onPostExecute(Object[] userInfoObjects) {
            HomeScreen.this.setUserInfomation(userInfoObjects);

        }


    }


}





