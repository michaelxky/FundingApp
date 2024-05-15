package Program.Tech;

import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class postPublish extends AppCompatActivity {
    private static final String TAG = "uploadPic";
    public static final int REQUEST_CODE_TAKE = 1;
    public static final int REQUEST_CODE_CHOOSE = 0;
    private Uri imageUri;
    private ImageView ivPics;
    private String imageBase64;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ActivityLifecycle", "postPublish Activity Created");
        setContentView(R.layout.activity_post_publish);
        initView();
    }

    private void initView() {
        ivPics = findViewById(R.id.iv_photo);
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
                ivPics.setImageBitmap(bitmap);
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
}
