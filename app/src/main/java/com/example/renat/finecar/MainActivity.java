package com.example.renat.finecar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    protected   EditText mEmailEditText;
    protected   EditText mPasswordEditText;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_EMAIL = "email";
    public static final String APP_PREFERENCES_PASSWORD = "password";
    public static final String APP_PREFERENCES_TOKENID = "tokenId";
    public static final String APP_PREFERENCES_USERID = "userId";

    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEmailEditText = (EditText) findViewById(R.id.emailEditText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if(mSettings.contains(APP_PREFERENCES_EMAIL)){
            mEmailEditText.setText(mSettings.getString(APP_PREFERENCES_EMAIL,"email"));
            mPasswordEditText.setText(mSettings.getString(APP_PREFERENCES_PASSWORD,"password"));
        }
    }

    public class AsyncLogin extends AsyncTask<String, String, String> {

        public String showResult;

        private HashMap<String, String> mData = null; // Данные отправки

        // конструктор

        public AsyncLogin(HashMap<String,String> data){
            mData = data;
        }

        // фоновая работа

        @Override
        protected String doInBackground(String... params) {

            byte[] result = null;
            String str = "";

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(params[0]); // params[0] это URL к которому будет отправляться запрос

            try{
                ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
                Iterator<String> it = mData.keySet().iterator();
                while (it.hasNext()){
                    String key = it.next();
                    nameValuePair.add(new BasicNameValuePair(key, mData.get(key)));
                }

                post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
                HttpResponse response = client.execute(post);
                StatusLine statusLine = response.getStatusLine();

                if(statusLine.getStatusCode()== HttpURLConnection.HTTP_OK){
                    result = EntityUtils.toByteArray(response.getEntity());
                    str = new String(result, "UTF-8");
                }

            }
            catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
            catch (Exception e){
            }
            return str;
        }

        @Override
        protected void onPostExecute(String resultData) {
            showResult = resultData;
            Context context = getApplicationContext();
            CharSequence text = showResult;
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            try {
                JSONObject obj = new JSONObject(showResult);
                String tokenId = obj.getString("id");
                String userId = obj.getString("userid");

                SharedPreferences.Editor editor = mSettings.edit();                                 // сохраняем данные в SharedPrefernces
                editor.putString(APP_PREFERENCES_TOKENID, tokenId);
                editor.putString(APP_PREFERENCES_USERID, userId);
                editor.apply();

            }catch (JSONException e){
                e.printStackTrace();
            }

            SharedPreferences.Editor editor = mSettings.edit();                                     // сохраняем данные в SharedPrefernces
            editor.putString(APP_PREFERENCES_EMAIL, mEmailEditText.getText().toString());
            editor.putString(APP_PREFERENCES_PASSWORD, mPasswordEditText.getText().toString());
            editor.apply();

            if(showResult != ""){
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                startActivity(intent);
            }

        }
    }


    public void loginBtnClick(View view){
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("email", mEmailEditText.getText().toString());
//        data.put("email", "renatd.k@gmail.com");
        data.put("password", mPasswordEditText.getText().toString());
//        data.put("password", "123456");

        System.out.println(data);

        AsyncLogin asyncLogin = new AsyncLogin(data);
        asyncLogin.execute("https://backfinecar-renatdk.c9.io:443/api/fUsers/login");

    }

}
