package com.example.ew8217sh.dicegame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((DiceGameApplication)getApplication()).clearPlayers();
        updatePlayersList();
    }

    public void onSubmitPlayer(View v) {
        EditText nameEdit = (EditText)findViewById(R.id.nameEdit);
        ((DiceGameApplication)getApplication()).getPlayers().add(new DiceGamePlayer(nameEdit.getText().toString()));
        updatePlayersList();
        nameEdit.setText("");
    }

    public void onStartGame(View v) {
        Intent intent = new Intent(this, PlayersActivity.class);
        startActivity(intent);
        finish();
    }

    protected void updatePlayersList() {
        TextView playersLabel = (TextView)findViewById(R.id.playersLbl);
        Button startButton = (Button)findViewById(R.id.startBtn);
        if (((DiceGameApplication)getApplication()).getPlayers().isEmpty()) {
            playersLabel.setText("");
            startButton.setEnabled(false);
        } else {
            playersLabel.setText(String.format(Locale.US, getString(R.string.players_lbl_format), ((DiceGameApplication)getApplication()).getPlayersAsString()));
            startButton.setEnabled(true);
        }
    }
}
