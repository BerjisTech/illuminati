package tech.berjis.illuminati;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    ViewPager featuredViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(FeedActivity.this, R.color.colorWhite));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        initVars();
    }

    private void initVars() {
        featuredViewPager = findViewById(R.id.featuredViewPager);

        loadFeatured();
    }

    private void loadFeatured() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        final List<Features> features = new ArrayList<>();

        dbRef.child("Stories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Features l = snap.getValue(Features.class);
                        features.add(l);
                    }
                    LoadFeaturesAdapter loadFeaturesAdapter = new LoadFeaturesAdapter(FeedActivity.this, features);
                    featuredViewPager.setAdapter(loadFeaturesAdapter);
                } else {
                    Toast.makeText(FeedActivity.this, "No stories for you", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}