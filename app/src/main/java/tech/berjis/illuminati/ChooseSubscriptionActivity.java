package tech.berjis.illuminati;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class ChooseSubscriptionActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference dbRef;
    String UID;

    View casualView, professionalView;
    TextView casualPrice, casualText, professionalPrice, professionalText;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_subscription);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ChooseSubscriptionActivity.this, R.color.gradient_start_color));
        window.setNavigationBarColor(ContextCompat.getColor(ChooseSubscriptionActivity.this, R.color.gradient_start_color));

        initVars();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initVars() {
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.keepSynced(true);
        UID = mAuth.getCurrentUser().getUid();

        casualView = findViewById(R.id.casualView);
        professionalView = findViewById(R.id.professionalView);
        casualPrice = findViewById(R.id.casualPrice);
        casualText = findViewById(R.id.casualText);
        professionalPrice = findViewById(R.id.professionalPrice);
        professionalText = findViewById(R.id.professionalText);

        loadText();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadText() {
        long nextMonth = OffsetDateTime.now(ZoneOffset.UTC)
                .plusMonths(1)
                .toEpochSecond();


        long dv = nextMonth * 1000;// its need to be in milisecond
        Date df = new Date(dv);
        String vv = new SimpleDateFormat("MMMM yyyy").format(df);

        // Toast.makeText(this, "Next month: " + vv, Toast.LENGTH_SHORT).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            casualText.setText(Html.fromHtml("Apostle", Html.FROM_HTML_MODE_COMPACT));
        } else {
            casualText.setText(Html.fromHtml("Apostle"));
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            professionalText.setText(Html.fromHtml("Guardian", Html.FROM_HTML_MODE_COMPACT));
        } else {
            professionalText.setText(Html.fromHtml("Guardian"));
        }

        loadCustomPrice();
    }

    private void loadCustomPrice() {
        dbRef.child("Users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currency_code = snapshot.child("currency_code").getValue().toString();
                String currency_symbol = snapshot.child("currency_symbol").getValue().toString();
                String country_code = snapshot.child("country_code").getValue().toString();

                getActualPrice(currency_code, currency_symbol, country_code);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getActualPrice(String code, String symbol, String country_code) {
        double casualP = 0;
        double professionalP = 0;
        String currency = "";

        if (country_code.equals("AX")) {
            casualP = 9.99;
            professionalP = 49.99;
            currency = code;

        } else if (country_code.equals("US")) {
            casualP = 9.99;
            professionalP = 49.99;
            currency = code;
        } else if (country_code.equals("GB")) {
            casualP = 9.99;
            professionalP = 49.99;
            currency = code;
        } else if (country_code.equals("KE")) {
            casualP = 999.99;
            professionalP = 4999.99;
            currency = code;
        } else {
            casualP = 9.99;
            professionalP = 49.99;
            currency = "USD";
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            casualPrice.setText(Html.fromHtml("Spread the word<br />" + symbol + " " + casualP, Html.FROM_HTML_MODE_COMPACT));
        } else {
            casualPrice.setText(Html.fromHtml("Spread the word<br />" + symbol + " " + casualP));
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            professionalPrice.setText(Html.fromHtml("Participate in missions<br />" + symbol + " " + professionalP, Html.FROM_HTML_MODE_COMPACT));
        } else {
            professionalPrice.setText(Html.fromHtml("Participate in missions<br />" + symbol + " " + professionalP));
        }

        setOnClick(casualP, professionalP, currency, country_code);
    }

    private void setOnClick(final double casualP, final double professionalP, final String currency, final String country_code) {
        professionalView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent p_i = new Intent(ChooseSubscriptionActivity.this, PaySubscriptionActivity.class);
                Bundle p_b = new Bundle();
                p_b.putString("country", country_code);
                p_b.putString("currency", currency);
                p_b.putString("subscription", "Guardian");
                p_b.putDouble("price", professionalP);
                p_i.putExtras(p_b);
                startActivity(p_i);
            }
        });
        casualView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent p_i = new Intent(ChooseSubscriptionActivity.this, PaySubscriptionActivity.class);
                Bundle p_b = new Bundle();
                p_b.putString("country", country_code);
                p_b.putString("currency", currency);
                p_b.putString("subscription", "Apostle");
                p_b.putDouble("price", casualP);
                p_i.putExtras(p_b);
                startActivity(p_i);
            }
        });
    }
}