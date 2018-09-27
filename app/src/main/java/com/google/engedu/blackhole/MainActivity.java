/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.blackhole;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Colors used to differentiate human player from computer player.
    private static final int[] COLORS = {Color.rgb(255, 128, 128), Color.rgb(128, 128, 255)};

    // The main board instance.
    private BlackHoleBoard board;

    static int nullIndex=-1;
    static ArrayList<Integer> neighbors=new ArrayList<>();
    private Button[] buttons=new Button[21];
    private Drawable copyButton=null;

    // Initialize the board on launch.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (int i = 0; i < BlackHoleBoard.BOARD_SIZE; i++) {
            int id = getResources().getIdentifier("button" + i, "id", getPackageName());
            buttons[i] = (Button) findViewById(id);
        }
        board = new BlackHoleBoard();
        onReset(null);
    }

    /* Shared handler for all the game buttons. When the user takes a turn we mark the button as
     * having been clicked and let the computer take a turn.
     */
    public void onClickHandler(View view) {
        Button clicked = (Button) view;
        if (clicked.isEnabled()) {
            markButtonAsClicked(clicked);
            try {
                computerTurn();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }

    // Change the button that was clicked and update the board accordingly.
    private void markButtonAsClicked(Button clicked) {
        clicked.setEnabled(false);
        clicked.setText("" + board.getCurrentPlayerValue());
        clicked.getBackground().setColorFilter(
                COLORS[board.getCurrentPlayer()], PorterDuff.Mode.MULTIPLY);
        String buttonLabel = getResources().getResourceEntryName(clicked.getId());
        board.setValue(Integer.parseInt(buttonLabel.substring(6)));
        if (board.gameOver()) {
            handleEndOfGame();
        }
    }

    // When the game is over, declare a winner.
    private void handleEndOfGame() {
        board.isRealGameOver=true;
        board.gameOver();
        int score = board.getScore();
        Log.e("mainactivity :", "null index = "+nullIndex);
        Button empty=buttons[nullIndex];
        copyButton=empty.getBackground();
        empty.setBackgroundColor(Color.BLACK);
        if(neighbors.size()>0)
            for(int x: neighbors) {
                Button b=buttons[x];
                b.setText("");
                b.getBackground().setColorFilter(null);
            }
        disableAllButtons();
        String message = null;
        if (score > 0) {
            message = "You win by " + score;
        }
        else if (score < 0) {
            message = "You lose by " + -score;
        } else
            message = "Hmmm... so we have almost the same power level";
        if (message != null) {
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.show();
            Log.i("BlackHole", message);
        }
    }

    // When the game is over disable all buttons (really just the one button that is left).
    private void disableAllButtons() {
        for (int i = 0; i < BlackHoleBoard.BOARD_SIZE; i++) {
            buttons[i].setEnabled(false);
        }
    }

    // Let the computer take a turn.
    private void computerTurn() throws CloneNotSupportedException {
        int position = board.pickMove();
        Button b = buttons[position];
        if (b == null) {
            //Log.i("Blackhole", "Couldn't find button " + position + " with id " + id);
        }
        markButtonAsClicked(b);
    }

    // Handler for the reset button. Resets both the board and the game buttons.
    public void onReset(View view) {
        board.reset();
        neighbors.clear();
        for (int i = 0; i < BlackHoleBoard.BOARD_SIZE; i++) {
            Button b = buttons[i];
            b.setEnabled(true);
            b.setText("?");
            if(i==nullIndex) {
                Log.e("null :", "true at = "+nullIndex);
                b.setBackground(copyButton);
            }
            b.getBackground().setColorFilter(null);
        }
    }
}