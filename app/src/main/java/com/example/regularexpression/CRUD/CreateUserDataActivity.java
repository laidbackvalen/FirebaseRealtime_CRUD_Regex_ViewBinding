package com.example.regularexpression.CRUD;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.regularexpression.R;
import com.example.regularexpression.models.UserDataModelClass;
import com.example.regularexpression.databinding.ActivityCreateUserDataBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import java.util.Objects;

public class CreateUserDataActivity extends AppCompatActivity {
    private ActivityCreateUserDataBinding binding;
    String url;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCreateUserDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        email = getIntent().getStringExtra("email");
        binding.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityResultLauncher.launch("image/*");
            }
        });
        binding.addDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumberInput = binding.phone.getText().toString();
                String nameInput = binding.name.getText().toString();
                addUserInfo(email, url, phoneNumberInput, nameInput);
                Toast.makeText(CreateUserDataActivity.this, ""+email, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void addUserInfo(String email, String url, String phoneNumberInput, String name) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        UserDataModelClass modelClass = new UserDataModelClass(email, url, phoneNumberInput, name);
        databaseReference.child("Users")
                .child(Objects.requireNonNull(firebaseAuth.getUid()))
                .setValue(modelClass)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(CreateUserDataActivity.this, "User Details Added Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), UserInformationActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateUserDataActivity.this, "Error Adding User data" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), results -> {
        FirebaseStorage.getInstance().getReference().child("images")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .child("IMG_" + System.currentTimeMillis())
                .putFile(results)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    url = uri.toString();
                                    Toast.makeText(CreateUserDataActivity.this, "" + url, Toast.LENGTH_SHORT).show();
                                    binding.userImage.setImageURI(results);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CreateUserDataActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
    });
    }