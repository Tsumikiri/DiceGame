package com.example.ew8217sh.dicegame;

import android.app.ListFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class PlayersFragment extends ListFragment {

    public PlayersFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_players, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayList<DiceGamePlayer> playersArrayList = ((DiceGameApplication)getActivity().getApplication()).getPlayers();
        String[] playerNamesArray = new String[playersArrayList.size()];
        for (int i = 0; i < playersArrayList.size(); i++) {
            playerNamesArray[i] = String.format(Locale.US, getString(R.string.player_list_item), playersArrayList.get(i).getName(), playersArrayList.get(i).getPoints());
            //playerNamesArray[i] = playersArrayList.get(i).getName();
        }
        setListAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_activated_1, playerNamesArray));
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        int highlightedPlayer = getActivity().getIntent().getIntExtra("player", 0);
        if (highlightedPlayer < 0) {
            highlightedPlayer = 0;
        } else if (highlightedPlayer >= playersArrayList.size()) {
            showWinner();
        }
        getListView().setItemChecked(highlightedPlayer, true);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Intent intent = new Intent(getActivity(), DiceActivity.class);
            intent.putExtra("player", position);
            startActivity(intent);
            getActivity().finish();
        } else {
            DiceFragment diceFragment = (DiceFragment)getFragmentManager().findFragmentById(R.id.fragment_dice);
            diceFragment.reset();
            diceFragment.updatePlayer(position);
        }
    }

    public void nextPlayer() {
        int position = getListView().getCheckedItemPosition();
        getListView().setItemChecked(position, false);
        if (position + 1 >= ((DiceGameApplication)getActivity().getApplication()).getPlayers().size()) {
            showWinner();
        } else {
            getListView().setItemChecked(position + 1, true);
            onListItemClick(getListView(), getListView().getChildAt(position + 1), position + 1, 0);
        }
    }

    public void showWinner() {
        ArrayList<DiceGamePlayer> highScorePlayers = ((DiceGameApplication)getActivity().getApplication()).getHighScorePlayers();
        if (highScorePlayers.size() > 1) {
            for (int i = 0; i < highScorePlayers.size(); i++) {
                highScorePlayers.get(i).setClearable(false);
            }
        }
        Toast.makeText(getActivity().getApplicationContext(), String.format(Locale.US, getString(highScorePlayers.size() > 1 ? R.string.tie_toast_format : R.string.winner_toast_format), DiceGamePlayer.getPlayersArrayListAsString(highScorePlayers)), Toast.LENGTH_LONG).show();
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

}
