package Program.Tech;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

import java.util.HashMap;
import java.util.Map;

public class postOverview extends AppCompatActivity {
    private static String POST_ID; // Updated to non-final, will be set from intent
    private String USERNAME ; //To be replaced by intent conveyed value
    private static final String UPDATE_FUND_URL = "https://studev.groept.be/api/a23PT414/joinFund";
    private static final String UPDATE_DONATION_URL = "https://studev.groept.be/api/a23PT414/joinDonation";
    private static final String UPDATE_VOLUNTEER_URL = "https://studev.groept.be/api/a23PT414/joinVolunteer";
    private static final String QUIT_VOLUNTEER_URL = "https://studev.groept.be/api/a23PT414/quitVolunteer";
    private static final String UPDATE_JOINED_RECORDS_URL = "https://studev.groept.be/api/a23PT414/updateJoinRecords";
    private ImageView imageRetrieved;
    private TextView postText;
    private TextView postTitle;
    private TextView funding;
    private CardView cardFund;
    private TextView volunteer;
    private CardView cardVolunteer;
    private TextView donation;
    private CardView cardDonation;
    private RequestQueue requestQueue;
    private static final String GET_POST_URL = "https://studev.groept.be/api/a23PT414/showPost";
    private static final String GET_SUPPORT_URL = "https://studev.groept.be/api/a23PT414/showSupport";
    private static final String TAG = "postOverview";
    private Button btnFund;
    private Button btnDonation;
    private Button joinButton;
    private Button quitButton;
    private String databaseFundGoal;
    private String databaseDonationGoal;
    private String databaseVoluntaryTitle;
    private String databaseVolunteerNbr;
    private String newFundGoal; //The fund goal after the funding action, used to update the fundGoal in db.
    private final Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_overview);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        imageRetrieved = findViewById(R.id.iv_photo);
        postText = findViewById(R.id.post_text);
        postTitle = findViewById(R.id.title);
        funding = findViewById(R.id.funding);
        cardFund = findViewById(R.id.cardViewFund);
        volunteer = findViewById(R.id.volunteer);
        cardVolunteer = findViewById(R.id.cardViewVolunteer);
        donation = findViewById(R.id.donation);
        cardDonation = findViewById(R.id.cardViewDonation);
        requestQueue = Volley.newRequestQueue(this);
        btnFund = findViewById(R.id.fundButton);
        //Define Click listener of each support buttons:
        btnFund.setOnClickListener(v -> payFundingDialog("Funding"));
        btnDonation = findViewById(R.id.donationButton);
        //Define Click listener of each support buttons:
        btnDonation.setOnClickListener(v -> offerDonationDialog("Donation"));
        joinButton = findViewById(R.id.joinButton);
        joinButton.setOnClickListener(v -> joinVolunteer());
        quitButton = findViewById(R.id.quitButton);
        quitButton.setOnClickListener(v -> quitVolunteer());

        // Get postID from intent
        Intent intent = getIntent();
        POST_ID = intent.getStringExtra("postID");
        USERNAME = intent.getStringExtra("username"); // Add this line to get the username

        showPost(null, POST_ID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        runnable = new Runnable() {
            @Override
            public void run() {
                showSupport(null, POST_ID);
                handler.postDelayed(this, 500); // updates the support info every 500ms
            }
        };
        handler.post(runnable);
    }

    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); // When this activity is not in the main thread, stop updating
    }

    public void showPost(View caller, final String yourSpecificId) {
        // Construct the URL with the specific ID as a parameter
        String urlWithId = GET_POST_URL + "/" + yourSpecificId;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlWithId, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() > 0) {
                                JSONObject jsonObject = response.getJSONObject(0);
                                String base64Image = jsonObject.getString("image");
                                String introText = jsonObject.getString("introText");
                                String introTitle = jsonObject.getString("introTitle");

                                // Decode Base64 string to byte array
                                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                                // Convert byte array to Bitmap
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                // Set Bitmap to ImageView
                                imageRetrieved.setImageBitmap(decodedByte);
                                // Set introText to TextView
                                postText.setText(introText);
                                // Set introTitle to TextView
                                postTitle.setText(introTitle);

                            } else {
                                Toast.makeText(postOverview.this, "No data found for the given ID", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(postOverview.this, "Failed to parse response", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log the error message
                Log.e(TAG, "Request error: " + error.getMessage());
                Toast.makeText(postOverview.this, "Request failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    public void showSupport(View caller, final String yourSpecificId) {
        // Construct the URL with the specific ID as a parameter
        String urlWithId = GET_SUPPORT_URL + "/" + yourSpecificId;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlWithId, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() > 0) {
                                JSONObject jsonSupport = response.getJSONObject(0);
                                //Retrieve info of funding
                                databaseFundGoal = jsonSupport.getString("fundGoal");
                                String fundBegin = jsonSupport.getString("fundBegin");
                                String fundEnd = jsonSupport.getString("fundEnd");
                                //Retrieve info of voluntary activity
                                databaseVoluntaryTitle = jsonSupport.getString("voluntaryTitle");
                                databaseVolunteerNbr = jsonSupport.getString("volunteerNbr");
                                String volunteerRequirement = jsonSupport.getString("volunteerRequirement");
                                String voluntaryBegin = jsonSupport.getString("voluntaryBegin");
                                String voluntaryEnd = jsonSupport.getString("voluntaryEnd");
                                String voluntaryCity = jsonSupport.getString("voluntaryCity");
                                //Retrieve info of donation
                                databaseDonationGoal = jsonSupport.getString("donationGoal");
                                String donationBegin = jsonSupport.getString("donationBegin");
                                String donationEnd = jsonSupport.getString("donationEnd");
                                String donationCity = jsonSupport.getString("donationCity");
                                //Show TextView block according to whether there is valid data in it.
                                //Funding details
                                if (databaseFundGoal.equals("null")) {
                                    cardFund.setVisibility(View.GONE);
                                } else {
                                    cardFund.setVisibility(View.VISIBLE);
                                    String fundingDetails = "Funding\n" + "I want to raise: " + databaseFundGoal + "\n" +
                                            "The funding begins:" + fundBegin + "\n" +
                                            "The funding ends: " + fundEnd;
                                    funding.setText(fundingDetails);
                                }
                                //Voluntary details
                                if (databaseVolunteerNbr.equals("null")) {
                                    cardVolunteer.setVisibility(View.GONE);
                                } else {
                                    cardVolunteer.setVisibility(View.VISIBLE);
                                    String voluntaryDetails = "Volunteer\n" + "Activity Name: " + databaseVoluntaryTitle + "\n" +
                                            "We need " + databaseVolunteerNbr + " volunteers\n" +
                                            "The volunteer should be " + volunteerRequirement + "\n" +
                                            "The volunteer begins:" + voluntaryBegin + "\n" +
                                            "The volunteer ends: " + voluntaryEnd + "\n" +
                                            "Location: " + voluntaryCity;
                                    volunteer.setText(voluntaryDetails);
                                }
                                //Donation details
                                if (databaseDonationGoal.equals("null")) {
                                    cardDonation.setVisibility(View.GONE);
                                } else {
                                    cardDonation.setVisibility(View.VISIBLE);
                                    String donationDetails = "donation\n" + "I want: " + databaseDonationGoal + "\n" +
                                            "The donation begins:" + donationBegin + "\n" +
                                            "The donation ends: " + donationEnd + "\n" +
                                            "Location: " + donationCity;
                                    donation.setText(donationDetails);
                                }
                                // Adjust constraints dynamically
                                ConstraintLayout.LayoutParams volunteerParams = (ConstraintLayout.LayoutParams) cardVolunteer.getLayoutParams();
                                ConstraintLayout.LayoutParams donationParams = (ConstraintLayout.LayoutParams) cardDonation.getLayoutParams();
                                //Set volunteer location
                                if (databaseFundGoal.equals("null")) {
                                    volunteerParams.topToBottom = R.id.iv_photo;
                                } else {
                                    volunteerParams.topToBottom = R.id.cardViewFund; // Or the parent view or some other reference
                                }
                                //Set donation location
                                if (databaseVoluntaryTitle.equals("null")) {
                                    if (databaseFundGoal.equals("null")) {
                                        donationParams.topToBottom = R.id.iv_photo;
                                    } else {
                                        donationParams.topToBottom = R.id.cardViewFund;
                                    }

                                } else {
                                    donationParams.topToBottom = R.id.cardViewVolunteer; // Or the parent view or some other reference
                                }

                            } else {
                                Toast.makeText(postOverview.this, "No data found for the given ID", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(postOverview.this, "Failed to parse response", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log the error message
                Log.e(TAG, "Request error: " + error.getMessage());
                Toast.makeText(postOverview.this, "Show Support Request failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    /**
     * Pop up dialogue blocks for users to pay money to fund
     */
    private void payFundingDialog(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fund_action, null);
        builder.setView(dialogView);

        final TextView dialogFundGoal = dialogView.findViewById(R.id.fundGoal);
        final EditText editFund = dialogView.findViewById(R.id.editFund);
        String showText = "We still need: " + databaseFundGoal + "€";
        dialogFundGoal.setText(showText);

        builder.setPositiveButton("OK", (dialog, which) -> {
            // Handle the positive button click event here
            String inputText = editFund.getText().toString().trim();
            Log.d("payFundingDialog", "Input text: " + inputText);
            //Analyze whether the input is valid
            if (!inputText.isEmpty()) {
                try {
                    double fundValue = Double.parseDouble(inputText);
                    double targetValue = Double.parseDouble(databaseFundGoal); //The remaining fund goal
                    if (fundValue > targetValue) {
                        Toast.makeText(this, "Your kindness is overflow!", Toast.LENGTH_SHORT).show();
                    } else if (fundValue > 0) {
                        double newGoal = targetValue - fundValue;
                        newFundGoal = String.valueOf(newGoal);
                        databaseFundGoal = newFundGoal;
                        Toast.makeText(this, "Funding received!\nThank you for your kindness!", Toast.LENGTH_SHORT).show();
                        //Submit the newFundGoal to the db.
                        submitFund(newFundGoal, inputText);
                    } else {
                        Toast.makeText(this, "The funding value should be positive!", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Log.e("payFundingDialog", "Invalid number format", e);
                    Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter a number", Toast.LENGTH_SHORT).show();
            }

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    /**
     * TRANSFER DATA TO DATABASE:
     * Update the new fund goal to table support
     * Insert a new activity record to table joinedSupport
     */
    public void submitFund(String newFundGoal, String newFundValue) {
        //Execute the Volley call. Note that we are not appending the image string to the URL, that happens further below
        StringRequest submitRequest = new StringRequest(Request.Method.POST, UPDATE_FUND_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(postOverview.this, "Post request executed", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(postOverview.this, "Post request failed", Toast.LENGTH_LONG).show();
            }
        }) { //NOTE THIS PART: here we are passing the parameter to the webservice, NOT in the URL!
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("newgoal", newFundGoal);
                params.put("id", POST_ID);
                return params;
            }
        };

        StringRequest updateJoinedRecords = new StringRequest(Request.Method.POST, UPDATE_JOINED_RECORDS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(postOverview.this, "Post request executed", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(postOverview.this, "Post request failed", Toast.LENGTH_LONG).show();
            }
        }) { //NOTE THIS PART: here we are passing the parameter to the webservice, NOT in the URL!
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", USERNAME);
                params.put("type", "Funding");
                String fundDetails = "Fund amount: " + newFundValue + "€";
                params.put("details", fundDetails);
                return params;
            }
        };

        requestQueue.add(submitRequest);
        requestQueue.add(updateJoinedRecords);
    }

    /**
     * Pop up dialogue blocks for users to register what they want to donate
     */
    private void offerDonationDialog(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.donation_action, null);
        builder.setView(dialogView);

        final TextView dialogDonationGoal = dialogView.findViewById(R.id.donationGoal);
        final EditText dialogEditDonation = dialogView.findViewById(R.id.editDonation);
        String showText = "We need: " + databaseDonationGoal;
        dialogDonationGoal.setText(showText);

        builder.setPositiveButton("OK", (dialog, which) -> {
            // Handle the positive button click event here
            String inputText = dialogEditDonation.getText().toString().trim();
            if (!inputText.isEmpty()) {
                submitDonation(inputText);
                Toast.makeText(this, "Thank you for your kindness!", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "The announcer would contact you for details soon!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter a valid item!", Toast.LENGTH_SHORT).show();
            }

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * TRANSFER DATA TO DATABASE:
     * Update the donationGet to table support
     * Insert a new activity record to table joinedSupport
     */
    public void submitDonation(String newDonation) {
        //Execute the Volley call. Note that we are not appending the image string to the URL, that happens further below
        StringRequest submitRequest = new StringRequest(Request.Method.POST, UPDATE_DONATION_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(postOverview.this, "Post request executed", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(postOverview.this, "Post request failed", Toast.LENGTH_LONG).show();
            }
        }) { //NOTE THIS PART: here we are passing the parameter to the webservice, NOT in the URL!
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("newdonation", newDonation);
                params.put("id", POST_ID);
                return params;
            }
        };

        StringRequest updateJoinedRecords = new StringRequest(Request.Method.POST, UPDATE_JOINED_RECORDS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(postOverview.this, "Post request executed", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(postOverview.this, "Post request failed", Toast.LENGTH_LONG).show();
            }
        }) { //NOTE THIS PART: here we are passing the parameter to the webservice, NOT in the URL!
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", USERNAME);
                params.put("type", "Donation");
                String donationDetails = "Donation Items: " + newDonation;
                params.put("details", donationDetails);
                return params;
            }
        };

        requestQueue.add(submitRequest);
        requestQueue.add(updateJoinedRecords);
    }

    /**
     * RESPOND OF PRESS OF JOIN BUTTON
     * Examine whether the enrollment is full and calculate the new volunteer nbr required
     */
    private void joinVolunteer() {
        Toast.makeText(postOverview.this, "Thank you for joining us!", Toast.LENGTH_SHORT).show();
        joinButton.setVisibility(View.GONE);
        quitButton.setVisibility(View.VISIBLE);
        int volunteerNumber = Integer.parseInt(databaseVolunteerNbr);
        if (volunteerNumber > 0) {
            volunteerNumber--;
            submitVolunteer(String.valueOf(volunteerNumber), databaseVoluntaryTitle);
        } else {
            Toast.makeText(postOverview.this, "Enrollment is full!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * RESPOND OF PRESS OF QUIT BUTTON
     * Calculate the new volunteer nbr required
     */
    private void quitVolunteer() {
        Toast.makeText(postOverview.this, "You have quitted the activity", Toast.LENGTH_SHORT).show();
        quitButton.setVisibility(View.GONE);
        joinButton.setVisibility(View.VISIBLE);
        int volunteerNumber = Integer.parseInt(databaseVolunteerNbr);
        volunteerNumber++;
        String voluntaryDetails = "Voluntary Activity: " + databaseVoluntaryTitle;
        deleteVoluntaryRecord(String.valueOf(volunteerNumber), voluntaryDetails);
    }

    /**
     * TRANSFER DATA TO DATABASE:
     * Update the volunteer nbr required to table support
     * Insert a new activity record to table joinedSupport
     */
    public void submitVolunteer(String newVolunteerNbr, String volDetails) {
        //Execute the Volley call. Note that we are not appending the image string to the URL, that happens further below
        StringRequest submitRequest = new StringRequest(Request.Method.POST, UPDATE_VOLUNTEER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(postOverview.this, "Post request executed", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(postOverview.this, "Post request failed", Toast.LENGTH_LONG).show();
            }
        }) { //NOTE THIS PART: here we are passing the parameter to the webservice, NOT in the URL!
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("newnbr", newVolunteerNbr);
                params.put("id", POST_ID);
                return params;
            }
        };

        StringRequest updateJoinedRecords = new StringRequest(Request.Method.POST, UPDATE_JOINED_RECORDS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(postOverview.this, "Post request executed", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(postOverview.this, "Post request failed", Toast.LENGTH_LONG).show();
            }
        }) { //NOTE THIS PART: here we are passing the parameter to the webservice, NOT in the URL!
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", USERNAME);
                params.put("type", "Volunteer");
                String voluntaryDetails = "Voluntary Activity: " + databaseVoluntaryTitle;
                params.put("details", voluntaryDetails);
                return params;
            }
        };

        requestQueue.add(submitRequest);
        requestQueue.add(updateJoinedRecords);
    }

    public void deleteVoluntaryRecord(String newVolunteerNbr, String volDetail) {
        //Execute the Volley call. Note that we are not appending the image string to the URL, that happens further below
        StringRequest submitRequest = new StringRequest(Request.Method.POST, UPDATE_VOLUNTEER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(postOverview.this, "Post request executed", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(postOverview.this, "Post request failed", Toast.LENGTH_LONG).show();
            }
        }) { //NOTE THIS PART: here we are passing the parameter to the webservice, NOT in the URL!
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("newnbr", newVolunteerNbr);
                params.put("id", POST_ID);
                return params;
            }
        };

        StringRequest updateJoinedRecords = new StringRequest(Request.Method.POST, QUIT_VOLUNTEER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(postOverview.this, "Post request executed", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(postOverview.this, "Post request failed", Toast.LENGTH_LONG).show();
            }
        }) { //NOTE THIS PART: here we are passing the parameter to the webservice, NOT in the URL!
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", USERNAME);
                params.put("details", volDetail);
                return params;
            }
        };

        requestQueue.add(submitRequest);
        requestQueue.add(updateJoinedRecords);
    }
}
