package Program.Tech;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Program.Tech.showProfileAct;
import java.util.ArrayList;
import java.util.List;

public class personalSupport extends AppCompatActivity {

    private  String USERNAME; //To be replaced by username conveyed
    private static final String SHOW_JOINED_URL = "https://studev.groept.be/api/a23PT414/showJoinedSupport";
    private static final String SHOW_PUBLISHED_URL = "https://studev.groept.be/api/a23PT414/showPublishedSupport";
    private TextView username;
    private TextView menuPublished;
    private TextView menuJoined;
    private RecyclerView recyclerView;

    private List<showProfileAct> publishedActivities;
    private List<showProfileAct> joinedActivities;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_support);

        Intent intent = getIntent();
        USERNAME = intent.getStringExtra("username");

        username = findViewById(R.id.username);
        menuPublished = findViewById(R.id.menu_published);
        menuJoined = findViewById(R.id.menu_joined);
        recyclerView = findViewById(R.id.support_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        username.setText(USERNAME);
        requestQueue = Volley.newRequestQueue(this);

        // Initialize activities
        publishedActivities = new ArrayList<>();
        showPublished(USERNAME);

        joinedActivities = new ArrayList<>();
        showJoined(USERNAME);

        menuPublished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayActivities(publishedActivities);
            }
        });

        menuJoined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayActivities(joinedActivities);
            }
        });

        // 默认显示Published
        displayActivities(publishedActivities);
    }

    private void displayActivities(List<showProfileAct> activities) {
        recyclerView.setAdapter(new profileActAdapter(activities));
    }

    /**
     *
     *
     */
    public void showJoined(String username) {
        // Construct the URL with the specific ID as a parameter
        String urlWithId = SHOW_JOINED_URL + "/" + username;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlWithId, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() > 0) {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    String type = jsonObject.getString("Type");
                                    String details = jsonObject.getString("Details");
                                    joinedActivities.add(new showProfileAct(type, details));
                                }

                            } else {
                                Toast.makeText(personalSupport.this, "No data found for the given username", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(personalSupport.this, "Failed to parse response", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log the error message
                Log.e(TAG, "Request error: " + error.getMessage());
                Toast.makeText(personalSupport.this, "Request failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        );

        requestQueue.add(jsonArrayRequest);
    }

    public void showPublished(String username) {
        // Construct the URL with the specific ID as a parameter
        String urlWithId = SHOW_PUBLISHED_URL + "/" + username;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlWithId, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() > 0) {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    String title = jsonObject.getString("introTitle");
                                    String content = jsonObject.getString("introText");
                                    publishedActivities.add(new showProfileAct(title, content));
                                }

                            } else {
                                Toast.makeText(personalSupport.this, "No data found for the given username", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(personalSupport.this, "Failed to parse response", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log the error message
                Log.e(TAG, "Request error: " + error.getMessage());
                Toast.makeText(personalSupport.this, "Request failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        );

        requestQueue.add(jsonArrayRequest);
    }
}
