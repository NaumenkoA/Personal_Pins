package com.example.alex.personalpins.ui;

import android.content.Intent;
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
import com.example.alex.personalpins.model.Board;
import com.example.alex.personalpins.model.Pin;
import com.example.alex.personalpins.model.Tag;
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
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.ReplaySubject;

public class PinListActivity extends AppCompatActivity {

    FirebaseDatabase firebase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebase.getReference();

    public static final String SELECTED_BOARD = "selected_board";
    private static final int CREATE_PIN_REQUEST = 20;
    public static final String CREATED_PIN = "created_pin";
    @BindView(R.id.pinRecyclerView) RecyclerView pinRecyclerView;
    @BindView(R.id.boardTitleTextView) TextView boardTitleTextView;
    @BindView(R.id.container) RelativeLayout container;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.emptyTextView) TextView emptyTextView;
    Board board;
    PinListAdapter adapter;
    ArrayList <Pin> pins;
    ReplaySubject<String> notifier = ReplaySubject.create();
    Disposable disposable;
    ArrayList <String> savedPinsIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_list);
        ButterKnife.bind(this);
        pins = new ArrayList<>();
        board = getIntent().getParcelableExtra(SELECTED_BOARD);
        adapter = new PinListAdapter(pins, this, emptyTextView);
        pinRecyclerView.setAdapter(adapter);
        pinRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        boardTitleTextView.setText(board.getBoardTitle());

        getSavedData();
        disposable = notifier.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                DatabaseReference reference = databaseReference.child(FirebaseKey.PINS).
                        child(s);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Pin newPin = dataSnapshot.getValue(Pin.class);
                        newPin.setFirebaseId(dataSnapshot.getKey());
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


    private void getSavedData () {
        showLoading(true);
        savedPinsIds = new ArrayList<>();
        final DatabaseReference ref = databaseReference.child(FirebaseKey.BOARDS).
                child(board.getFirebaseId()).child(FirebaseKey.PINS);
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
                notifier.onNext(dataSnapshot.getKey());
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

    @OnClick (R.id.newPinFab)
    void createNewPin () {
        Intent intent = new Intent(this, CreatePinActivity.class);
        startActivityForResult(intent, CREATE_PIN_REQUEST);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CREATE_PIN_REQUEST) {
                if (data != null) {
                    Pin newPin =  data.getParcelableExtra(CREATED_PIN);
                    String firebaseKey = databaseReference.child(FirebaseKey.PINS).push().getKey();
                    databaseReference.child(FirebaseKey.PINS).child(firebaseKey).setValue(newPin);
                    databaseReference.child(FirebaseKey.BOARDS).child(board.getFirebaseId())
                            .child(FirebaseKey.PINS).child(firebaseKey).setValue(true);
                    if (newPin.getTags() != null && newPin.getTags().size()!= 0) {
                        for (Tag tag:newPin.getTags()) {
                            databaseReference.child(FirebaseKey.TAGS).child(tag.getFirebaseId())
                                    .child (FirebaseKey.PINS).child(firebaseKey).setValue(true);
                        }
                    }
                }
            }
        }
    }

}
