package org.tensorflow.lite.examples.detection;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class herosuggestion extends AppCompatActivity {

    SurfaceView mCameraView;
    TextView mTextView;
    CameraSource mCameraSource;
    String[] Assassins = {"Ahri","Akali","Ekko","Evelynn","Fiora","Fizz","Gwen","Irelia","Jax","Kassadin","Katarina","Kayn","Kha'Zix","LeBlanc","Lee Sin","Malzahar","Master Yi","Nidalee","Nocturne","Pantheon","Pyke","Qiyana","Quinn","Rengar","Riven","Shaco","Sylas","Talon","Teemo","Tristana","Tryndamere","Twitch","Vayne","Vi","Viego","Xin Zhao","Yasuo", "Yone","Zed"};
    String[] Frighters = {"Aatrox","Blitzcrank","Camille","Darius","Diana","Dr. Mundo","Ekko","Elise","Fiora","Fizz","Gangplank","Garen","Gnar","Gragas","Gwen","Hecarim","Illaoi","Irelia","Jarvan IV","Jax","Jayce","Kayle","Kayn","Kled","Lee Sin","Lillia","Malphite","Master Yi","Mordekaiser","Nasus","Nautilus","Nocturne","Nunu & Willump","Olaf","Ornn","Pantheon","Poppy","Qiyana","Rammus","Rek'Sai","Renekton","Rengar","Riven","Rumble","Ryze","Sejuani","Sett","Shyvana","Singed","Sion","Skarner","Swain","Taric","Thresh","Trundle","Tryndamere","Udyr","Urgot","Vi","Viego","Volibear","Warwick","Wukong","Xin Zhao","Yasuo","Yone","Yorick","Zac"};
    String[] Mages = {"Ahri","Amumu","Anivia","Annie","Aurelion Sol","Azir","Bard","Brand","Cassiopeia","Cho'Gath","Diana","Elise","Evelynn","Ezreal","Fiddlesticks","Galio","Gragas","Heimerdinger","Ivern","Janna","Jhin","Karma","Karthus","Kassadin","Katarina","Kennen","Kog'Maw","LeBlanc","Lillia","Lissandra","Lulu","Lux","Malzahar","Maokai","Morgana","Nami","Neeko","Nidalee","Orianna","Rumble","Ryze","Seraphine","Sona","Soraka","Swain","Sylas","Syndra","Taliyah","Twisted Fate","Varus","Veigar","Vel'Koz","Viktor","Vladimir","Xerath","Yuumi","Ziggs","Zilean","Zoe","Zyra"};
    String[] Marksman = {"Aphelios", "Ashe", "Caitlyn", "Corki","Draven","Ezreal","Jhin","Jinx","Kai'Sa","Kalista","Kindred","Kog'Maw","Lucian","Miss Fortune","Samira","Senna","Sivir","Tristana","Twitch","Varus","Vayne","Xayah"};
    String[] Supports = {"Alistar","Anivia","Ashe","Bard","Braum","Fiddlesticks","Heimerdinger","Ivern","Janna","Karma","Kayle","Leona","Lulu","Lux","Morgana","Nami","Neeko","Orianna","Pyke","Rakan","Senna","Seraphine","Sona","Soraka","Syndra","Tahm Kench","Taliyah","Taric","Thresh","Yuumi","Zilean","Zoe","Zyra"};
    String[] Tanks = {"Aatrox","Alistar","Amumu","Blitzcrank","Braum","Camille","Cho'Gath","Darius","Dr. Mundo","Galio","Garen","Gnar","Hecarim","Illaoi","Jarvan IV","Kled","Leona","Malphite","Maokai","Nasus","Nautilus","Nunu & Willump","Olaf","Ornn","Poppy","Rammus","Rell","Renekton","Sejuani","Sett","Shen","Shyvana","Singed","Sion","Skarner","Tahm Kench","Trundle","Udyr","Urgot","Volibear","Warwick","Wukong","Yorick","Zac"};
    boolean checkdetect = false;
    String creditcardnostring;
    String creditcarddatestring;
    String creditcardcvcstring;

    private static final String TAG = "MainActivity";
    private static final int requestPermissionID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.herosuggestion);

        mCameraView = findViewById(R.id.surfaceView);
        mTextView = findViewById(R.id.text_view);

        startCameraSource();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != requestPermissionID) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mCameraSource.start(mCameraView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startCameraSource() {

        //Create the TextRecognizer
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies not loaded yet");
        } else {

            //Initialize camerasource to use high resolution and set Autofocus on.
            mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();

            /**
             * Add call back to SurfaceView and check if camera permission is granted.
             * If permission is granted we can start our cameraSource and pass it to surfaceView
             */
            mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(herosuggestion.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    requestPermissionID);
                            return;
                        }
                        mCameraSource.start(mCameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mCameraSource.stop();
                }
            });

            //Set the TextRecognizer's Processor.
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }

                /**
                 * Detect all the text from camera using TextBlock and the values into a stringBuilder
                 * which will then be set to the textView.
                 * */
                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0 ){

                        mTextView.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for(int i=0;i<items.size();i++){
                                    /*
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                     */
                                    String myitem = items.valueAt(i).getValue();
                                    Pattern marksman,assassins,fighters,mages,supports,tanks,octopus,studentcard,creditcard;
                                    Matcher marksmanm,assassinsm,fightersm,magesm,supportsm,tanksm,octopusm,studentcardm,creditcardm;
                                    String aftertrim = myitem.trim();
                                    Random rand = new Random();
                                    //credit card
                                    creditcard= Pattern.compile("[0-9]{4}\\p{Blank}[0-9]{4}\\p{Blank}[0-9]{4}\\p{Blank}[0-9]{4}");
                                    creditcardm= creditcard.matcher(aftertrim);
                                    //Octopus Card
                                    octopus= Pattern.compile("[0-9]{8}\\p{Blank}\\p{Punct}[0-9]\\p{Punct}");
                                    octopusm= octopus.matcher(myitem);
                                    //Student Card
                                    studentcard= Pattern.compile("[0-9]{9}");
                                    studentcardm= studentcard.matcher(myitem);
                                    //assassins
                                    assassins= Pattern.compile("[aA][sS][sS][aA][sS][sS][iI][nN]");
                                    assassinsm = assassins.matcher(myitem);
                                    //fighters
                                    fighters= Pattern.compile("[fF][iI][gG][hH][tT][eE][rR]");
                                    fightersm = fighters.matcher(myitem);
                                    //mages
                                    mages= Pattern.compile("[mM][aA][gG][eE]");
                                    magesm = mages.matcher(myitem);
                                    //marksmen
                                    marksman = Pattern.compile("[mM][aA][rR][kK][sS][mM][aAeE][nN]");
                                    marksmanm = marksman.matcher(myitem);
                                    //supports
                                    supports= Pattern.compile("[sS][uU][pP][pP][oO][rR][tT]");
                                    supportsm = supports.matcher(myitem);
                                    //tanks
                                    tanks= Pattern.compile("[tT][aA][nN][kK]");
                                    tanksm = tanks.matcher(myitem);
                                    Log.i(TAG, "receive text " + myitem);
                                    Log.i(TAG, "aftertrim text" + aftertrim);
                                    if ( assassinsm.find()){
                                        int int_random = rand.nextInt(Assassins.length);
                                        stringBuilder.append("This is assassin"+"\nI suggest you play "+Assassins[int_random]);
                                        checkdetect = true;
                                    }else if (fightersm.find())
                                    {
                                        int int_random = rand.nextInt(Frighters.length);
                                        stringBuilder.append("This is fighter"+"\nI suggest you play "+Frighters[int_random]);
                                        checkdetect = true;
                                    }
                                    else if (magesm.find())
                                    {
                                        int int_random = rand.nextInt(Mages.length);
                                        stringBuilder.append("This is magesm"+"\nI suggest you play "+Mages[int_random]);
                                        checkdetect = true;
                                    }
                                    else if (marksmanm.find())
                                    {
                                        int int_random = rand.nextInt(Marksman.length);
                                        stringBuilder.append("This is marksman"+"\nI suggest you play "+Marksman[int_random]);
                                        checkdetect = true;
                                    }
                                    else if (supportsm.find())
                                    {
                                        int int_random = rand.nextInt(Supports.length);
                                        stringBuilder.append("This is support"+"\nI suggest you play "+Supports[int_random]);
                                        checkdetect = true;
                                    }
                                    else if (tanksm.find())
                                    {
                                        int int_random = rand.nextInt(Tanks.length);
                                        stringBuilder.append("This is tank"+"\nI suggest you play "+Tanks[int_random]);
                                        checkdetect = true;
                                    }
                                    else if ( studentcardm.find())
                                    {
                                        stringBuilder.append("Student no:"+studentcardm.group());

                                    }
                                    else if(octopusm.find())
                                    {
                                        stringBuilder.append("Octopus card no:"+myitem);
                                    }
                                    else if(creditcardm.find())
                                    {
                                        stringBuilder.append("Credit card no:"+aftertrim);
                                    }


                                }
                                mTextView.setText(stringBuilder.toString());
                                if (checkdetect == true){
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    checkdetect = false;
                                }
                            }
                        });
                    }
                }
            });
        }
    }
}
