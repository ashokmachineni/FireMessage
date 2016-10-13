package com.ashok.firemessage;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CategoryMovieActivity extends AppCompatActivity {

    private RecyclerView mCategoryMovieList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_movie_activity);
        mCategoryMovieList = (RecyclerView) findViewById(R.id.category_blog_list);
        mCategoryMovieList.setHasFixedSize(true);
        mCategoryMovieList.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        final List<Movie> categoryMovies = (ArrayList<Movie>)getIntent().getSerializableExtra(MainActivity.CATEGORY_MOVIES);
        setTitle(getIntent().getStringExtra(MainActivity.CATEGORY));
        mCategoryMovieList.setAdapter(new RecyclerView.Adapter<MovieViewHolder>() {
            @Override
            public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MovieViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list, parent, false));
            }

            @Override
            public void onBindViewHolder(MovieViewHolder holder, int position) {
                final Movie movie = categoryMovies.get(position);
                holder.setImage(movie.getImage());
                holder.setTitle(movie.getTitle());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /** PLAYER */
                        UriSample uriSample = new UriSample("Test", null, null, null, false, movie.getLink(), null);
                        startActivity(uriSample.buildIntent(getApplicationContext()));
                    }
                });
            }

            @Override
            public int getItemCount() {
                return categoryMovies.size();
            }
        });
    }


    /**
     * EXOPLAYER
     */
    private abstract static class Sample {

        public final String name;
        public final boolean preferExtensionDecoders;
        public final UUID drmSchemeUuid;
        public final String drmLicenseUrl;
        public final String[] drmKeyRequestProperties;

        public Sample(String name, UUID drmSchemeUuid, String drmLicenseUrl,
                      String[] drmKeyRequestProperties, boolean preferExtensionDecoders) {
            this.name = name;
            this.drmSchemeUuid = drmSchemeUuid;
            this.drmLicenseUrl = drmLicenseUrl;
            this.drmKeyRequestProperties = drmKeyRequestProperties;
            this.preferExtensionDecoders = preferExtensionDecoders;

        }

        public Intent buildIntent(Context context) {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra(PlayerActivity.PREFER_EXTENSION_DECODERS, preferExtensionDecoders);
            if (drmSchemeUuid != null) {
                intent.putExtra(PlayerActivity.DRM_SCHEME_UUID_EXTRA, drmSchemeUuid.toString());
                intent.putExtra(PlayerActivity.DRM_LICENSE_URL, drmLicenseUrl);
                intent.putExtra(PlayerActivity.DRM_KEY_REQUEST_PROPERTIES, drmKeyRequestProperties);
            }
            return intent;
        }
    }

    private static final class UriSample extends Sample {

        public final String uri;
        public final String extension;

        public UriSample(String name, UUID drmSchemeUuid, String drmLicenseUrl,
                         String[] drmKeyRequestProperties, boolean preferExtensionDecoders, String uri,
                         String extension) {
            super(name, drmSchemeUuid, drmLicenseUrl, drmKeyRequestProperties, preferExtensionDecoders);
            this.uri = uri;
            this.extension = extension;
        }

        @Override
        public Intent buildIntent(Context context) {
            return super.buildIntent(context)
                    .setData(Uri.parse(uri))
                    .putExtra(PlayerActivity.EXTENSION_EXTRA, extension)
                    .setAction(PlayerActivity.ACTION_VIEW);
        }
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title) {
            TextView movie_title = (TextView) mView.findViewById(R.id.movie_title);
            movie_title.setText(title);
        }

        public void setImage(String image) {
            ImageView movie_image = (ImageView) mView.findViewById(R.id.movie_image);
            Picasso.with(mView.getContext()).load(image).into(movie_image);
        }
    }
}
