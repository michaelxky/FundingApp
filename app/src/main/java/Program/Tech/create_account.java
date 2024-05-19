package Program.Tech;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

public class create_account extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void createAccount(String Name, String Password, String Question, String Answer) {
        // Proper URL for the API endpoint to insert new user
        String url = "https://studev.groept.be/api/a23PT414/Register";

        // Hash the password and the answer before sending them to the server
        String hashedPassword = BCrypt.hashpw(Password, BCrypt.gensalt());
        String hashedAnswer = BCrypt.hashpw(Answer, BCrypt.gensalt());

        // Create a request queue for Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create a StringRequest to send data to the server and receive response as a string
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle response from the server
                        // Log the response for debugging
                        Log.d("Response", response);
                        // Here you can parse the response and handle it accordingly
                        // Check if the response contains expected JSON data or HTML content
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            // Handle JSON response
                            if (jsonResponse.has("success") && jsonResponse.getBoolean("success")) {
                                Toast.makeText(
                                        create_account.this,
                                        "Account created successfully",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(create_account.this, Main.class));
                            } else {
                                Toast.makeText(
                                        create_account.this,
                                        "Error creating account: " + jsonResponse.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // Handle if response is not in expected JSON format
                            e.printStackTrace();
                            Toast.makeText(
                                    create_account.this,
                                    "Success",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response from the server
                        Toast.makeText(
                                create_account.this,
                                "Error creating account: " + error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Add parameters to the request
                Map<String, String> params = new HashMap<>();
                params.put("name", Name);
                params.put("pass", hashedPassword); // Use the hashed password
                params.put("ques", Question);
                params.put("ans", hashedAnswer); // Use the hashed answer
                return params;
            }
        };

        // Add the request to the request queue
        requestQueue.add(stringRequest);
    }

    public void Create(View view) {
        EditText usernameEditText = findViewById(R.id.username);
        EditText passwordEditText = findViewById(R.id.password);
        EditText securityQuestionEditText = findViewById(R.id.Question);
        EditText answerEditText = findViewById(R.id.Answer);

        String Name = usernameEditText.getText().toString();
        String Password = passwordEditText.getText().toString();
        String Question = securityQuestionEditText.getText().toString();
        String Answer = answerEditText.getText().toString();

        createAccount(Name, Password, Question, Answer);
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
    }

}
