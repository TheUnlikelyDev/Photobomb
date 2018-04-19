package com.photobomb;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;

public class SignIn extends Activity {

    TextView signInmesg;

    EditText usernameInput;
    EditText passwordInput;

    Button submit;
    Button signUp;

    ProgressBar pb2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signInmesg = findViewById(R.id.signInMessage);

        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        submit = findViewById(R.id.submit);
        signUp = findViewById(R.id.signup);
        pb2 = findViewById(R.id.pb2);


        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/atomicage.ttf");
        submit.setTypeface(typeface);
        usernameInput.setTypeface(typeface);
        signInmesg.setTypeface(typeface);
        signUp.setTypeface(typeface);
        passwordInput.setTypeface(typeface);
    }


    public void doSignUpPressed(View view){
        doTranstionToSignUp();
    }

    /**
     * Case 1: no input
     * Case 2: no username input
     * Case 3: no password input
     * Case 4: no password || username input
     * Case 5: valid input
     * @param view
     */
    public void doSubmitPressed(View view){

        pb2.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                doTranstitionToHomeScreen();
            }
        }, 2000);



  //doSignInValidation();


    }

    private void doSignInValidation(){

        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        if(username.equals("") && password.equals("")){
            doToastPrompt("Username and Password");
        }
        else if(password.equals("")){
            doToastPrompt("Password");
        }
        else if(username.equals("")){
            doToastPrompt("Username");
        }
        else{

            new SignInTask().execute(username,password);

        }

    }

    private void doToastPrompt(String empty){

        Toast.makeText(this,empty + " must be entered.",Toast.LENGTH_LONG).show();

    }

    private void doTranstionToSignUp(){
     Intent toSignUp = new Intent(this,SignUp.class);
      startActivity(toSignUp);
    }

    private void doToastResponse(String resp){


        Toast.makeText(this,resp,Toast.LENGTH_SHORT).show();


    }

    private void doTranstitionToHomeScreen(){


       startActivity(new Intent(this,HomeScreen.class));
       finish();

    }



    class SignInTask extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            //chain to super removed
            pb2.setVisibility(View.VISIBLE);


        }

        @Override
        protected String doInBackground(String... strings) {

            String username = strings[0];
            String password = strings[1];
            Socket clientSocket = null;
            try {
                clientSocket = new Socket(InetAddress.getByName(NetworkUtils.SERVER_IP), NetworkUtils.SERVER_PORT);

                clientSocket.setSoTimeout(38000);
                //send sign in request
                OutputStream os = clientSocket.getOutputStream();
                InputStream in = clientSocket.getInputStream();
                BufferedReader read = new BufferedReader(new InputStreamReader(in));

                Writer writer = new OutputStreamWriter(os, "UTF-8");

                writer.write("SIGN_IN\n");
                writer.write(username + "\n");
                writer.write(password + "\n");
                writer.flush();


                String response = read.readLine();
                return response;
            } catch (IOException exception) {


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

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String response) {
            //chain to super removed

            doToastResponse(response);
            if (response.equals("SUCCESSFUL SIGN IN")) {
                new LoadUserTask().execute();
            } else if (response.equals("INCORRECT PASSWORD")) {
                pb2.setVisibility(View.INVISIBLE);
                doToastPrompt("Incorrect password");
            } else {
                assert false;
            }

        }


    }





    class LoadUserTask extends AsyncTask<Void,Void,Bitmap>{




            @Override
            protected Bitmap doInBackground(Void... voids) {

                Socket socket;

                try {
                    Log.d("a", "TRY ");
                    socket = new Socket(InetAddress.getByName(NetworkUtils.SERVER_IP),NetworkUtils.SERVER_PORT);

                    InputStream in = socket.getInputStream();
                    OutputStream out = socket.getOutputStream();


                    String user = usernameInput.getText().toString();
                    String pass = passwordInput.getText().toString();

                    BufferedWriter write = new BufferedWriter(new OutputStreamWriter(out));
                    write.write("LOAD_USER\n");
                    write.write(user + "\n");
                    write.flush();

                    BufferedReader read = new BufferedReader(new InputStreamReader(in));
                    String username = read.readLine();
                    String score = read.readLine();
                    Log.d("a", "USERNAME/SCORE" + username + "/" + score);


                    in.close();

                    //second pass
                    socket = new Socket(InetAddress.getByName(NetworkUtils.SERVER_IP),NetworkUtils.SERVER_PORT);
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    writer.write("LOAD_IMAGES\n");
                    writer.write(username + "\n");
                    writer.flush();

                    Bitmap profilePicBM = BitmapFactory.decodeStream(socket.getInputStream());

                    Log.d("a", "ProfilePicBM :" + profilePicBM);





                    //save info to file.
                    //1.create user directory
                    File userDir = new File(getFilesDir(),"UserInfo");
                    userDir.mkdir();
                    String userDirPath = userDir.getAbsolutePath();

                    //2.save username + score
                    File userInfoFile = new File(userDirPath + "//info.txt");
                    BufferedWriter bf = new BufferedWriter(new FileWriter(userInfoFile));

                    bf.write(username + "\n");//new line or escape?
                    bf.write(score + "\n");
                    bf.flush();//needed to flush buffers

                    //3.save images

                    File profilePic = new File(userDirPath + "//profile_picture.png");
                    FileOutputStream fileOutputStream = new FileOutputStream(profilePic);
                    Log.d("a", "doInBackground: " + fileOutputStream.toString());

                    if(profilePicBM != null) {
                        profilePicBM.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    }














                }catch(IOException e){
                    Log.d("a", "CATCH");
                }


                return null;
            }


            @Override
            protected void onPostExecute(Bitmap bitmap) {
                pb2.setVisibility(View.INVISIBLE);
                doTranstitionToHomeScreen();



            }
        }



    }











