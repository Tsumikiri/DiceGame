package com.example.ew8217sh.dicegame;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.Locale;
import java.util.Random;

public class DiceFragment extends Fragment {

    public static final int maxTries = 3; //Number of tries to allow (constant)
    private final Random randomDie = new Random(System.currentTimeMillis()); //Random number generator using system clock for the seed
    private int[] dieDrawables = { R.drawable.die1, R.drawable.die2, R.drawable.die3, R.drawable.die4, R.drawable.die5, R.drawable.die6 }; //Array of drawable dice (useful for getting image by index number or for looping)
    private int[] dieButtons = { R.id.die_btn_1, R.id.die_btn_2, R.id.die_btn_3, R.id.die_btn_4, R.id.die_btn_5 }; //Array of button ids (useful for getting buttons by index number or for looping)
    private int playerIndex = -1; //Index of the current player in the application's player list; -1 for none
    private DiceGamePlayer player; //The current player object

    public DiceFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dice, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Check for the PlayersFragment (e.g. user navigated to the DiceActivity in portrait, then changed to landscape - we need to change to a PlayersActivity in landscape)
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && getFragmentManager().findFragmentById(R.id.fragment_players) == null) {
            startActivity(new Intent(getActivity(), PlayersActivity.class));
            getActivity().finish();
        }
        updatePlayer(savedInstanceState != null && savedInstanceState.containsKey("player") ? savedInstanceState.getInt("player") : getActivity().getIntent().getIntExtra("player", playerIndex));
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("player", playerIndex);
    }

    /**
     * Call to update the player
     * Reconfigures the UI appropriately
     * @param p The index of the player in the DiceGameApplication
     */
    public void updatePlayer(int p) {
        playerIndex = p;
        if (playerIndex >= 0 && playerIndex < ((DiceGameApplication) getActivity().getApplication()).getPlayers().size()) {
            player = ((DiceGameApplication) getActivity().getApplication()).getPlayers().get(playerIndex);
            if (player.dice == null || player.dice.length < dieButtons.length) {
                player.dice = new int[dieButtons.length];
            } else {
                for (int i = 0; i < dieButtons.length; i++) {
                    setDieButton(i, player.dice[i]);
                }
            }
            for (int j = 0; j < dieButtons.length; j++) {
                getActivity().findViewById(dieButtons[j]).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int value = getDieValueById(v.getId());
                        View savedView;
                        switch (value) {
                            case 6:
                                savedView = getActivity().findViewById(R.id.saved_6);
                                if (savedView.getVisibility() != View.VISIBLE) {
                                    savedView.setVisibility(View.VISIBLE);
                                    v.setVisibility(View.INVISIBLE);
                                }
                                break;
                            case 5:
                                savedView = getActivity().findViewById(R.id.saved_5);
                                if (savedView.getVisibility() != View.VISIBLE && getActivity().findViewById(R.id.saved_6).getVisibility() == View.VISIBLE) {
                                    savedView.setVisibility(View.VISIBLE);
                                    v.setVisibility(View.INVISIBLE);
                                }
                                break;
                            case 4:
                                savedView = getActivity().findViewById(R.id.saved_4);
                                if (savedView.getVisibility() != View.VISIBLE && getActivity().findViewById(R.id.saved_6).getVisibility() == View.VISIBLE && getActivity().findViewById(R.id.saved_5).getVisibility() == View.VISIBLE) {
                                    savedView.setVisibility(View.VISIBLE);
                                    v.setVisibility(View.INVISIBLE);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
            getActivity().findViewById(R.id.roll_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (player.getTries() < maxTries) {
                        player.incrementTries();
                        player.setPoints(roll());
                        updateLabels();
                    }
                }
            });
            getActivity().findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        Intent intent = new Intent(getActivity(), PlayersActivity.class);
                        intent.putExtra("player", getActivity().getIntent().getIntExtra("player", -1) + 1);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        PlayersFragment playersFragment = (PlayersFragment) getFragmentManager().findFragmentById(R.id.fragment_players);
                        playersFragment.nextPlayer();
                    }
                }
            });
            updateLabels();
            if (getView() != null) {
                getView().setVisibility(View.VISIBLE);
            }
        } else if (getView() != null) {
            getView().setVisibility(View.GONE);
        }
    }

    /**
     * Call to reset the UI
     * Brings back clicked die buttons and clears their images so they are ready for a new roll
     * Clears saved dice
     */
    public void reset() {
        for (int i = 0; i < dieButtons.length; i++) {
            getActivity().findViewById(dieButtons[i]).setVisibility(View.VISIBLE);
            ((ImageButton)getActivity().findViewById(dieButtons[i])).setImageDrawable(null);
        }
        int[] savedViews = { R.id.saved_6, R.id.saved_5, R.id.saved_4 };
        for (int i = 0; i < savedViews.length; i++) {
            getActivity().findViewById(savedViews[i]).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Uses the visibility of die buttons and saved dice views to determine the player's current score
     * @return The player's current score
     */
    public int roll() {
        player.dice = new int[dieButtons.length];
        int nextSave = 6;
        boolean found6 = false;
        boolean found5 = false;
        boolean found4 = false;
        int total = 0;
        for (int i = 0; i < dieButtons.length; i++) {
            if (isDieButtonVisible(i)) {
                player.dice[i] = randomDie.nextInt(6) + 1;
                setDieButton(i, player.dice[i]);
            } else if ((nextSave == 6 && getActivity().findViewById(R.id.saved_6).getVisibility() == View.VISIBLE) ||
                    (nextSave == 5 && getActivity().findViewById(R.id.saved_5).getVisibility() == View.VISIBLE) ||
                    (nextSave == 4 && getActivity().findViewById(R.id.saved_4).getVisibility() == View.VISIBLE)) {
                player.dice[i] = nextSave;
                nextSave--;
            } else {
                player.dice[i] = 0;
            }
            if (player.dice[i] == 6) {
                found6 = true;
            } else if (player.dice[i] == 5) {
                found5 = true;
            } else if (player.dice[i] == 4) {
                found4 = true;
            }
            total += player.dice[i];
        }
        if (found6 && found5 && found4) {
            total -= 15;
        } else {
            total = 0;
        }
        return total;
    }

    /**
     * Uses the associated DiceGamePlayer object to update the UI labels
     * Also asks the DiceGameApplication for the high score
     */
    public void updateLabels() {
        if (player != null) {
            ((TextView)getActivity().findViewById(R.id.player_name_lbl)).setText(String.format(Locale.US, getString(R.string.player_name_lbl_format), player.getName()));
            ((TextView)getActivity().findViewById(R.id.points_lbl)).setText(String.format(Locale.US, getString(R.string.points_lbl_format), player.getPoints()));
            ((TextView)getActivity().findViewById(R.id.tries_lbl)).setText(String.format(Locale.US, getString(R.string.tries_lbl_format), player.getTries(), maxTries));
        }
        DiceGamePlayer highScorePlayer = ((DiceGameApplication)getActivity().getApplication()).getHighScorePlayers().get(0);
        if (highScorePlayer != null) {
            ((TextView)getActivity().findViewById(R.id.high_score_lbl)).setText(String.format(Locale.US, getString(R.string.high_score_lbl_format), highScorePlayer.getPoints(), highScorePlayer.getName()));
        }
    }

    /**
     * Used to conveniently show a specific face on a specific die
     * @param index The index of the die button (0-4)
     * @param roll The number to show on the die (1-6)
     */
    public void setDieButton(int index, int roll) {
        if (index >= 0 && index < dieButtons.length) {
            if (roll >= 1 && roll <= 6) {
                ((ImageButton) getActivity().findViewById(dieButtons[index])).setImageResource(dieDrawables[roll - 1]);
            } else {
                ((ImageButton) getActivity().findViewById(dieButtons[index])).setImageDrawable(null);
            }
        }
    }

    /**
     * Used to conveniently check if a specific die button is visible
     * @param index The index of the die button (0-4)
     * @return True if the specified button is visible (View.VISIBLE), else false
     */
    public boolean isDieButtonVisible(int index) {
        if (index >= 0 && index < dieButtons.length) {
            return getActivity().findViewById(dieButtons[index]).getVisibility() == View.VISIBLE;
        } else {
            return false;
        }
    }

    /**
     * Used to get the value of a specific die button as an integer
     * Won't work if the values instance variable is desynced from the UI display
     * @param id The id of the die button (e.g. R.id.some_die_button)
     * @return The value displayed on that die button (as stored in the values instance variable)
     */
    public int getDieValueById(int id) {
        int value = 0;
        if (player.dice != null) {
            for (int k = 0; k < dieButtons.length; k++) {
                if (getActivity().findViewById(dieButtons[k]).getId() == id) {
                    value = player.dice[k];
                    break;
                }
            }
        }
        return value;
    }

}
