package org.tensorflow.lite.examples.detection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Base64;
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


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    public static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;
    Button logoutbtn;
    ListView listView;
    String mTitle[] = {"LOL Detector","Text Recognition","Hero advise to you","Image Classification","LOL AR","LOL VR","Dialogflow","Communication","Redeem gifts"};
    String mDexcription[] = {"LOL Detector Description","Text Recognition Description","Hero advise to you","Image Classification","LOL AR Description","LOL VR Description","Dialogflow Description","Communication Description","Redeem gifts Description"};
    int image[] = {R.drawable.objectdetection,R.drawable.text,R.drawable.random,R.drawable.imageclassificationlogo,R.drawable.ar,R.drawable.vr,R.drawable.dialogflow,R.drawable.chat,R.drawable.gift};
    private byte encryptionkey[] = {9,115,51,86,105,4,-31,23,-68,88,17,20,3,-105,119,-53};
    private Cipher cipher,decipher;
    private SecretKeySpec secretKeySpec;
    ImageView myprofile;
    TextView myname;
    String name;
    FirebaseAuth auth;
    String fuid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbarcustom);
        setSupportActionBar(toolbar);
        myname = toolbar.findViewById(R.id.myTitleToolbar);
        myprofile = toolbar.findViewById(R.id.profile_image);
        //descrypttext  &&  image
        try {
            cipher = Cipher.getInstance("AES");
            decipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        secretKeySpec = new SecretKeySpec(encryptionkey,"AES");

        Intent intent = getIntent();
        fuid = intent.getStringExtra("fuid");

        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        Query query = firebaseDatabase.child("Account").child(fuid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = snapshot.child("name").getValue().toString();
                String profileimage = snapshot.child("photo").getValue().toString();
                Log.d("firebasename", name);
                try {
                    myname.setText(AESDecryptionmethod(name));
                    myprofile.setImageBitmap(AESDecryptionmethodimage(profileimage));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("firebasename", "error");
            }
        });




        listView = findViewById(R.id.listview);
        logoutbtn = (Button)findViewById(R.id.logoutbtn);
        MyAdapter adapter = new MyAdapter(this,mTitle,mDexcription,image);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    //Toast.makeText(MainActivity.this,"LOL Detector Description",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this, DetectorActivity.class));
                }
                if(position == 1){
                    startActivity(new Intent(MainActivity.this, OcrCaptureActivity.class));
                   // Toast.makeText(MainActivity.this,"Text Recognition",Toast.LENGTH_SHORT).show();
                }
                if(position == 2){
                    startActivity(new Intent(MainActivity.this,herosuggestion.class));
                    // Toast.makeText(MainActivity.this,"Text Recognition",Toast.LENGTH_SHORT).show();
                }
                if(position == 3){
                    startActivity(new Intent(MainActivity.this, imageclassification.class));
                    Toast.makeText(MainActivity.this,"Image Classification",Toast.LENGTH_SHORT).show();
                }
                if(position == 4){
                    //Toast.makeText(MainActivity.this,"LOL AR Description",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this, Arcamera.class));
                }
                if(position == 5){
                   //Toast.makeText(MainActivity.this,"LOL VR",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, showvr.class));
                }

                if(position == 6){
                    startActivity(new Intent(MainActivity.this, Dialogflowchat.class));
                    Toast.makeText(MainActivity.this,"Dialogflow",Toast.LENGTH_SHORT).show();
                }
                if(position == 7){
                    startActivity(new Intent(MainActivity.this, socketio.class));
                    Toast.makeText(MainActivity.this,"Communication",Toast.LENGTH_SHORT).show();
                }
                if(position == 8){
                    startActivity(new Intent(MainActivity.this, advertisement.class));

                }

            }
        });
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });

    }
    class MyAdapter extends ArrayAdapter<String>{
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
    private String AESDecryptionmethod(String string) throws UnsupportedEncodingException {
        byte[] EncryptByte = string.getBytes("ISO-8859-1");
        String decryptedString = string;
        byte[] decryption;
        try {
            decipher.init(cipher.DECRYPT_MODE,secretKeySpec);
            decryption = decipher.doFinal(EncryptByte);
            decryptedString = new String(decryption);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return decryptedString;


    }
    private Bitmap AESDecryptionmethodimage(String string) throws UnsupportedEncodingException {
        byte[] EncryptByte = string.getBytes("ISO-8859-1");
        String decryptedString = string;
        byte[] decryption;
        try {
            decipher.init(cipher.DECRYPT_MODE,secretKeySpec);
            decryption = decipher.doFinal(EncryptByte);
            decryptedString = new String(decryption);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        byte[] byteArray1;
        byteArray1 = Base64.decode(decryptedString, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray1, 0,
                byteArray1.length);

        return bmp;


    }
}
