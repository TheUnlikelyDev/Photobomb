package com.photobomb;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;

public class AddFriends extends Activity {

    EditText friendSearchEDT;
    ImageView findPropic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        this.friendSearchEDT = findViewById(R.id.findFriendEDT);
        this.findPropic = findViewById(R.id.friendProPic);

    }

    public void doSearchSubmit(View view) {

        String friendUsername = friendSearchEDT.getText().toString();
        if (friendUsername.equals("")) {
            Toast.makeText(this, "Enter a friends username into the field ", Toast.LENGTH_SHORT).show();
        } else {
            //progress bar + check server
            new FindUserTask().execute(friendUsername);

        }

    }
    public void doSendFriendPressed(View view){
        String friend = friendSearchEDT.getText().toString();
        Log.d("a", "doSendFriendPressed: 1");
        new SendFriendRequest().execute(friend);

    }

    class SendFriendRequest extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... usernames) {
          String username = usernames[0];
            Socket clientSocket = null;
            try {

                clientSocket = new Socket(InetAddress.getByName(NetworkUtils.SERVER_IP), NetworkUtils.SERVER_PORT);
                OutputStream out = clientSocket.getOutputStream();
                InputStream in = clientSocket.getInputStream();

                OutputStreamWriter write = new OutputStreamWriter(out);
                write.write("FRIEND_REQUEST\n");
                write.write("morri93\n");
                write.write(username + "\n");
                write.flush();





            }catch (IOException e){
                Log.d("a", "doSendFriendPressed: catch");

            }
            finally {
                try {
                    if(clientSocket != null) {
                        clientSocket.close();
                    }
                }catch (IOException e){}

            }
            return null;
        }
    }


    class FindUserTask extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... usernames) {

            String username = usernames[0];


            Socket clientSocket = null;
            try {

                clientSocket = new Socket(InetAddress.getByName(NetworkUtils.SERVER_IP), NetworkUtils.SERVER_PORT);
                OutputStream out = clientSocket.getOutputStream();
                InputStream in = clientSocket.getInputStream();

                OutputStreamWriter write = new OutputStreamWriter(out);
                write.write("LOAD_IMAGES" + "\n");
                write.write(username + "\n");
                write.flush();
                //will get back nothing if hasnt select profile pic

                //get profile pic
                Bitmap bmp = BitmapFactory.decodeStream(in);
                return  bmp;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            AddFriends.this.findPropic.setImageBitmap(bitmap);






        }
    }


}
