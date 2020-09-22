package tech.berjis.illuminati;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LoadFeaturesAdapter extends PagerAdapter {

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private Context mContext;
    private List<Features> listData;

    LoadFeaturesAdapter(Context mContext, List<Features> listData) {
        this.mContext = mContext;
        this.listData = listData;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View mView = LayoutInflater.from(mContext).inflate(R.layout.features, container, false);
        final Features ld = listData.get(position);

        /*ImageView imgSlide = mView.findViewById(R.id.intro_img);
        TextView title = mView.findViewById(R.id.intro_title);
        TextView description = mView.findViewById(R.id.intro_description);

        title.setText(listData.get(position).getTitle());
        description.setText(listData.get(position).getDescription());
        imgSlide.setImageResource(listData.get(position).getScreenImg());*/

        TextView title = mView.findViewById(R.id.title);
        TextView description = mView.findViewById(R.id.description);
        TextView author = mView.findViewById(R.id.author);
        TextView time = mView.findViewById(R.id.time);

        title.setText(ld.getTitle());

        if(ld.getDescription().length() > 128){
            description.setText(ld.getDescription().substring(0, 128) + "...");
        }else{
            description.setText(ld.getDescription());
        }

        author.setText(ld.getAuthor());

        long dv = ld.getTime() * 1000;// its need to be in milisecond
        Date df = new java.util.Date(dv);
        String vv = new SimpleDateFormat("dd MMMM yyyy").format(df);
        time.setText(vv);

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent groupIntent = new Intent(mContext, FeatureView.class);
                Bundle groupBundle = new Bundle();
                groupBundle.putString("group_id", ld.getId());
                groupIntent.putExtras(groupBundle);
                mContext.startActivity(groupIntent);
            }
        });

        container.addView(mView);

        return mView;


    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View) object);

    }

}

    