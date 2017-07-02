package com.example.alex.personalpins.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.alex.personalpins.BuildConfig;
import com.example.alex.personalpins.R;
import com.example.alex.personalpins.model.MediaType;
import com.example.alex.personalpins.model.Pin;
import com.example.alex.personalpins.model.Tag;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreatePinActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CreatePinActivity.class.getSimpleName();

    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_TAKE_VIDEO = 1;
    private static final int REQUEST_SELECT_PHOTO = 2;
    private static final int REQUEST_SELECT_VIDEO = 3;
    private static final int REQUEST_SELECT_TAGS = 4;

    @BindView(R.id.gridLayout) GridLayout gridLayout;
    @BindView(R.id.imageContainer) FrameLayout frameLayout;
    @BindView(R.id.tagsListTextView) TextView tagsListTextView;
    @BindView(R.id.titleEditText) EditText titleEditText;
    @BindView(R.id.commentEditText) EditText commentEditText;
    ImageView pinImageView;
    private ArrayList <Tag> tags;
    private String mediaType = "";
    private Uri mediaUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pin);
        ButterKnife.bind(this);
        findViewById(R.id.newPhotoImageView).setOnClickListener(this);
        findViewById(R.id.selectPhotoImageView).setOnClickListener(this);
        findViewById(R.id.newVideoImageView).setOnClickListener(this);
        findViewById(R.id.selectVideoImageView).setOnClickListener(this);
        findViewById(R.id.editTagsButton).setOnClickListener(this);
        findViewById(R.id.tagsTitleTextView).setOnClickListener(this);
        findViewById(R.id.tagsListTextView).setOnClickListener(this);
        findViewById(R.id.createPinButton).setOnClickListener(this);
         }

    private Uri getOutputMediaFileUri(String mediaType) {
        if (isExternalStorageAvailable()) {
            File mediaStorageDir;
            String fileName;
            String fileType;
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            switch (mediaType) {
                case (MediaType.MEDIA_TYPE_IMAGE):
                    mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    fileName = "IMG_" + timeStamp;
                    fileType = ".jpg";
                    break;
                case (MediaType.MEDIA_TYPE_VIDEO):
                    mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
                    fileName = "VID_" + timeStamp;
                    fileType = ".mp4";
                    break;
                default:
                    return null;
            }
            File mediaFile;
            try {
                mediaFile = File.createTempFile(fileName, fileType, mediaStorageDir);
                Log.i (TAG, "File: " + Uri.fromFile(mediaFile));
                return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", mediaFile);
            } catch (IOException e) {
                Log.i (TAG, "Error creating file: " + mediaStorageDir.getAbsolutePath() + fileName + fileType);
            }
        }
        return null;
    }

    private boolean isExternalStorageAvailable () {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    void takePhoto () {
        mediaUri = getOutputMediaFileUri (MediaType.MEDIA_TYPE_IMAGE);
        if (mediaUri == null) {
            Toast.makeText(this, "There was a problem accessing your device's external storage.", Toast.LENGTH_LONG).show();
        } else {
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
            startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO);
        }
    }

    void takeVideo () {
        mediaUri = getOutputMediaFileUri(MediaType.MEDIA_TYPE_VIDEO);
        if (mediaUri == null) {
            Toast.makeText(this, "There was a problem accessing your device's external storage.", Toast.LENGTH_LONG).show();
        } else {
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
            takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
            startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);
        }
    }

    void selectPhoto () {
        Intent selectPhotoIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        selectPhotoIntent.setType("image/*");
        selectPhotoIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(selectPhotoIntent, REQUEST_SELECT_PHOTO);
    }

    void selectVideo(){
        Intent selectVideoIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        selectVideoIntent.setType("video/*");
        selectVideoIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(selectVideoIntent, REQUEST_SELECT_VIDEO);
    }

    private void selectTags() {
        Intent selectTagsIntent = new Intent(this, SelectTagsActivity.class);
        startActivityForResult(selectTagsIntent, REQUEST_SELECT_TAGS);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newPhotoImageView:
                takePhoto();
                break;
            case R.id.selectPhotoImageView:
                selectPhoto();
                break;
            case R.id.newVideoImageView:
                takeVideo();
                break;
            case R.id.selectVideoImageView:
                selectVideo();
                break;

            case R.id.editTagsButton:
            case R.id.tagsTitleTextView:
            case R.id.tagsListTextView:
                selectTags();
                break;
            case R.id.createPinButton:
                Pin pin = createNewPin();
                if (pin != null) {
                    Intent data = new Intent();
                    data.putExtra(PinListActivity.CREATED_PIN, pin);
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;
        }
    }

    private Pin createNewPin() {
        if (titleEditText.getText().toString().equals("")) {
            Toast.makeText(this, "Pin title can't be empty. Please enter pin title!", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (mediaUri == null || ("").equals(mediaType)) {
            Toast.makeText(this, "Select photo or video, which will be included into pin!", Toast.LENGTH_SHORT).show();
            return null;
        }
          return new Pin (titleEditText.getText().toString().trim(),
                mediaUri, mediaType,
                commentEditText.getText().toString().trim(),
                tags);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            View.OnClickListener photoOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enlargePhoto(mediaUri);
                }
            };

            View.OnClickListener videoOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playVideo(mediaUri);
                }
            };

            switch (requestCode) {
                case  REQUEST_TAKE_PHOTO:
                    hideMediaSelectionBar();
                    Glide.with(this).load(mediaUri).into(pinImageView);
                    pinImageView.setOnClickListener(photoOnClickListener);
                    mediaType = MediaType.MEDIA_TYPE_IMAGE;
                    break;

                case REQUEST_TAKE_VIDEO:
                    hideMediaSelectionBar();
                    Glide.with(this).load(mediaUri).into(pinImageView);
                    addPlayArrow();
                    pinImageView.setOnClickListener(videoOnClickListener);
                    mediaType = MediaType.MEDIA_TYPE_VIDEO;
                    break;

                case REQUEST_SELECT_PHOTO:
                   if (data != null) {
                        mediaUri = data.getData();
                        hideMediaSelectionBar();
                        Glide.with(this).load(mediaUri).into(pinImageView);
                        pinImageView.setOnClickListener(photoOnClickListener);
                        mediaType = MediaType.MEDIA_TYPE_IMAGE;
                    }
                    break;
                case REQUEST_SELECT_VIDEO:
                    if (data != null) {
                        mediaUri = data.getData();
                        hideMediaSelectionBar();
                        Glide.with(this).load(mediaUri).into(pinImageView);
                        addPlayArrow();
                        pinImageView.setOnClickListener(videoOnClickListener);
                        mediaType = MediaType.MEDIA_TYPE_VIDEO;
                    }
                    break;
                case REQUEST_SELECT_TAGS:
                    tags = data.getParcelableArrayListExtra(SelectTagsActivity.SELECTED_TAGS);
                    tagsListTextView.setText (Tag.convertTagListToString(tags));
                    break;
            }
        }
    }

    private void hideMediaSelectionBar() {
        gridLayout.setVisibility(View.GONE);
        pinImageView = new ImageView(this);
        pinImageView.setTransitionName("pinImage");
        frameLayout.addView(pinImageView);
        pinImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    private void playVideo(Uri uri) {
        Intent intent = new Intent(CreatePinActivity.this, PlayVideoActivity.class);
        intent.setData(uri);
        startActivity(intent);
    }

    private void enlargePhoto(Uri uri) {
        Intent intent = new Intent(CreatePinActivity.this, ImageEnlargeActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                CreatePinActivity.this, this.pinImageView, "pinImage");
        intent.setData(uri);
        startActivity(intent, options.toBundle());
    }

    private void addPlayArrow() {
        ImageView playImageView = new ImageView(this);
        playImageView.setImageResource(R.drawable.ic_play_arrow_white_36dp);
        playImageView.setScaleType(ImageView.ScaleType.CENTER);
        frameLayout.addView(playImageView);
    }

}
