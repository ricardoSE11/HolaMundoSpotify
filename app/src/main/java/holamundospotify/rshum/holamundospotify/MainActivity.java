package holamundospotify.rshum.holamundospotify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

public class MainActivity extends Activity implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback {

    private static final String CLIENT_ID = "1e317b7bdbf9487382a91f24d531bc98";
    private static final String REDIRECT_URI = "https://accounts.spotify.com/authorize";

    //Vars
    private Player mPlayer;
    private String playlistURI = "spotify:album:5IwOZQP8N8aklileNY9KXu";
    private int currentSongIndex;
    private boolean isPlaying;
    private boolean isPaused;

    // UI Widgets
    ImageButton btnPlay;
    ImageButton btnPause;
    ImageButton btnNextSong;
    ImageButton btnPreviousSong;

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Widget "identification"
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnNextSong = findViewById(R.id.btnNextSong);
        btnPreviousSong = findViewById(R.id.btnPreviousSong);

        this.currentSongIndex = 0;
        this.isPlaying = false;
        this.isPaused = false;

        btnPause.setVisibility(View.GONE);


        // The only thing that's different is we added the 5 lines below.
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);


        // Button functionality
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //@Params = Callback, URI , index , positionInMs
                System.out.println("LOG: Attempting to [PLAY] song");
                if (!isPlaying && !isPaused)
                {
                    mPlayer.playUri(null, playlistURI, currentSongIndex, 0);
                    isPlaying = true;
                    btnPlay.setVisibility(View.GONE);
                    btnPause.setVisibility(View.VISIBLE);
                }
                else if(isPaused){
                    System.out.println("LOG: Attempting to [RESUME] song");
                    mPlayer.resume(null);
                    isPlaying = true;
                    btnPlay.setVisibility(View.GONE);
                    btnPause.setVisibility(View.VISIBLE);
                }

            }
        });

        btnNextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("LOG: Attempting to [PLAY NEXT SONG]. Current index = " + currentSongIndex);
                currentSongIndex++;
                mPlayer.playUri(null, playlistURI, currentSongIndex, 0);
            }
        });

        btnPreviousSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("LOG: Attempting to [PLAY PREVIOUS SONG]. Current index = " + currentSongIndex);
                currentSongIndex--;
                mPlayer.playUri(null, playlistURI, currentSongIndex, 0);
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("LOG: Attempting to [PAUSE] the song");
                mPlayer.pause(null);
                isPlaying = false;
                isPaused = true;
                btnPause.setVisibility(View.GONE);
                btnPlay.setVisibility(View.VISIBLE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        // The next 19 lines of the code are what you need to copy & paste! :)
        if (requestCode == REQUEST_CODE)
        {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(MainActivity.this);
                        mPlayer.addNotificationCallback(MainActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    // --- Homework ---
    public void play()
    {

    }

    public void pause(){}

    public void nextSong(){}

    public void previousSong(){}

    // --- Homework ---

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("MainActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");


        // This is the line that plays a song.
        //mPlayer.playUri(null, "spotify:track:6cAxrJHIgrBbsd4nhDWucX", 0, 0);
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error var1) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }
}