package org.tensorflow.lite.examples.detection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;

public class advertisement extends AppCompatActivity {
    private RewardedAd mRewardedAd;
    private final String TAG = "MainActivity";
    Button rewardbutton;
    TextView coinvalue;
    String noofcoin;
    ListView listView;
    String mTitle[] = {"Ekko","Ezreal","Fiora","Graves","Jinx","Riven","Riven","Zed"};
    String mDexcription[] = {"10000 coins required","12000 coins required","9700 coins required","25000 coins required","13500 coins required","10800 coins required","7900 coins required","40000 coins required"};
    int image[] = {R.drawable.ekko,R.drawable.ezreal,R.drawable.fiora,R.drawable.graves,R.drawable.jinx,R.drawable.riven,R.drawable.yasuo,R.drawable.zed};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement);
        rewardbutton = (Button)findViewById(R.id.rewardbutton);
        coinvalue =(TextView)findViewById(R.id.coinvaluetext);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        Query query = firebaseDatabase.child("Account").child(userid);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noofcoin =  snapshot.child("coin").getValue().toString();
                coinvalue.setText("You have "+noofcoin+" coins");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("firebasename", "error");
            }
        });


        listView = findViewById(R.id.listview);
        MyAdapter adapter = new MyAdapter(this,mTitle,mDexcription,image);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                  showalertdialog("10000");

                }
                if(position == 1){
                    showalertdialog("12000");

                }
                if(position == 2){
                    showalertdialog("9700");
                }
                if(position == 3){
                    showalertdialog("25000");
                }
                if(position == 4){
                    showalertdialog("13500");
                }
                if(position == 5){
                    showalertdialog("10000");
                }

                if(position == 6){
                    showalertdialog("7900");
                }
                if(position == 7){
                    showalertdialog("40000");
                }

            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback(){
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d("add", loadAdError.getMessage());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Log.d(TAG, "Ad was loaded.");
                    }

                });

        rewardbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRewardedAd != null) {
                    Activity activityContext = advertisement.this;
                    mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                            Log.d(TAG, "The user earned the reward.");
                            int newnoofcoin = Integer.parseInt(noofcoin) + 10;
                            myRef.child("Account").child(userid).child("coin").setValue(String.valueOf(newnoofcoin));
                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();
                        }
                    });
                } else {
                    Log.d(TAG, "The rewarded ad wasn't ready yet.");
                }
            }
        });
    }

    private void showalertdialog(String noofcoinrequired) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        AlertDialog.Builder builder = new AlertDialog.Builder(advertisement.this);
        builder.setTitle("Confirm to redeem");
        builder.setMessage("Click Yes to redeem");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int intnoofcoinhave = Integer.parseInt(noofcoin);
                int intnoofcoinrequirement = Integer.parseInt(noofcoinrequired);
                if (intnoofcoinhave < intnoofcoinrequirement)
                {
                    Toast.makeText(advertisement.this,"You don't have enough coin",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    myRef.child("Account").child(userid).child("coin").setValue(String.valueOf(intnoofcoinhave-intnoofcoinrequirement));
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String rTitle[];
        String rDescription[];
        int rImgs[];

        MyAdapter(Context c,String title[],String description[], int imgs[]){
            super(c, R.layout.row,R.id.textView1,title);
            this.context = c;
            this.rTitle = title;
            this.rDescription = description;
            this.rImgs = imgs;
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row,parent,false);
            ImageView images = row.findViewById(R.id.image);
            TextView myTitle = row.findViewById(R.id.textView1);
            TextView myDescription = row.findViewById(R.id.textView2);

            images.setImageResource(rImgs[position]);
            myTitle.setText(rTitle[position]);
            myDescription.setText(rDescription[position]);

            return row;
        }
    }
}