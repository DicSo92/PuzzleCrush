package com.game.puzzlecrush;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button newGame;
    private Button testGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.newGame = (Button) findViewById(R.id.btn_newGame);

        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newGameActivity = new Intent(getApplicationContext(), GameActivity.class);
                startActivity(newGameActivity);
                finish();
            }
        });


        this.testGame = (Button) findViewById(R.id.btn_test);
        testGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newTestActivity = new Intent(getApplicationContext(), NewActivity.class);
                startActivity(newTestActivity);
                finish();
            }
        });

    }
}
