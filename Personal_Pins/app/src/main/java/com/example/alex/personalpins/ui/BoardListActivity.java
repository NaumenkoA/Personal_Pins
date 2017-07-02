package com.example.alex.personalpins.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alex.personalpins.R;
import com.example.alex.personalpins.adapters.BoardListAdapter;
import com.example.alex.personalpins.firebase.FirebaseKey;
import com.example.alex.personalpins.model.Board;
import com.example.alex.personalpins.model.Tag;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BoardListActivity extends AppCompatActivity {

    FirebaseDatabase firebase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebase.getReference();

    private static final int REQUEST_SELECT_COVER_IMAGE = 10;
    private static final int REQUEST_SELECT_TAGS = 11;
    @BindView(R.id.boardRecyclerView) RecyclerView boardRecyclerView;
    @BindView(R.id.newBoardFab) FloatingActionButton newBoardFab;
    @BindView(R.id.searchPinsFab) FloatingActionButton searchPinsFab;
    @BindView(R.id.container) RelativeLayout container;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.emptyTextView) TextView emptyTextView;
    BoardListAdapter adapter;
    ArrayList<Board> boards;
    String newBoardName;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_list);
        ButterKnife.bind(this);
        boards = new ArrayList<>();
        getSavedData();
        adapter = new BoardListAdapter(boards, this, emptyTextView);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        boardRecyclerView.setAdapter(adapter);
        boardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

   private void getSavedData () {
       showLoading(true);
       final DatabaseReference ref = databaseReference.child(FirebaseKey.BOARDS);

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
               Board board = dataSnapshot.getValue(Board.class);
               board.setFirebaseId(dataSnapshot.getKey());
               boards.add(board);
               adapter.refreshData(boards);
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

    @OnClick (R.id.searchPinsFab)
    void searchPinsByTags () {
        Intent intent = new Intent(this, SelectTagsActivity.class);
        intent.putExtra(SelectTagsActivity.TAG_SELECTING_MODE, true);
        startActivityForResult(intent, REQUEST_SELECT_TAGS);
    }

    @OnClick (R.id.newBoardFab)
    void createNewBoard () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter title for the new board");
        final EditText inputEditText = new EditText(this);
        inputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(inputEditText);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (inputEditText.getText().length() == 0) {
                    Toast.makeText(BoardListActivity.this, "Board name can't be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String boardName = inputEditText.getText().toString().trim();
                boolean b = boardListHasSameBoardName(boardName);
                if (b) {
                    Toast.makeText(BoardListActivity.this, "Board with the same name is already in the list.", Toast.LENGTH_SHORT).show();
                } else {
                    newBoardName = boardName;
                    selectCoverImage();
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private boolean boardListHasSameBoardName(String newBoardName) {
        for (Board board : boards) {
            if (board.getBoardTitle().equals(newBoardName)) return true;
        }
        return false;
    }

    void selectCoverImage () {
        showFabs(false);
        Snackbar snackbar = Snackbar.make (relativeLayout, "Select cover image for the new board.", Snackbar.LENGTH_SHORT);
        snackbar.show();
        snackbar.addCallback(new Snackbar.Callback() {

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                Intent selectPhotoIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                selectPhotoIntent.setType("image/*");
                selectPhotoIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                startActivityForResult(selectPhotoIntent, REQUEST_SELECT_COVER_IMAGE);
               }
        });
    }

    private void showFabs(boolean isVisible) {
        if (!isVisible) {
            newBoardFab.setVisibility(View.INVISIBLE);
            searchPinsFab.setVisibility(View.INVISIBLE);
        } else {
            newBoardFab.setVisibility(View.VISIBLE);
            searchPinsFab.setVisibility(View.VISIBLE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Board newBoard = null;
        showFabs(true);
        if (requestCode == REQUEST_SELECT_COVER_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    newBoard = new Board(newBoardName, data.getData());
                    pushToFirebase(newBoard);
                }
                } else {
                    newBoard = new Board(newBoardName);
                    pushToFirebase(newBoard);
                }
            }

        if (requestCode == REQUEST_SELECT_TAGS) {
            if (resultCode == RESULT_OK) {
                ArrayList <Tag> tags = data.getParcelableArrayListExtra(SelectTagsActivity.SELECTED_TAGS);
                if (tags != null && tags.size() > 0) {
                    Intent intent = new Intent(this, FilteredPinListActivity.class);
                    intent.putParcelableArrayListExtra(FilteredPinListActivity.TAGS_TO_FILTER_PINS, tags);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "No tags were selected.", Toast.LENGTH_SHORT).show();
                }
            }
            }
    }

    private void pushToFirebase(Board newBoard) {
        String firebaseKey = databaseReference.child(FirebaseKey.BOARDS).push().getKey();
        databaseReference.child(FirebaseKey.BOARDS).child(firebaseKey).setValue(newBoard);
    }
}
