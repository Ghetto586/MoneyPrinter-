package com.moneyprinter.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {

    // =========================
    // MONEYPRINTER COLORS
    // =========================

    private final int BG = Color.rgb(7, 10, 15);
    private final int CARD = Color.rgb(17, 22, 30);
    private final int CARD2 = Color.rgb(25, 32, 42);
    private final int WHITE = Color.rgb(255, 255, 255);
    private final int MUTED = Color.rgb(145, 154, 168);
    private final int GREEN = Color.rgb(0, 230, 118);
    private final int RED = Color.rgb(255, 82, 82);
    private final int GOLD = Color.rgb(255, 193, 7);
    private final int BLUE = Color.rgb(80, 150, 255);

    private TextView status;
    private TextView connectionText;

    private TextView balance;
    private TextView equity;
    private TextView margin;
    private TextView floatingPL;

    private TextView dailyPL;
    private TextView scanner;
    private TextView modeText;
    private TextView eaState;

    private Button startButton;
    private Button stopButton;
    private Button scalpButton;
    private Button swingButton;

    private boolean eaRunning = false;
    private String tradingMode = "SCALP";


    // =========================
    // ACTIVITY
    // =========================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);

        createDashboard();
    }


    // =========================
    // TEXT HELPERS
    // =========================

    private TextView text(String value, float size, int color) {

        TextView view = new TextView(this);

        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);

        view.setPadding(0, 4, 0, 4);

        return view;
    }


    private TextView heading(String value) {

        TextView view = text(value, 17, WHITE);

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

        LinearLayout layout = new LinearLayout(this);

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

        layout.setBackground(background);

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

    private Button button(String value) {

        Button button = new Button(this);

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


        TextView subtitle =
                text(
                        "AI TRADING HOST  •  MT5",
                        12,
                        MUTED
                );

        root.addView(subtitle);


        // =========================
        // CONNECTION STATUS
        // =========================

        status =
                text(
                        "●  NOT CONNECTED",
                        15,
                        RED
                );

        status.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        status.setPadding(
                0,
                18,
                0,
                5
        );

        root.addView(status);


        connectionText =
                text(
                        "Connect your MoneyPrinter EA to display live account data.",
                        12,
                        MUTED
                );

        root.addView(connectionText);


        // =========================
        // ACCOUNT OVERVIEW
        // =========================

        LinearLayout account =
                card();

        account.addView(
                heading("ACCOUNT OVERVIEW")
        );


        equity =
                text(
                        "—",
                        31,
                        WHITE
                );

        equity.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );


        account.addView(
                text(
                        "EQUITY",
                        11,
                        MUTED
                )
        );

        account.addView(equity);


        dailyPL =
                text(
                        "P/L  —",
                        14,
                        MUTED
                );

        account.addView(dailyPL);


        LinearLayout accountRow =
                new LinearLayout(this);

        accountRow.setOrientation(
                LinearLayout.HORIZONTAL
        );


        balance =
                text(
                        "BALANCE\n—",
                        14,
                        WHITE
                );


        margin =
                text(
                        "FREE MARGIN\n—",
                        14,
                        WHITE
                );


        accountRow.addView(
                balance,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );


        accountRow.addView(
                margin,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );


        account.addView(accountRow);


        floatingPL =
                text(
                        "FLOATING P/L\n—",
                        14,
                        WHITE
                );

        account.addView(floatingPL);


        root.addView(account);


        // =========================
        // EA CONTROL
        // =========================

        LinearLayout ea =
                card();


        ea.addView(
                heading("MONEYPRINTER EA")
        );


        eaState =
                text(
                        "● EA STOPPED",
                        15,
                        RED
                );

        eaState.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        ea.addView(eaState);


        modeText =
                text(
                        "Trading Mode: SCALP",
                        14,
                        WHITE
                );

        modeText.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        ea.addView(modeText);


        ea.addView(
                text(
                        "Risk     1.00%\n" +
                        "Lot      AUTO\n" +
                        "Max      8 trades",
                        13,
                        MUTED
                )
        );


        // =========================
        // MODE BUTTONS
        // =========================

        LinearLayout modes =
                new LinearLayout(this);

        modes.setOrientation(
                LinearLayout.HORIZONTAL
        );


        scalpButton =
                button("SCALP MODE");


        swingButton =
                button("SWING MODE");


        modes.addView(
                scalpButton,
                new LinearLayout.LayoutParams(
                        0,
                        58,
                        1
                )
        );


        modes.addView(
                swingButton,
                new LinearLayout.LayoutParams(
                        0,
                        58,
                        1
                )
        );


        ea.addView(modes);


        // =========================
        // START / STOP
        // =========================

        LinearLayout controls =
                new LinearLayout(this);

        controls.setOrientation(
                LinearLayout.HORIZONTAL
        );


        startButton =
                button("START EA");


        stopButton =
                button("STOP EA");


        controls.addView(
                startButton,
                new LinearLayout.LayoutParams(
                        0,
                        62,
                        1
                )
        );


        controls.addView(
                stopButton,
                new LinearLayout.LayoutParams(
                        0,
                        62,
                        1
                )
        );


        ea.addView(controls);


        root.addView(ea);


        // =========================
        // MARKET SCANNER
        // =========================

        LinearLayout scannerCard =
                card();


        scannerCard.addView(
                heading("AI MARKET SCANNER")
        );


        scanner =
                text(
                        "NASDAQ        —       —\n" +
                        "US30          —       —\n" +
                        "GOLD          —       —\n" +
                        "USDJPY        —       —\n" +
                        "GBPUSD        —       —\n" +
                        "SYNTHETIC     —       —",
                        14,
                        WHITE
                );


        scanner.setTypeface(
                Typeface.MONOSPACE
        );


        scannerCard.addView(scanner);


        scannerCard.addView(
                text(
                        "Scanner data will appear when the EA connection is active.",
                        11,
                        MUTED
                )
        );


        root.addView(scannerCard);


        // =========================
        // POSITIONS
        // =========================

        LinearLayout positions =
                card();


        positions.addView(
                heading("POSITIONS")
        );


        positions.addView(
                text(
                        "OPEN TRADES        0\n" +
                        "FLOATING P/L       —",
                        14,
                        WHITE
                )
        );


        Button closeAll =
                button("CLOSE ALL TRADES");


        positions.addView(closeAll);


        root.addView(positions);


        // =========================
        // NAVIGATION
        // =========================

        LinearLayout navigation =
                new LinearLayout(this);

        navigation.setOrientation(
                LinearLayout.HORIZONTAL
        );

        navigation.setGravity(
                Gravity.CENTER
        );


        String[] tabs = {
                "HOME",
                "MARKETS",
                "TRADE",
                "HISTORY"
        };


        for (String tab : tabs) {

            Button nav =
                    button(tab);

            navigation.addView(
                    nav,
                    new LinearLayout.LayoutParams(
                            0,
                            58,
                            1
                    )
            );
        }


        root.addView(navigation);


        // =========================
        // SCALP MODE
        // =========================

        scalpButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        tradingMode = "SCALP";

                        modeText.setText(
                                "Trading Mode: SCALP"
                        );

                        modeText.setTextColor(
                                GREEN
                        );

                        scalpButton.setText(
                                "✓ SCALP MODE"
                        );

                        swingButton.setText(
                                "SWING MODE"
                        );
                    }
                }
        );


        // =========================
        // SWING MODE
        // =========================

        swingButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        tradingMode = "SWING";

                        modeText.setText(
                                "Trading Mode: SWING"
                        );

                        modeText.setTextColor(
                                GOLD
                        );

                        scalpButton.setText(
                                "SCALP MODE"
                        );

                        swingButton.setText(
                                "✓ SWING MODE"
                        );
                    }
                }
        );


        // =========================
        // START EA
        // =========================

        startButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        eaRunning = true;

                        status.setText(
                                "●  EA READY"
                        );

                        status.setTextColor(
                                GREEN
                        );


                        eaState.setText(
                                "● EA START REQUESTED"
                        );

                        eaState.setTextColor(
                                GREEN
                        );


                        connectionText.setText(
                                "Waiting for live MT5/EA connection..."
                        );


                        scanner.setText(
                                "NASDAQ        CONNECTING   --\n" +
                                "US30          CONNECTING   --\n" +
                                "GOLD          CONNECTING   --\n" +
                                "USDJPY        CONNECTING   --\n" +
                                "GBPUSD        CONNECTING   --\n" +
                                "SYNTHETIC     CONNECTING   --"
                        );
                    }
                }
        );


        // =========================
        // STOP EA
        // =========================

        stopButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        eaRunning = false;

                        status.setText(
                                "●  EA STOPPED"
                        );

                        status.setTextColor(
                                RED
                        );


                        eaState.setText(
                                "● EA STOPPED"
                        );

                        eaState.setTextColor(
                                RED
                        );


                        connectionText.setText(
                                "EA is not running."
                        );


                        scanner.setText(
                                "NASDAQ        —       —\n" +
                                "US30          —       —\n" +
                                "GOLD          —       —\n" +
                                "USDJPY        —       —\n" +
                                "GBPUSD        —       —\n" +
                                "SYNTHETIC     —       —"
                        );
                    }
                }
        );


        // =========================
        // CLOSE ALL
        // =========================

        closeAll.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (!eaRunning) {

                            connectionText.setText(
                                    "No EA connection. Nothing was closed."
                            );

                        } else {

                            connectionText.setText(
                                    "Close-all request ready for EA."
                            );
                        }
                    }
                }
        );


        setContentView(scroll);
    }
    }
