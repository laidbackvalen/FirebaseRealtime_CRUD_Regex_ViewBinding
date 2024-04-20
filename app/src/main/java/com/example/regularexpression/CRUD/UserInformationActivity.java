package com.example.regularexpression.CRUD;

import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;
import com.example.regularexpression.R;
import com.example.regularexpression.models.UserDataModelClass;
import com.example.regularexpression.databinding.ActivityUserInformationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserInformationActivity extends AppCompatActivity {
    private ActivityUserInformationBinding binding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = firebaseAuth.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityUserInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        retrieveUserdata();
    }
    private void retrieveUserdata() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserDataModelClass modelClass = snapshot.getValue(UserDataModelClass.class);
                    String email = modelClass.getEmail();
                    String phone = modelClass.getPhone();
                    binding.userEmailRetrieved.setText(email);
                    binding.userPhoneRetrieved.setText(phone);
                    binding.usernameRetrieved.setText(modelClass.getName());
                    Glide.with(UserInformationActivity.this)
                            .asBitmap()
                            .load(modelClass.getUrl())
                            .placeholder(R.drawable.baseline_person_24)
                            .error(R.drawable.ic_launcher_background)
                            .into(binding.retrieveUserImage);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserInformationActivity.this, ""+error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}