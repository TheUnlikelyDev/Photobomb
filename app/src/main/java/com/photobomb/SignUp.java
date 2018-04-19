package com.photobomb;

import android.app.Activity;
import android.content.Intent;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;

public class SignUp extends Activity {

    TextView message;
    EditText username;
    EditText password;
    Button  submit;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username = findViewById(R.id.usernameInput);
        password = findViewById(R.id.passwordInput);
        message = findViewById(R.id.signUpMessage);
        submit = findViewById(R.id.submitButton);
       progressBar = findViewById(R.id.pb);


        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/atomicage.ttf");
        username.setTypeface(typeface);
        password.setTypeface(typeface);
        submit.setTypeface(typeface);
        message.setTypeface(typeface);

    }

    public void doSubmitSignUp(View view){


        progressBar.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SignUp.this,"Sign Up Successful!",Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(SignUp.this,SignIn.class));
                progressBar.setVisibility(View.INVISIBLE);
            }
        }, 2000);
        /**
        String user = username.getText().toString();
        String pass = password.getText().toString();

        if(user.equals("") && pass.equals("")){
            doToastPrompt("Username and Password");
        }
        else if(pass.equals("")){
            doToastPrompt("Password");
        }
        else if(user.equals("")){
            doToastPrompt("Username");
        }
        else{

            new SignUpTask().execute(user,pass);

        }
**/

    }

    private void doToastPrompt(String empty){

        Toast.makeText(this,empty + " must be entered.",Toast.LENGTH_LONG).show();

    }

    private void doToastConfirm(){
        Toast.makeText(this,"Sign Up Successful!",Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SignUp.this,SignIn.class));
            }
        }, 2000);

    }
    private void doToastFail(){
        Toast.makeText(this,"Username taken!",Toast.LENGTH_SHORT).show();
    }

    class SignUpTask extends AsyncTask<String,Void,String>{

        @Override
        protected void onPreExecute() {
            //not chaining to super
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String username = strings[0];
            String password = strings[1];
            Socket clientSocket = null;
            try {

                clientSocket = new Socket(InetAddress.getByName(NetworkUtils.SERVER_IP), NetworkUtils.SERVER_PORT);

                clientSocket.setSoTimeout(15000);
                //send sign in request
                OutputStream os = clientSocket.getOutputStream();
                InputStream in = clientSocket.getInputStream();
                BufferedReader read = new BufferedReader(new InputStreamReader(in));
                Writer writer = new OutputStreamWriter(os,"UTF-8");

                writer.write("SIGN_UP\n");
                writer.write(username+"\n");
                writer.write(password+"\n");
                writer.flush();

                String response = read.readLine();


                if(response.equals("USER EXISTS")){
                    return "USER EXISTS";
                }
                else if (response.equals("SUCCESSFUL SIGN UP")){
                    return "SUCCESSFUL SIGN UP";

                }





            }catch (IOException exception){


            }
            finally {
                if(clientSocket != null){
                    try {
                        clientSocket.close();
                    }catch (IOException exception){}
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
              //not changing to super


            progressBar.setVisibility(View.INVISIBLE);
            if(response.equals("USER EXISTS")){
                doToastFail();
            }
            else if(response.equals("SUCCESSFUL SIGN UP")){
                doToastConfirm();
            }

        }
    }
}
