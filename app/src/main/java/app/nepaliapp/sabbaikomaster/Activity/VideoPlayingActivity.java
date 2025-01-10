package app.nepaliapp.sabbaikomaster.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import app.nepaliapp.sabbaikomaster.Adapter.VideoCardAdapter;
import app.nepaliapp.sabbaikomaster.R;
import app.nepaliapp.sabbaikomaster.common.CustomHttpDataSourceFactory;
import app.nepaliapp.sabbaikomaster.common.PreferencesManager;

public class VideoPlayingActivity extends AppCompatActivity {
    PlayerView playerView;
    TextView titlesView;
    RecyclerView recyclerView;
    private ExoPlayer player;
    private boolean isFullScreen = false;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_playing);
        playerView = findViewById(R.id.player_view);
        playerView.setControllerShowTimeoutMs(5000);
        playerView.setFullscreenButtonClickListener(isFullScreen -> toggleFullScreen());
        titlesView = findViewById(R.id.titleOfTheVideo);
        recyclerView = findViewById(R.id.OtherVideos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        handleIntent(intent);
    }

    @UnstableApi
    public void initializePlayer(MediaItem mediaItem) {
        if (player != null) {
            player.release();
        }

        //Security for Video Playing
        // Retrieve the JWT token
        PreferencesManager selectedAnswerStorehouse = new PreferencesManager(getApplicationContext());
        String jwtToken = selectedAnswerStorehouse.getJwtToken();

        // Create the custom HttpDataSource.Factory
        HttpDataSource.Factory dataSourceFactory = new CustomHttpDataSourceFactory(jwtToken);

        //Old Code

        player = new ExoPlayer.Builder(this)
                .setMediaSourceFactory(new DefaultMediaSourceFactory(dataSourceFactory)).build();
        playerView.setPlayer(player);
        player.addMediaItem(mediaItem);
//        for (MediaItem mediaItem : mediaItems) {
//            player.addMediaItem(mediaItem);
//        }
        player.prepare();

        player.play();

        // Set up a listener to play the next video when one finishes


//
//        player.addListener(new Player.Listener() {
//            @Override
//            public void onPlaybackStateChanged(int playbackState) {
//                if (playbackState == Player.STATE_READY && player.getCurrentMediaItem() != null) {
//                    // Update the video title when playback is ready
//                    videoTitle.setText(titles.get(currentIndex));
//                } else if (playbackState == Player.STATE_ENDED) {
//                    // Check if there is a next media item and play it
//                    if (player.hasNextMediaItem()) {
//                        currentIndex++;
//                        player.seekToNext();
//                        player.play();
//                    }
//                }
//            }
//        });


    }

    private void toggleFullScreen() {
        if (isFullScreen) {
            // Exit fullscreen
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
            }

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);


            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = (int) (200 * getApplicationContext().getResources().getDisplayMetrics().density);
            playerView.setLayoutParams(params);
            isFullScreen = false;
        } else {
            // Enter fullscreen
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            playerView.setLayoutParams(params);
            isFullScreen = true;
        }
    }


    private Map<String, String> getAuthorizationHeader() {
        PreferencesManager selectedAnswerStorehouse = new PreferencesManager(getApplicationContext());
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + selectedAnswerStorehouse.getJwtToken());
        return headers;
    }
//Handling the NewIntent Easy Methods is here..
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Update the intent
        handleIntent(intent); // Handle the new intent data
    }

    @OptIn(markerClass = UnstableApi.class)
    private void handleIntent(Intent intent) {
        String fileName = intent.getStringExtra("videoUrl");
        String title = intent.getStringExtra("title");
        String jsonArray = intent.getStringExtra("jsonArray");
        titlesView.setText(title);
        String uuid = removeExtensionOfFile(fileName);
        String link = "https://gc5tdmq1-8080.inc1.devtunnels.ms/api/range/" + uuid;

        try {
            VideoCardAdapter videoCardAdapter = new VideoCardAdapter(getApplicationContext(), new JSONArray(jsonArray));
            recyclerView.setAdapter(videoCardAdapter);
        } catch (JSONException e) {
            Toast.makeText(this, "List of Video is Damaged", Toast.LENGTH_SHORT).show();
        }
        Log.d("video Url", link);
        MediaItem mediaItem = MediaItem.fromUri(link);

        initializePlayer(mediaItem);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.release();
            player = null;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private String removeExtensionOfFile(String fileNameWithExtension) {
        String uuid = fileNameWithExtension.split("\\.")[0]; // Split by the dot and take the first part
        return uuid;
    }
}