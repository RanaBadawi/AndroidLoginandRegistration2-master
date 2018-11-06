package activity;

import helper.SQLiteHandler;
import helper.SessionManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.example.user.androidloginandregistration.MapsActivity;
import com.example.user.androidloginandregistration.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private TextView txtName;
    private TextView txtEmail;
    private Button btnLogout;
    private Button currentLocation;
    private Button sort;
    ListView listView;
    ArrayList<JSONObject> JsonArrayAsL;
    public static final String TAG = "gwa";
    private SQLiteHandler db;
    private SessionManager session;
    private String latitudeValue;
    private String longitudeValue;
    Object[] heroes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        currentLocation = (Button) findViewById(R.id.currentLocation);
        sort = (Button) findViewById(R.id.sort);
        JsonArrayAsL = new ArrayList<JSONObject>();

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);
        listView = (ListView) findViewById(R.id.listView);
        getJSON("http://172.16.0.67/myapi/api.php");
        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });


        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(JsonArrayAsL, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject jsonObjectA, JSONObject jsonObjectB) {
                        int compare = 0;
                        try {
                            Double keyA = jsonObjectA.getDouble("rating");
                            Double keyB = jsonObjectB.getDouble("rating");
                            compare = Double.compare(keyA, keyB);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return compare;
                    }
                });

                try {
                    addlist();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//            @Override
//            public void onItemClick(AdapterView<?>adapter,View v, int position, long id){
//                ItemClicked item = adapter.getItemAtPosition(position);
//
//                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
//                //based on item add info to intent
//                startActivity(intent);
//            }
//        });


        //        JSONObject obj = JsonArrayAsL.get(m);

//                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
//
//                try {
//                    intent.putExtra(latitudeValue, obj.getString("latitude"));
//                    intent.putExtra(longitudeValue, obj.getString("longitude"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                startActivity(intent);


    }


    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void getJSON(final String urlWebService) {

        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                try {

                    loadIntoListView(s);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    private void loadIntoListView(String json) throws JSONException {
        try {
//        JSONObject test= new JSONObject(json);
            String sss = json.substring(1, json.length() - 1);
            Log.d(TAG, "elstring" + sss);
            JSONArray jsonArray = new JSONArray(sss);
            Log.d(TAG, "testatat" + jsonArray.toString());
            heroes = new Object[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
//                heroes[i] = obj.getString("Name")+" "+obj.getString("rating");
                JsonArrayAsL.add(obj);
            }

            for (int i = 0; i < JsonArrayAsL.size(); i++) {
                JSONObject obj = JsonArrayAsL.get(i);
                heroes[i] = obj.getString("Name") + " " + obj.getString("rating");


            }


            ArrayAdapter<Object> arrayAdapter = new ArrayAdapter<Object>(MainActivity.this, android.R.layout.simple_list_item_1, heroes);
            listView.setAdapter(arrayAdapter);


        } catch (JSONException e) {
            Log.e(TAG, "reham" + e.getMessage());
        }
    }

//    public ItemClicked getItem(int position){
//        return items.get(position);
//    }


    private void addlist() throws JSONException {
        for (int i = 0; i < JsonArrayAsL.size(); i++) {
            JSONObject obj = JsonArrayAsL.get(i);
            heroes[i] = obj.getString("Name") + " " + obj.getString("rating");
//

        }


        ArrayAdapter<Object> arrayAdapter = new ArrayAdapter<Object>(MainActivity.this, android.R.layout.simple_list_item_1, heroes);
        listView.setAdapter(arrayAdapter);

    }
}