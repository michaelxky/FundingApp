package Program.Tech;

import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import java.util.Calendar;
import android.widget.DatePicker;
import android.app.DatePickerDialog;
public class postPublish extends AppCompatActivity {
    private static final String TAG = "uploadPic";
    public static final int REQUEST_CODE_TAKE = 1;
    public static final int REQUEST_CODE_CHOOSE = 0;
    private Uri imageUri;
    private ImageView ivPics;
    private String imageBase64;
    private EditText introText;
    private ProgressDialog progressDialog;
    private static final String POST_URL = "https://studev.groept.be/api/a23PT414/uploadPost";
    private RequestQueue requestQueue;
    private Button btnFunding;
    private Button btnActivity;
    private Button btnDonation;
    private Map<String, String[]> fundingMap;
    private Map<String, String[]> activityMap;
    private Map<String, String[]> donationMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ActivityLifecycle", "postPublish Activity Created");
        setContentView(R.layout.activity_post_publish);
        requestQueue = Volley.newRequestQueue(this);
        initView();
    }


    private void initView() {
        ivPics = findViewById(R.id.iv_photo);
        introText = findViewById(R.id.edit_text);
        btnFunding = findViewById(R.id.btnFunding);
        btnActivity = findViewById(R.id.btnActivity);
        btnDonation = findViewById(R.id.btnDonation);

        //Define Click listener of each support buttons:
        btnFunding.setOnClickListener(v -> showFundingInputDialog("Funding"));
        btnActivity.setOnClickListener(v -> showActivityInputDialog("Activity"));
        btnDonation.setOnClickListener(v -> showDonationInputDialog("Donation"));
        fundingMap = new HashMap<>();
        activityMap = new HashMap<>();
        donationMap = new HashMap<>();
    }


    public void takePhoto(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            doTake();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult called with requestCode: " + requestCode);
        if (grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                Log.d(TAG, "Permission " + permissions[i] + " result: " + grantResults[i]);
            }
        } else {
            Log.d(TAG, "Permission request was canceled or no result");
        }

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doTake();
            } else {
                Toast.makeText(this, "No access to camera", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openAlbum();
            } else {
                Toast.makeText(this, "No access to album", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void doTake() {
        File imageTemp = new File(getExternalCacheDir(), "imageOut.jpeg");
        if (imageTemp.exists()) {
            imageTemp.delete();
        }
        try {
            imageTemp.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageUri = FileProvider.getUriForFile(this, "com.example.Program.Tech.fileprovider", imageTemp);
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CODE_TAKE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    bitmap = imageUtil.getResizedBitmap(bitmap,400);
                    if (bitmap != null) {
                        ivPics.setImageBitmap(bitmap);
                        imageBase64 = imageUtil.imageToBase64(bitmap);
                    } else {
                        Log.e(TAG, "Failed to decode bitmap from the taken photo");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_CODE_CHOOSE) {
            if (resultCode == RESULT_OK && data != null) {
                Log.d(TAG, "onActivityResult: URI: " + data.getData());
                Uri uri = data.getData();
                displayImage(uri);
            }
        }
    }

    private void displayImage(Uri imagePath) {
        if (imagePath != null)
        {
            Log.d(TAG, "displayImage: " + imagePath);
            try{
                Bitmap bitmap =MediaStore.Images.Media.getBitmap(getContentResolver(),imagePath);
                bitmap = imageUtil.getResizedBitmap(bitmap, 400);
                ivPics.setImageBitmap(bitmap);
                imageBase64 = imageUtil.imageToBase64(bitmap);
            }
            catch (Exception e)
            {
                Log.e(TAG, "Failed to load bitmap: " + imagePath);
                Toast.makeText(this, "Failed to load bitmap", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Log.e(TAG, "Image path is null");
        }
    }

    public void choosePhoto(View view) {
        Log.d(TAG, "choosePhoto called");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openAlbum();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_CHOOSE);
    }

    /**
     * Submits text, images and ways of supports to the database
     */
    public void onSubmitClicked(View view) {
        progressDialog = new ProgressDialog(postPublish.this);
        progressDialog.setMessage("Uploading, please wait...");
        progressDialog.show();

//Execute the Volley call. Note that we are not appending the image string to the URL, that happens further below
        StringRequest submitRequest = new StringRequest (Request.Method.POST, POST_URL,  new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Turn the progress widget off
                progressDialog.dismiss();
                Toast.makeText(postPublish.this, "Post request executed", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(postPublish.this, "Post request failed", Toast.LENGTH_LONG).show();
            }
        }) { //NOTE THIS PART: here we are passing the parameter to the webservice, NOT in the URL!
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                // Load previously saved data if available
                String[] fundData = fundingMap.get("Funding");
                String[] voluntaryData = activityMap.get("Activity");
                String[] donationData = donationMap.get("Donation");
                params.put("image", imageBase64);
                params.put("text", introText.getText().toString());
                if (fundData != null) {
                    params.put("fundgoal", fundData[0]);
                    params.put("fundbegin", fundData[1]);
                    params.put("fundend", fundData[2]);
                }
                if (voluntaryData != null) {
                    params.put("volnbr", voluntaryData[0]);
                    params.put("volreq", voluntaryData[1]);
                    params.put("volbegin", voluntaryData[2]);
                    params.put("volend", voluntaryData[3]);
                }
                if (donationData != null) {
                    params.put("donationgoal", donationData[0]);
                    params.put("donationbegin", donationData[1]);
                    params.put("donationend", donationData[2]);
                }
                return params;
            }
        };

        requestQueue.add(submitRequest);
    }

    /**
     * Pop up dialogue blocks for users to edit ways of funding
     */

    private void showFundingInputDialog(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.support_funding_input, null);
        builder.setView(dialogView);

        final EditText totalAmount = dialogView.findViewById(R.id.totalAmount);
        final EditText beginDate = dialogView.findViewById(R.id.beginDate);
        final EditText endDate = dialogView.findViewById(R.id.endDate);
// Load previously saved data if available
        if (fundingMap.containsKey(title)) {
            String[] details = fundingMap.get(title);
            totalAmount.setText(details[0]);
            beginDate.setText(details[1]);
            endDate.setText(details[2]);
        }
        // Set up date picker for beginDate and endDate
        beginDate.setOnClickListener(v -> showDatePickerDialog(beginDate));
        endDate.setOnClickListener(v -> showDatePickerDialog(endDate));

        builder.setPositiveButton("OK", (dialog, which) -> {
            // Handle the positive button click event here
            String total = totalAmount.getText().toString();
            String start = beginDate.getText().toString();
            String end = endDate.getText().toString();
            /*
            Store the input details to a HashMap object, to be shown on EditText block when the
            same button of support ways is clicked again
             */
            fundingMap.put(title, new String[]{total, start, end});
            // Process the input details as needed
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Pop up dialogue blocks for users to edit ways of voluntary work
     */
    private void showActivityInputDialog(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.support_activity_input, null);
        builder.setView(dialogView);

        final EditText volunteerNbr = dialogView.findViewById(R.id.volunteerNbr);
        final EditText requirements = dialogView.findViewById(R.id.requirements);
        final EditText beginDate = dialogView.findViewById(R.id.beginDate);
        final EditText endDate = dialogView.findViewById(R.id.endDate);
// Load previously saved data if available
        if (activityMap.containsKey(title)) {
            String[] details = activityMap.get(title);
            volunteerNbr.setText(details[0]);
            requirements.setText(details[1]);
            beginDate.setText(details[2]);
            endDate.setText(details[3]);
        }
        // Set up date picker for beginDate and endDate
        beginDate.setOnClickListener(v -> showDatePickerDialog(beginDate));
        endDate.setOnClickListener(v -> showDatePickerDialog(endDate));

        builder.setPositiveButton("OK", (dialog, which) -> {
            // Handle the positive button click event here
            String nbr = volunteerNbr.getText().toString();
            String quality = requirements.getText().toString();
            String start = beginDate.getText().toString();
            String end = endDate.getText().toString();
            /*
            Store the input details to a HashMap object, to be shown on EditText block when the
            same button of support ways is clicked again
             */
            activityMap.put(title, new String[]{nbr, quality, start, end});
            // Process the input details as needed
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Pop up dialogue blocks for users to edit ways of donation
     */
    private void showDonationInputDialog(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.support_donation_input, null);
        builder.setView(dialogView);

        final EditText expectedItems = dialogView.findViewById(R.id.epectedItems);
        final EditText beginDate = dialogView.findViewById(R.id.beginDate);
        final EditText endDate = dialogView.findViewById(R.id.endDate);
// Load previously saved data if available
        if (donationMap.containsKey(title)) {
            String[] details = donationMap.get(title);
            expectedItems.setText(details[0]);
            beginDate.setText(details[1]);
            endDate.setText(details[2]);
        }
        // Set up date picker for beginDate and endDate
        beginDate.setOnClickListener(v -> showDatePickerDialog(beginDate));
        endDate.setOnClickListener(v -> showDatePickerDialog(endDate));

        builder.setPositiveButton("OK", (dialog, which) -> {
            // Handle the positive button click event here
            String items = expectedItems.getText().toString();
            String start = beginDate.getText().toString();
            String end = endDate.getText().toString();
            /*
            Store the input details to a HashMap object, to be shown on EditText block when the
            same button of support ways is clicked again
             */
            donationMap.put(title, new String[]{items, start, end});
            // Process the input details as needed
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDatePickerDialog(EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                    editText.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }
}
