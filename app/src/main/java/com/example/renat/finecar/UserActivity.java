package com.example.renat.finecar;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class UserActivity extends AppCompatActivity {


    private SharedPreferences mSettings;
    public static final String APP_PREFERENCES_TOKENID = "tokenId";

    protected TextView mCarListTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mCarListTextView = (TextView) findViewById(R.id.CarListTextVeiw);

        AsyncHttpGetCars asyncGetCar = new AsyncHttpGetCars();
        asyncGetCar.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
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

    public class AsyncHttpGetCars extends AsyncTask<String, String, String>{


        public AsyncHttpGetCars(){   // конструктор
        }



        @Override
        protected String doInBackground(String... params) {

            byte[] result = null;
            String str = "";

            HttpClient client = new DefaultHttpClient();
//            HttpGet request = new HttpGet("https://backfinecar-renatdk.c9.io/api/Cars"+mSettings.getString(APP_PREFERENCES_TOKENID,"tokenId"));
            HttpGet request = new HttpGet("https://backfinecar-renatdk.c9.io/api/Cars");

            HttpResponse response;

            try {
                response = client.execute(request);
                result = EntityUtils.toByteArray(response.getEntity());
                str = new String(result, "UTF-8");
                Log.d("Response of GET request", response.toString());
            } catch (ClientProtocolException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }

            return str;
        }

        @Override
        protected void onPostExecute(String response) {
            mCarListTextView.setText(response);
        }
    }
}
