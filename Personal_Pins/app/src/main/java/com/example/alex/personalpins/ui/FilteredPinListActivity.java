package com.example.alex.personalpins.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.alex.personalpins.R;
import com.example.alex.personalpins.adapters.PinListAdapter;
import com.example.alex.personalpins.firebase.FirebaseKey;
import com.example.alex.personalpins.model.Pin;
import com.example.alex.personalpins.model.Tag;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.ReplaySubject;

public class FilteredPinListActivity extends AppCompatActivity {

    public static final String TAGS_TO_FILTER_PINS = "tags_to_filter_pins";

    FirebaseDatabase firebase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebase.getReference();

    @BindView (R.id.pinRecyclerView) RecyclerView pinRecyclerView;
    @BindView(R.id.tagListTextView) TextView tagListTextView;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.container) RelativeLayout container;
    @BindView(R.id.emptyTextView) TextView emptyTextView;

    ArrayList <Tag> selectedTags;
    ArrayList <Pin> pins = new ArrayList<>();
    HashSet <String> pinIds = new HashSet<>();
    PinListAdapter adapter;
    Disposable disposable;
    ReplaySubject<String> notifier = ReplaySubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_pin_list);
        ButterKnife.bind(this);
        selectedTags = getIntent().getParcelableArrayListExtra(TAGS_TO_FILTER_PINS);
        tagListTextView.setText(getResources().getString(R.string.filter_pins_hint, Tag.convertTagListToString(selectedTags)));
        adapter = new PinListAdapter(pins, this, emptyTextView);
        pinRecyclerView.setAdapter(adapter);
        pinRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        showLoading(true);
        int i = 0;
        for (Tag tag:selectedTags) {
            boolean shouldCheckIsLoading = (i == selectedTags.size() - 1);
            searchPinsByTag(tag, shouldCheckIsLoading);
            i++;
        }

        disposable = notifier.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                DatabaseReference reference = databaseReference.child(FirebaseKey.PINS).
                        child(s);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Pin newPin = dataSnapshot.getValue(Pin.class);
                        pins.add(newPin);
                        adapter.refreshData(pins);
                        showLoading(false);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    private void searchPinsByTag(Tag tag, boolean shouldCheckIsLoading) {
        final DatabaseReference ref = databaseReference.child(FirebaseKey.TAGS).
                child(tag.getFirebaseId()).child(FirebaseKey.PINS);
        if (shouldCheckIsLoading) {
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
                    if (pinIds.add(dataSnapshot.getKey())) {
                        notifier.onNext(dataSnapshot.getKey());
                    }
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
    }

    private void showLoading (boolean isLoading) {
        if (isLoading) {
            container.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            container.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
