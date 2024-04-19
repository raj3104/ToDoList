package com.example.to_dolist;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    Button add,google;
    private static final int RC_SIGN_IN = 123;
    private GoogleApiClient mGoogleApiClient;
    String default_web_client_id= "414564467545-5ojhfus23pjfdt2pbqs0poo5vh4jqtpg.apps.googleusercontent.com";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText taskTitleEditText;
    private EditText taskDueDateEditText;
    private Button addTaskButton;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();


        taskTitleEditText = findViewById(R.id.editTextTitle);
        taskDueDateEditText = findViewById(R.id.datePicker);
        addTaskButton = findViewById(R.id.buttonAddTask);

        addTaskButton.setOnClickListener(v -> {
            String title = taskTitleEditText.getText().toString();

            String dueDate = taskDueDateEditText.getText().toString();

            // Add task to Firestore
            addTasktoFirebase(title, dueDate);
        });
        add=findViewById(R.id.appCompatButton);
        google=findViewById(R.id.google);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTaskDialog();
            }
        });
        google.setOnClickListener(v -> signInWithGoogle());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this) // Pass in the activity and OnConnectionFailedListener
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso) // Add Google Sign-In API
                .build();
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Handle connection failures
        Log.e(TAG, "GoogleApiClient connection failed: " + connectionResult.getErrorMessage());
    }
    private void signInWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("414564467545-qb6pippjiec0homh4h7k54e6pmu3ctjh.apps.googleusercontent.com")
                .requestEmail()
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Handle Google Sign-In failure
            }
        }
    }
    private void showAddTaskDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_layout, null);
        dialogBuilder.setView(dialogView);

        EditText editTextTitle = dialogView.findViewById(R.id.editTextTitle);

        dialogBuilder.setTitle("Add Task");
        dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Add task logic here
                String taskTitle = editTextTitle.getText().toString();
                // Add the task to the database or perform any other action
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Cancelled, do nothing
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void addTaskToFirestore(String title, Date dueDate) {
        // Get a reference to the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Construct a reference to the tasks collection for the current user
        String userId = "user123"; // Replace with the actual user ID
        DocumentReference tasksRef = db.collection("users").document(userId).collection("tasks").document();

        // Create a new Task object with the provided title and due date
        Task task = new Task(title, dueDate);

        // Add the task to Firestore
        tasksRef.set(task)
                .addOnSuccessListener(aVoid -> {
                    // Task added successfully
                    // You can perform any UI updates or show a success message here
                })
                .addOnFailureListener(e -> {
                    // Failed to add task
                    // Handle the error (e.g., show an error message)
                });
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Proceed with your app logic, such as navigating to the main activity
                    } else {
                        // If sign in fails, display a message to the user.
                        // ...
                    }
                });
    }
    private void addTasktoFirebase(String title, String dueDate) {
        // Create a new task object with title and due date
        Task task = new Task(title, dueDate);

        // Add task to Firestore
        db.collection("tasks")
                .add(task)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Task added with ID: " + documentReference.getId());
                    // Clear input fields after adding task
                    taskTitleEditText.setText("");
                    taskDueDateEditText.setText("");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding task");
                    // Handle failure to add task
                });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
    // In your MainActivity class



}