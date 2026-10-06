package com.moneyprinter.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {

    // MoneyPrinter modern colors
    private final int BG = Color.rgb(7, 10, 15);
    private final int CARD = Color.rgb(17, 22, 30);
    private final int CARD2 = Color.rgb(29, 37, 48);
    private final int WHITE = Color.rgb(255, 255, 255);
    private final int MUTED = Color.rgb(145, 154, 168);
    private final int GREEN = Color.rgb(0, 230, 118);
    private final int RED = Color.rgb(255, 82, 82);
    private final int GOLD = Color.rgb(255, 193, 7);

    private TextView status;
    private TextView equity;
    private TextView dailyPL;
    private TextView scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);

        createDashboard();
    }

    private TextView label(String value, float size, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setPadding(0, 4, 0, 4);
        return view;
    }

    private TextView heading(String value) {
        TextView view = label(value, 17, WHITE);
        view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return view;
    }

    private LinearLayout card() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 18, 20, 18);
        layout.setBackgroundColor(CARD);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 7, 0, 7);
        layout.setLayoutParams(params);

        return layout;
    }

    private Button actionButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(13);
        button.setTextColor(WHITE);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setAllCaps(false);
        return button;
    }

    private void createDashboard() {

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BG);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18, 28, 18, 20);

        scroll.addView(root);

        // HEADER
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView logo = label("MONEY", 23, WHITE);
        logo.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        TextView printer = label("PRINTER", 23, GREEN);
        printer.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        header.addView(logo);
        header.addView(printer);

        TextView settings = label("⚙", 24, MUTED);

        header.addView(
                settings,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        settings.setGravity(Gravity.RIGHT);

        root.addView(header);

        TextView subtitle =
                label("AI AUTOSCALP  •  MT5", 12, MUTED);

        root.addView(subtitle);

        // STATUS
        status = label("●  EA OFFLINE", 15, RED);
        status.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        status.setPadding(0, 16, 0, 12);

        root.addView(status);

        // EQUITY CARD
        LinearLayout equityCard = card();

        equityCard.addView(
                label("EQUITY", 12, MUTED)
        );

        equity = label("R10,000.00", 30, WHITE);
        equity.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        equityCard.addView(equity);

        dailyPL =
                label("+R0.00     +0.00%", 14, GREEN);

        equityCard.addView(dailyPL);

        TextView chart =
                label(
                        "\n   ╱╲      ╱╲\n" +
                        " ╱    ╲  ╱    ╲___\n" +
                        "╱       ╲╱         ╲\n",
                        17,
                        GREEN
                );

        equityCard.addView(chart);

        root.addView(equityCard);

        // ACCOUNT
        LinearLayout account = card();

        account.addView(
                heading("ACCOUNT")
        );

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        TextView balance =
                label(
                        "BALANCE\nR10,000.00",
                        14,
                        WHITE
                );

        TextView margin =
                label(
                        "FREE MARGIN\nR9,850.00",
                        14,
                        WHITE
                );

        row.addView(
                balance,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        row.addView(
                margin,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        account.addView(row);

        root.addView(account);

        // AUTOSCALP
        LinearLayout bot = card();

        bot.addView(
                heading("AUTOSCALP")
        );

        TextView mode =
                label(
                        "● READY        SCALP",
                        15,
                        GREEN
                );

        mode.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        bot.addView(mode);

        bot.addView(
                label(
                        "Risk  1.00%        Lot  AUTO        Max  8",
                        13,
                        MUTED
                )
        );

        LinearLayout controls = new LinearLayout(this);

        controls.setOrientation(
                LinearLayout.HORIZONTAL
        );

        Button start =
                actionButton("START EA");

        Button stop =
                actionButton("STOP EA");

        controls.addView(
                start,
                new LinearLayout.LayoutParams(
                        0,
                        60,
                        1
                )
        );

        controls.addView(
                stop,
                new LinearLayout.LayoutParams(
                        0,
                        60,
                        1
                )
        );

        bot.addView(controls);

        root.addView(bot);

        // MARKET SCANNER
        LinearLayout scanCard = card();

        scanCard.addView(
                heading("MARKET SCANNER")
        );

        scanner =
                label(
                        "NASDAQ        WAIT     --\n" +
                        "US30          WAIT     --\n" +
                        "GOLD          WAIT     --\n" +
                        "USDJPY        WAIT     --\n" +
                        "GBPUSD        WAIT     --\n" +
                        "SYNTHETIC     WAIT     --",
                        14,
                        WHITE
                );

        scanner.setTypeface(
                Typeface.MONOSPACE
        );

        scanCard.addView(scanner);

        root.addView(scanCard);

        // POSITIONS
        LinearLayout positions = card();

        positions.addView(
                heading("POSITIONS")
        );

        positions.addView(
                label(
                        "Open Trades       0\n" +
                        "Floating P/L      R0.00",
                        14,
                        WHITE
                )
        );

        Button close =
                actionButton("CLOSE ALL");

        positions.addView(close);

        root.addView(positions);

        // BOTTOM NAVIGATION
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
                    actionButton(tab);

            navigation.addView(
                    nav,
                    new LinearLayout.LayoutParams(
                            0,
                            60,
                            1
                    )
            );
        }

        root.addView(navigation);

        // START EA
        start.setOnClickListener(v -> {

            status.setText("●  EA ONLINE");
            status.setTextColor(GREEN);

            scanner.setText(
                    "NASDAQ        BUY      87%\n" +
                    "US30          WAIT     54%\n" +
                    "GOLD          BUY      91%\n" +
                    "USDJPY        SELL     78%\n" +
                    "GBPUSD        WAIT     48%\n" +
                    "SYNTHETIC     WAIT     52%"
            );
        });

        // STOP EA
        stop.setOnClickListener(v -> {

            status.setText("●  EA OFFLINE");
            status.setTextColor(RED);

            scanner.setText(
                    "NASDAQ        WAIT     --\n" +
                    "US30          WAIT     --\n" +
                    "GOLD          WAIT     --\n" +
                    "USDJPY        WAIT     --\n" +
                    "GBPUSD        WAIT     --\n" +
                    "SYNTHETIC     WAIT     --"
            );
        });

        // CLOSE ALL
        close.setOnClickListener(v -> {

            dailyPL.setText(
                    "+R0.00     +0.00%"
            );
        });

        setContentView(scroll);
    }
    }
