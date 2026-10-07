package com.moneyprinter.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    // =========================
    // MONEYPRINTER COLORS
    // =========================

    private final int BG = Color.rgb(7, 10, 15);
    private final int CARD = Color.rgb(17, 22, 30);
    private final int WHITE = Color.rgb(255, 255, 255);
    private final int MUTED = Color.rgb(145, 154, 168);
    private final int GREEN = Color.rgb(0, 230, 118);
    private final int RED = Color.rgb(255, 82, 82);
    private final int GOLD = Color.rgb(255, 193, 7);
    private final int BLUE = Color.rgb(80, 150, 255);

    // =========================
    // EA DATA
    // =========================

    private ArrayList<EAItem> eaList =
            new ArrayList<>();

    private LinearLayout eaContainer;

    private TextView hostStatus;
    private TextView totalEA;
    private TextView runningEA;

    // =========================
    // EA MODEL
    // =========================

    private static class EAItem {

        String name;
        String mode;
        boolean running;

        EAItem(
                String name,
                String mode,
                boolean running
        ) {
            this.name = name;
            this.mode = mode;
            this.running = running;
        }
    }


    // =========================
    // ACTIVITY
    // =========================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);

        // Example EA
        eaList.add(
                new EAItem(
                        "MoneyMaker AutoScalp",
                        "SCALP",
                        false
                )
        );

        createDashboard();
    }


    // =========================
    // TEXT
    // =========================

    private TextView text(
            String value,
            float size,
            int color
    ) {

        TextView view =
                new TextView(this);

        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);

        view.setPadding(
                0,
                4,
                0,
                4
        );

        return view;
    }


    private TextView heading(
            String value
    ) {

        TextView view =
                text(
                        value,
                        17,
                        WHITE
                );

        view.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        return view;
    }


    // =========================
    // CARD
    // =========================

    private LinearLayout card() {

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.setPadding(
                20,
                18,
                20,
                18
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(CARD);
        background.setCornerRadius(22);

        layout.setBackground(
                background
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                0,
                8,
                0,
                8
        );

        layout.setLayoutParams(params);

        return layout;
    }


    // =========================
    // BUTTON
    // =========================

    private Button button(
            String value
    ) {

        Button button =
                new Button(this);

        button.setText(value);
        button.setTextSize(13);
        button.setTextColor(WHITE);

        button.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        button.setAllCaps(false);

        button.setGravity(
                Gravity.CENTER
        );

        return button;
    }


    // =========================
    // DASHBOARD
    // =========================

    private void createDashboard() {

        ScrollView scroll =
                new ScrollView(this);

        scroll.setBackgroundColor(BG);

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setPadding(
                18,
                24,
                18,
                25
        );

        scroll.addView(root);


        // =========================
        // HEADER
        // =========================

        LinearLayout header =
                new LinearLayout(this);

        header.setOrientation(
                LinearLayout.HORIZONTAL
        );

        header.setGravity(
                Gravity.CENTER_VERTICAL
        );


        TextView money =
                text(
                        "MONEY",
                        24,
                        WHITE
                );

        money.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );


        TextView printer =
                text(
                        "PRINTER",
                        24,
                        GREEN
                );

        printer.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );


        header.addView(money);
        header.addView(printer);


        TextView settings =
                text(
                        "⚙",
                        25,
                        MUTED
                );

        settings.setGravity(
                Gravity.RIGHT
        );


        header.addView(
                settings,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );


        root.addView(header);


        root.addView(
                text(
                        "MULTI-EA HOST  •  MT5",
                        12,
                        MUTED
                )
        );


        // =========================
        // HOST STATUS
        // =========================

        hostStatus =
                text(
                        "●  MT5 HOST NOT CONNECTED",
                        15,
                        RED
                );

        hostStatus.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        hostStatus.setPadding(
                0,
                18,
                0,
                5
        );

        root.addView(hostStatus);


        root.addView(
                text(
                        "Connect your MT5 bridge to receive live EA data.",
                        12,
                        MUTED
                )
        );


        // =========================
        // EA SUMMARY
        // =========================

        LinearLayout summary =
                card();

        summary.addView(
                heading("EA MANAGER")
        );


        totalEA =
                text(
                        "TOTAL EAs: 0",
                        14,
                        WHITE
                );


        runningEA =
                text(
                        "RUNNING: 0",
                        14,
                        GREEN
                );


        summary.addView(totalEA);
        summary.addView(runningEA);


        root.addView(summary);


        // =========================
        // EA LIST
        // =========================

        LinearLayout listCard =
                card();


        listCard.addView(
                heading("YOUR EAs")
        );


        eaContainer =
                new LinearLayout(this);

        eaContainer.setOrientation(
                LinearLayout.VERTICAL
        );


        listCard.addView(
                eaContainer
        );


        root.addView(listCard);


        // =========================
        // ADD EA
        // =========================

        Button addEA =
                button(
                        "+ ADD EA"
                );


        addEA.setTextSize(15);


        root.addView(
                addEA,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        62
                )
        );


        // =========================
        // STOP ALL
        // =========================

        Button stopAll =
                button(
                        "STOP ALL EAs"
                );


        stopAll.setTextColor(
                RED
        );


        root.addView(
                stopAll,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        62
                )
        );


        // =========================
        // ADD EA CLICK
        // =========================

        addEA.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        showAddEADialog();
                    }
                }
        );


        // =========================
        // STOP ALL
        // =========================

        stopAll.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        for (EAItem ea : eaList) {

                            ea.running = false;
                        }

                        refreshEAList();

                        Toast.makeText(
                                MainActivity.this,
                                "All EAs stopped",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );


        // =========================
        // INITIAL LIST
        // =========================

        refreshEAList();


        setContentView(scroll);
    }


    // =========================
    // REFRESH EA LIST
    // =========================

    private void refreshEAList() {

        if (eaContainer == null) {
            return;
        }


        eaContainer.removeAllViews();


        int runningCount = 0;


        for (
                int i = 0;
                i < eaList.size();
                i++
        ) {

            EAItem ea =
                    eaList.get(i);


            if (ea.running) {
                runningCount++;
            }


            createEACard(
                    ea,
                    i
            );
        }


        totalEA.setText(
                "TOTAL EAs: " +
                        eaList.size()
        );


        runningEA.setText(
                "RUNNING: " +
                        runningCount
        );
    }


    // =========================
    // EA CARD
    // =========================

    private void createEACard(
            final EAItem ea,
            final int index
    ) {

        LinearLayout item =
                new LinearLayout(this);

        item.setOrientation(
                LinearLayout.VERTICAL
        );

        item.setPadding(
                16,
                16,
                16,
                16
        );


        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.rgb(25, 32, 42)
        );

        background.setCornerRadius(
                18
        );


        item.setBackground(
                background
        );


        LinearLayout.LayoutParams itemParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );


        itemParams.setMargins(
                0,
                7,
                0,
                7
        );


        item.setLayoutParams(
                itemParams
        );


        // =========================
        // NAME
        // =========================

        TextView name =
                text(
                        ea.name,
                        17,
                        WHITE
                );


        name.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );


        item.addView(name);


        // =========================
        // STATUS
        // =========================

        String statusText;


        if (ea.running) {

            statusText =
                    "● RUNNING     " +
                            ea.mode;

        } else {

            statusText =
                    "● STOPPED     " +
                            ea.mode;
        }


        TextView state =
                text(
                        statusText,
                        13,
                        ea.running
                                ? GREEN
                                : MUTED
                );


        item.addView(state);


        // =========================
        // DATA
        // =========================

        item.addView(
                text(
                        "P/L       —\n" +
                        "Trades    —\n" +
                        "Equity    —",
                        13,
                        WHITE
                )
        );


        // =========================
        // MODE ROW
        // =========================

        LinearLayout modeRow =
                new LinearLayout(this);


        modeRow.setOrientation(
                LinearLayout.HORIZONTAL
        );


        Button scalp =
                button(
                        "SCALP"
                );


        Button swing =
                button(
                        "SWING"
                );


        modeRow.addView(
                scalp,
                new LinearLayout.LayoutParams(
                        0,
                        55,
                        1
                )
        );


        modeRow.addView(
                swing,
                new LinearLayout.LayoutParams(
                        0,
                        55,
                        1
                )
        );


        item.addView(modeRow);


        // =========================
        // CONTROL ROW
        // =========================

        LinearLayout controls =
                new LinearLayout(this);


        controls.setOrientation(
                LinearLayout.HORIZONTAL
        );


        Button start =
                button(
                        "START"
                );


        Button stop =
                button(
                        "STOP"
                );


        controls.addView(
                start,
                new LinearLayout.LayoutParams(
                        0,
                        55,
                        1
                )
        );


        controls.addView(
                stop,
                new LinearLayout.LayoutParams(
                        0,
                        55,
                        1
                )
        );


        item.addView(controls);


        // =========================
        // SCALP
        // =========================

        scalp.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        ea.mode = "SCALP";

                        refreshEAList();

                        Toast.makeText(
                                MainActivity.this,
                                ea.name +
                                        " → SCALP",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );


        // =========================
        // SWING
        // =========================

        swing.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        ea.mode = "SWING";

                        refreshEAList();

                        Toast.makeText(
                                MainActivity.this,
                                ea.name +
                                        " → SWING",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );


        // =========================
        // START
        // =========================

        start.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        ea.running = true;

                        refreshEAList();

                        Toast.makeText(
                                MainActivity.this,
                                ea.name +
                                        " start requested",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );


        // =========================
        // STOP
        // =========================

        stop.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        ea.running = false;

                        refreshEAList();

                        Toast.makeText(
                                MainActivity.this,
                                ea.name +
                                        " stopped",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );


        eaContainer.addView(item);
    }


    // =========================
    // ADD EA DIALOG
    // =========================

    private void showAddEADialog() {

        final android.app.AlertDialog dialog =
                new android.app.AlertDialog.Builder(
                        this
                ).create();


        LinearLayout layout =
                new LinearLayout(this);


        layout.setOrientation(
                LinearLayout.VERTICAL
        );


        layout.setPadding(
                35,
                25,
                35,
                20
        );


        TextView title =
                heading(
                        "ADD NEW EA"
                );


        layout.addView(title);


        final EditText nameInput =
                new EditText(this);


        nameInput.setHint(
                "EA name"
        );


        nameInput.setTextColor(
                WHITE
        );


        nameInput.setHintTextColor(
                MUTED
        );


        layout.addView(
                nameInput
        );


        Button add =
                button(
                        "ADD EA"
                );


        layout.addView(
                add
        );


        add.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        String name =
                                nameInput
                                        .getText()
                                        .toString()
                                        .trim();


                        if (name.length() == 0) {

                            Toast.makeText(
                                    MainActivity.this,
                                    "Enter an EA name",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }


                        eaList.add(
                                new EAItem(
                                        name,
                                        "SCALP",
                                        false
                                )
                        );


                        refreshEAList();


                        dialog.dismiss();


                        Toast.makeText(
                                MainActivity.this,
                                name +
                                        " added",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );


        dialog.setView(layout);

        dialog.show();
    }
}
