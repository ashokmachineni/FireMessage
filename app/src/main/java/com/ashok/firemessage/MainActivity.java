package com.ashok.firemessage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabase;
    InterstitialAd mInterstitialAd;
    private InterstitialAd mInterstitial;
    private com.google.android.gms.ads.AdView mAdView;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    public static final String ANONYMOUS = "anonymous";
    private String mUsername;
    private String mPhotoUrl;
    public static final String CATEGORY_MOVIES = "CATEGORY_MOVIES";
    public static final String CATEGORY = "CATEGORY";

    private Map<String, List<Movie>> categoryToMovieMap = new HashMap<>();

    private final ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            categoryToMovieMap.clear();
            for (DataSnapshot snapMovies : dataSnapshot.getChildren()) {
                Movie movie = snapMovies.getValue(Movie.class);
                String category = movie.getCategory().toUpperCase();
                List<Movie> categoryBlogList = categoryToMovieMap.get(category);
                if (categoryBlogList == null) {
                    categoryBlogList = new ArrayList<>();
                    categoryToMovieMap.put(category, categoryBlogList);
                }
                categoryBlogList.add(movie);
            }
            provideAdapter();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void provideAdapter() {
        final List<String> categories = new ArrayList<>(categoryToMovieMap.keySet());
        mRecyclerView.setAdapter(new RecyclerView.Adapter<CategoryViewHolder>() {
            @Override
            public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category, parent, false));
            }

            @Override
            public void onBindViewHolder(CategoryViewHolder holder, int position) {
                holder.bind(categories.get(position));
            }

            @Override
            public int getItemCount() {
                return categories.size();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUsername = ANONYMOUS;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
            Log.i("User Name:",mUsername);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Movie");
        mRecyclerView = (RecyclerView) findViewById(R.id.recview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDatabase.addListenerForSingleValueEvent(valueEventListener);

        Exception exception = new Exception("Oops! Firebase non-fatal error!");
        FirebaseCrash.report(exception);

       /* mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();


            }
        });


        requestNewInterstitial();*/
        mAdView = (com.google.android.gms.ads.AdView) findViewById(R.id.adView);
        mAdView.loadAd(new AdRequest.Builder().build());

        mInterstitial = new InterstitialAd(this);
        mInterstitial.setAdUnitId("ca-app-pub-4981485298642595/6250525668");
        mInterstitial.loadAd(new AdRequest.Builder().build());

        mInterstitial.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdLoaded() {
                // TODO Auto-generated method stub
                super.onAdLoaded();
                if (mInterstitial.isLoaded()) {
                    mInterstitial.show();
                }
            }
        });

    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    private class CategoryViewHolder extends  RecyclerView.ViewHolder {

        private final TextView view;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            view = (TextView) itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCategoryBlogs(view.getText().toString());
                }
            });
        }

        public void bind(String category) {
            view.setText(category);
        }
    }

    private void showCategoryBlogs(String s) {
        ArrayList<Movie> categoryMovies = (ArrayList<Movie>)categoryToMovieMap.get(s);
        Intent intent = new Intent(this, CategoryMovieActivity.class);
        intent.putExtra(CATEGORY_MOVIES, categoryMovies);
        intent.putExtra(CATEGORY, s);
        startActivity(intent);
    }
}