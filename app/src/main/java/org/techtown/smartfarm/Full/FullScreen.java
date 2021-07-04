package org.techtown.smartfarm.Full;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.FirebaseFirestore;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import org.techtown.smartfarm.R;

public class FullScreen extends AppCompatActivity {

    SimpleExoPlayer player;
    PlayerView playerView;
    TextView textView;
    String title, url;
    boolean playWhenReady = false;
    int currentWindow = 0;
    long playBackPosition = 0;

    boolean fullscreen = false;
    ImageView fullScreenButton;

    RecyclerView pigLists;
    FirebaseFirestore fStore;
    FirestoreRecyclerAdapter<Pig, PigViewHolder> pigAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        playerView = findViewById(R.id.fullscreen_exoplayer);
        textView = findViewById(R.id.fullscreen_videoName);

        Intent intent = getIntent();
        title = intent.getExtras().getString("name");
        textView.setText(title);
        url = intent.getExtras().getString("url");

        fStore = FirebaseFirestore.getInstance();
        Query query = fStore.collection("pig").document("public").collection(title).orderBy("id", Query.Direction.ASCENDING);

        // 영상 전체화면으로 확장하고 축소하기
        fullScreenButton = playerView.findViewById(R.id.exoplayer_fullscreen_icon);
        fullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fullscreen) {
                    fullScreenButton.setImageDrawable(ContextCompat.getDrawable(FullScreen.this, R.drawable.ic_fullscreen_expand));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().show();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = (int) (200 * getApplicationContext().getResources().getDisplayMetrics().density);
                    playerView.setLayoutParams(params);
                    fullscreen = false;
                    textView.setVisibility(View.VISIBLE);
                } else {
                    fullScreenButton.setImageDrawable(ContextCompat.getDrawable(FullScreen.this, R.drawable.ic_fullscreen_shrink));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().hide();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = params.MATCH_PARENT;
                    playerView.setLayoutParams(params);
                    fullscreen = true;
                    textView.setVisibility(View.INVISIBLE);  // 가로 전체 화면 모드에서 텍스트 사라지게 하기
                }
            }
        });

        FirestoreRecyclerOptions<Pig> pigSets = new FirestoreRecyclerOptions.Builder<Pig>()
                .setQuery(query, Pig.class)
                .build();

        pigAdapter = new FirestoreRecyclerAdapter<Pig, PigViewHolder>(pigSets) {
            @Override
            protected void onBindViewHolder(@NonNull PigViewHolder holder, int position, @NonNull Pig model) {
                holder.pigId.setText(model.getId());
                holder.pigTemp.setText(model.getTemp());
                if(Double.parseDouble(model.getTemp()) >= 39.8) {
                    holder.pig_state.setVisibility(View.VISIBLE);
                }
                else {
                    holder.pig_state.setVisibility(View.INVISIBLE);
                }

                if (model.getCondition().equals("attack") == true) {
                    holder.pig_healing.setVisibility(View.INVISIBLE);
                    holder.pig_warning.setVisibility(View.VISIBLE);
                }
                else if (model.getCondition().equals("heal") == true){
                    holder.pig_warning.setVisibility(View.INVISIBLE);
                    holder.pig_healing.setVisibility(View.VISIBLE);
                }
                else {
                    holder.pig_warning.setVisibility(View.INVISIBLE);
                    holder.pig_healing.setVisibility(View.INVISIBLE);
                }
            }

            @NonNull
            @Override
            public PigViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pig, parent,false);
                return new PigViewHolder(view);
            }
        };

        pigLists = findViewById(R.id.fullscreen_recyclerview);
        pigLists.setLayoutManager(new LinearLayoutManager(FullScreen.this));
        pigLists.setAdapter(pigAdapter);
    }

    // 리스트 클릭하면 영상 크게 보기 ################################################################
    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory("video");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        Uri uri = Uri.parse(url);
        MediaSource mediaSource = buildMediaSource(uri);
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playBackPosition);
        player.prepare(mediaSource, false, false);
    }

    @Override
    protected void onStart() {
        super.onStart();

        pigAdapter.startListening();
        if(Util.SDK_INT >= 26) {
            initializePlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(Util.SDK_INT >= 26 || player == null) {
            initializePlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(Util.SDK_INT >= 26) {
            releasePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        pigAdapter.stopListening();
        if(Util.SDK_INT >= 26) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playBackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player = null;
        }
    }
    // #############################################################################################

    @Override  // 뒤로가기 버튼 누르면 영상 자동으로 재생 멈춤
    public void onBackPressed() {
        super.onBackPressed();

        player.stop();
        releasePlayer();

        final Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    public class PigViewHolder extends RecyclerView.ViewHolder {
        TextView pigId, pigTemp;
        View view;
        CardView mCardView;
        ImageView pig_state, pig_warning, pig_healing;

        public PigViewHolder(@NonNull View itemView) {
            super(itemView);

            pigId = itemView.findViewById(R.id.pig_id);
            pigTemp = itemView.findViewById(R.id.pig_temp);
            mCardView = itemView.findViewById(R.id.pigCard);
            pig_state = itemView.findViewById(R.id.pig_state_img);
            pig_warning = itemView.findViewById(R.id.pig_warning_img);
            pig_healing = itemView.findViewById(R.id.pig_healing_img);
            view = itemView;
        }
    }
}