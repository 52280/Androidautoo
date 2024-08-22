package com.example.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class MainActivity extends AppCompatActivity {

    private RewardedAd rewardedAd;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private Button btn_interstialads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Start the service
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        }
        MobileAds.initialize(this, initializationStatus -> {});
        mAdView = findViewById(R.id.adView);

        // Create an ad request and load the ad
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        btn_interstialads = findViewById(R.id.dads);
        btn_interstialads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadInterstitialAd();
            }
        });


        // Check internet connection and load the ad
        if (NetworkUtil.isConnected(this)) {
            loadRewardedAd();
        } else {
            Log.e("Ads","Loading ads failed");
            // Handle no internet connection scenario
            // You might want to notify the user or attempt to load the ad later
        }
    }
    private void loadRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-9580559852193051/8224401228", adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                rewardedAd = ad;
                showRewardedAd();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                // Handle error
            }
        });
    }

    private void showRewardedAd() {
        if (rewardedAd != null) {
            rewardedAd.show(this, rewardItem -> {
                // Handle the reward
            });
        }
    }

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-9580559852193051/5598237880", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                          // Show the ad
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(MainActivity.this);
                        }
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        mInterstitialAd = null;
                        Log.d("Ad", "Failed to load interstitial ad: " + adError.getMessage());
                    }
                });
    }
}
