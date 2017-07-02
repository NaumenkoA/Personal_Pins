package com.example.alex.personalpins.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.alex.personalpins.R;
import com.example.alex.personalpins.helpers.FileHelper;
import com.example.alex.personalpins.helpers.ImageResizer;

public class ImageEnlargeActivity extends AppCompatActivity {

    public static final String INTENT_SOURCE = "intent_source";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_enlarge);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        ImageView scaledImageView = (ImageView) findViewById(R.id.scaledImageView);

        Intent intent = getIntent();
        if (intent.getStringExtra(INTENT_SOURCE) != null &&
                intent.getStringExtra(INTENT_SOURCE).contains("PinListActivity")) {
            frameLayout.setBackgroundColor(Color.parseColor("#e8e5ee"));
        }
        Uri uri = intent.getData();
        byte [] imageAsByteArray = FileHelper.getByteArrayFromFile(this, uri);
        int size = getScreenSmallestSize();
        Bitmap resizedImage = ImageResizer.resizeImageMaintainAspectRatio(imageAsByteArray, size);
        scaledImageView.setImageBitmap(resizedImage);
    }

    private int getScreenSmallestSize () {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpHeight < dpWidth ? dpHeight : dpWidth);
    }
}
