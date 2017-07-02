package com.example.alex.personalpins.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alex.personalpins.R;
import com.example.alex.personalpins.adapters.TagListAdapter;
import com.example.alex.personalpins.firebase.FirebaseKey;
import com.example.alex.personalpins.model.Board;
import com.example.alex.personalpins.model.Pin;
import com.example.alex.personalpins.model.Tag;
import com.example.alex.personalpins.model.TagList;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.ReplaySubject;

public class SelectTagsActivity extends AppCompatActivity {

    public static final String SELECTED_TAGS = "selected_tags";
    public static final String TAG_SELECTING_MODE = "tag_selecting_mode";
    @BindView(R.id.searchInfoTextView)  TextView searchInfoTextView;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.tagNameEditText) EditText newTagEditText;
    @BindView(R.id.container) LinearLayout container;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.button) Button button;
    @BindView(R.id.emptyTextView) TextView emptyTextView;
    TagList tagList;
    TagListAdapter adapter;

    FirebaseDatabase firebase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tags);
        ButterKnife.bind(this);

        tagList = new TagList();
        adapter = new TagListAdapter(tagList, emptyTextView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getSavedData();

        if (getIntent().getBooleanExtra(TAG_SELECTING_MODE, false)) {
            newTagEditText.setVisibility(View.GONE);
            button.setText(R.string.search);
        } else {
            searchInfoTextView.setVisibility(View.GONE);
            newTagEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    boolean handled = false;
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if (!newTagEditText.getText().toString().equals("")) {
                            addNewTag(newTagEditText.getText().toString());
                        }
                        handled = true;
                    }
                    newTagEditText.setText("");
                    return handled;
                }
            });
        }

    }

    private void getSavedData () {
        showLoading(true);
        final DatabaseReference ref = databaseReference.child(FirebaseKey.TAGS);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) showLoading(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ref.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Tag tag = dataSnapshot.getValue(Tag.class);
                tag.setFirebaseId(dataSnapshot.getKey());
                tagList.add(tag);
                adapter.refreshData(tagList);
                showLoading(false);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showLoading (boolean isLoading) {
        if (isLoading) {
            container.setVisibility(View.INVISIBLE);
            emptyTextView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            container.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void addNewTag(String s) {
        String trimmedString = s.trim();
        for (Tag tag:tagList) {
            if (tag.getTagName().equals(trimmedString)) {
                Toast.makeText(this, "Same tag is already in the list!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Tag newTag = new Tag(trimmedString);
        databaseReference.child(FirebaseKey.TAGS).push().setValue(newTag);
    }

 @OnClick (R.id.button)
    void submit () {
     ArrayList <Tag> selectedTags = new ArrayList<>();
     for (int i = 0; i < recyclerView.getAdapter().getItemCount(); i++) {
         CheckBox checkBox = (CheckBox) recyclerView.getChildAt(i);
         if (checkBox != null && checkBox.isChecked()) {
             selectedTags.add (tagList.get(i));
         }
     }
     Intent intent = new Intent();
     intent.putParcelableArrayListExtra(SELECTED_TAGS, selectedTags);
     setResult(RESULT_OK, intent);
     finish();
 }
}
