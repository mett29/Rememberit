package com.app.mattiamancassola.rememberit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Random;


public class MainActivity extends Activity {

    public static int record = 0;
    static int count = 0;
    static int howManyBlink = 1;
    static int countPressed = 0;
    static int howManyPressed = 0;
    int[] idArray = new int[100];
    int[] userChoice = new int[100];
    boolean canClick = false;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                setContentView(R.layout.activity_main);
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                setContentView(R.layout.normal_layout);
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                setContentView(R.layout.small_layout);
                break;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = preferences.edit();

        record = preferences.getInt("record", 0);

        howManyBlink = record + 1;

        final Button startButton = (Button) findViewById(R.id.start);
        TextView textView1 = (TextView) findViewById(R.id.textView);
        textView1.setText("STEP " + howManyBlink);

        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Azzero array e altre variabili
                idArray = new int[100];
                userChoice = new int[100];
                howManyPressed = 0;
                countPressed = 0;

                //Ripristino colore bottoni
                // TODO


                if (startButton.getText().equals("NEXT STEP")) {
                    TextView textView1 = (TextView) findViewById(R.id.textView);
                    textView1.setText("STEP " + howManyBlink);

                    if (record < howManyBlink) {
                        record = howManyBlink;

                        editor.putInt("record", record); // value to store
                        editor.commit();
                    }
                }

                blinkButtons();
            }
        });
    }

    private void blinkButtons() {
        canClick = false;
        Random r = new Random();

        int randomNumber = r.nextInt(13 - 1) + 1;

        int ID = this.getResources().getIdentifier("button" + randomNumber, "id", getPackageName());
        final Button btn = (Button) findViewById(ID);

        idArray[count] = ID;

        final Animation myFadeInAnimation = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.fade);

        btn.startAnimation(myFadeInAnimation);
        count++;

        if (count < howManyBlink) {
            new CountDownTimer(1000, 1000) {

                public void onTick(long millisUntilFinished) {
                    //NADA
                }

                public void onFinish() {
                    blinkButtons();
                }
            }.start();
        } else {
            //Finito di blinkare

            //Abilito touch
            canClick = true;

            //L'utente deve selezionare i bottoni
            Button startButton = (Button) findViewById(R.id.start);
            startButton.setText("NEXT STEP");
            startButton.setEnabled(false);

            count = 0;
            howManyBlink++;

        }
    }

    public void onClick(View v) {
        if (canClick) {
            howManyPressed++;

            //Cambio colore al bottone premuto
            Button btnClicked = (Button) findViewById(v.getId());
            GradientDrawable bgShape = (GradientDrawable)btnClicked.getBackground();
            bgShape.setColor(randomColor());

            //Se l'utente ne ha premuto meno di quanti non ne siano blinkati
            if (howManyPressed < howManyBlink - 1) {
                userChoice[countPressed] = v.getId();
                countPressed++;
            } else {
                userChoice[countPressed] = v.getId();
                countPressed++;
                canClick = false;
                //Controllo se la sequenza � esatta
                if (Arrays.equals(userChoice, idArray)) {
                    //SEQUENZA ESATTA
                    Button startButton = (Button) findViewById(R.id.start);
                    startButton.setEnabled(true);
                } else {
                    //SEQUENZA SBAGLIATA
                    //RESET
                    Button startButton = (Button) findViewById(R.id.start);
                    startButton.setText("TRY AGAIN");
                    howManyBlink--;
                    startButton.setEnabled(true);
                }
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "WAIT", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    int randomColor() {
        Random r = new Random();
        int red = r.nextInt(256);
        int green = r.nextInt(256);
        int blue = r.nextInt(256);
        return Color.rgb(red, green, blue);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra("record", record);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
