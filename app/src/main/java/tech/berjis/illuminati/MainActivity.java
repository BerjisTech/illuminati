package tech.berjis.illuminati;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;
    TextView title, start;
    Animation btnAnim;
    String UID;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorWhite));
        window.setNavigationBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorWhite));

        initVars();
    }

    private void initVars() {
        if (firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
        }

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.keepSynced(true);

        title = findViewById(R.id.title);
        start = findViewById(R.id.start);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);
        start.setAnimation(btnAnim);

        if (restorePrefData()) {
            title.setVisibility(View.GONE);
            start.setVisibility(View.GONE);

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            userState();
                        }
                    }, 3000);
        }

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                savePrefsData();
                finish();
            }
        });
    }

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend", false);
        return isIntroActivityOpnendBefore;
    }

    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend", true);
        editor.commit();
    }

    private void userState() {
        if (mAuth.getCurrentUser() == null) {
            Intent mainActivity = new Intent(getApplicationContext(), RegisterActivity.class);
            mainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainActivity);
        } else {
            redirectUser();
        }
    }

    private void redirectUser() {
        UID = mAuth.getCurrentUser().getUid();
        dbRef.child("Users")
                .child(UID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String user_type = Objects.requireNonNull(snapshot.child("user_type").getValue()).toString();
                        String user_email = Objects.requireNonNull(snapshot.child("user_email").getValue()).toString();
                        String user_name = Objects.requireNonNull(snapshot.child("user_name").getValue()).toString();
                        String first_name = Objects.requireNonNull(snapshot.child("first_name").getValue()).toString();
                        String last_name = Objects.requireNonNull(snapshot.child("last_name").getValue()).toString();

                        if (user_email.equals("") ||
                                user_name.equals("") ||
                                first_name.equals("") ||
                                last_name.equals("")) {
                            Intent mainActivity = new Intent(getApplicationContext(), FeedActivity.class);
                            startActivity(mainActivity);
                            finish();
                        } else {
                            if (snapshot.child("subscription").exists()) {
                                long today = System.currentTimeMillis() / 1000L;
                                long nextMonth = Long.parseLong(Objects.requireNonNull(snapshot.child("next_month").getValue()).toString());


                                // long dv = today * 1000;// its need to be in milisecond
                                // Date df = new java.util.Date(dv);
                                // String vv = new SimpleDateFormat("dd MMMM yyyy").format(df);

                                // long dvs = nextMonth * 1000;// its need to be in milisecond
                                // Date dfs = new java.util.Date(dvs);
                                // String vvs = new SimpleDateFormat("dd MMMM yyyy").format(dfs);

                                // Toast.makeText(MainActivity.this, "This month " + vv, Toast.LENGTH_SHORT).show();
                                // Toast.makeText(MainActivity.this, "Next month " + vvs, Toast.LENGTH_SHORT).show();

                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                dbRef.child("Users").child(UID).child("device").setValue(deviceToken);
                                Intent mainActivity = new Intent(getApplicationContext(), FeedActivity.class);
                                startActivity(mainActivity);
                                finish();
                            } else {
                                Intent mainActivity = new Intent(getApplicationContext(), ChooseSubscriptionActivity.class);
                                startActivity(mainActivity);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}