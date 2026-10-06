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

    private TextView eaStatus;
    private TextView balance;
    private TextView equity;
    private TextView profit;
    private TextView positions;
    private TextView scanner;

    private boolean eaRunning = false;

    private int white = Color.WHITE;
    private int gray = Color.rgb(160, 166, 178);
    private int green = Color.rgb(0, 230, 118);
    private int red = Color.rgb(255, 59, 48);
    private int gold = Color.rgb(255, 213, 79);
    private int background = Color.rgb(8, 11, 17);
    private int panel = Color.rgb(20, 24, 34);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(background);
        getWindow().setNavigationBarColor(background);

        buildDashboard();
    }

    private TextView text(String value, float size, int color) {

        TextView t = new TextView(this);

        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setPadding(0, 5, 0, 5);

        return t;
    }

    private TextView title(String value) {

        TextView t = text(value, 18, white);

        t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        return t;
    }

    private LinearLayout panel() {

        LinearLayout p = new LinearLayout(this);

        p.setOrientation(LinearLayout.VERTICAL);
        p.setPadding(20, 18, 20, 18);
        p.setBackgroundColor(panel);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        lp.setMargins(0, 10, 0, 10);

        p.setLayoutParams(lp);

        return p;
    }

    private Button button(String name) {

        Button b = new Button(this);

        b.setText(name);
        b.setTextSize(15);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        return b;
    }

    private void buildDashboard() {

        ScrollView scroll = new ScrollView(this);

        scroll.setBackgroundColor(background);

        LinearLayout main = new LinearLayout(this);

        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(20, 45, 20, 25);

        scroll.addView(main);

        // HEADER
        TextView logo = text("MONEYPRINTER", 27, white);

        logo.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        logo.setGravity(Gravity.CENTER);

        main.addView(logo);

        TextView subtitle =
                text("AUTOSCALP • MT4 / MT5", 13, gray);

        subtitle.setGravity(Gravity.CENTER);

        main.addView(subtitle);

        eaStatus = text("● EA OFFLINE", 17, red);

        eaStatus.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        eaStatus.setGravity(Gravity.CENTER);
        eaStatus.setPadding(0, 18, 0, 15);

        main.addView(eaStatus);

        // ACCOUNT
        LinearLayout accountPanel = panel();

        accountPanel.addView(title("ACCOUNT"));

        accountPanel.addView(
                text("MT5 ACCOUNT", 13, gray)
        );

        TextView accountNumber =
                text("Not Connected", 16, white);

        accountPanel.addView(accountNumber);

        main.addView(accountPanel);

        // MONEY
        LinearLayout moneyPanel = panel();

        moneyPanel.addView(title("ACCOUNT SUMMARY"));

        balance =
                text("Balance        R0.00", 16, white);

        equity =
                text("Equity         R0.00", 16, white);

        profit =
                text("Daily P/L      R0.00", 16, white);

        moneyPanel.addView(balance);
        moneyPanel.addView(equity);
        moneyPanel.addView(profit);

        main.addView(moneyPanel);

        // BOT CONTROL
        LinearLayout botPanel = panel();

        botPanel.addView(title("BOT CONTROL"));

        TextView mode =
                text("MODE: SCALP", 17, gold);

        mode.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        botPanel.addView(mode);

        botPanel.addView(
                text("Risk: 1.00%     Lot: AUTO", 15, white)
        );

        Button start = button("START BOT");

        Button stop = button("STOP BOT");

        botPanel.addView(start);
        botPanel.addView(stop);

        main.addView(botPanel);

        // SCANNER
        LinearLayout scannerPanel = panel();

        scannerPanel.addView(title("MARKET SCANNER"));

        scanner =
                text(
                        "NASDAQ       WAIT\n" +
                        "US30         WAIT\n" +
                        "GOLD         WAIT\n" +
                        "USDJPY       WAIT\n" +
                        "GBPUSD       WAIT\n" +
                        "SYNTHETIC    WAIT",
                        15,
                        white
                );

        scannerPanel.addView(scanner);

        main.addView(scannerPanel);

        // POSITIONS
        LinearLayout positionPanel = panel();

        positionPanel.addView(title("OPEN POSITIONS"));

        positions =
                text(
                        "Open Trades: 0\n" +
                        "Floating P/L: R0.00",
                        16,
                        white
                );

        positionPanel.addView(positions);

        Button closeAll = button("CLOSE ALL");

        positionPanel.addView(closeAll);

        main.addView(positionPanel);

        // NAVIGATION
        LinearLayout nav = new LinearLayout(this);

        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setGravity(Gravity.CENTER);

        Button dashboard = button("DASHBOARD");
        Button trades = button("TRADES");
        Button history = button("HISTORY");

        nav.addView(dashboard,
                new LinearLayout.LayoutParams(0, 60, 1));

        nav.addView(trades,
                new LinearLayout.LayoutParams(0, 60, 1));

        nav.addView(history,
                new LinearLayout.LayoutParams(0, 60, 1));

        main.addView(nav);

        // START
        start.setOnClickListener(v -> {

            eaRunning = true;

            eaStatus.setText("● EA ONLINE");
            eaStatus.setTextColor(green);

            scanner.setText(
                    "NASDAQ       BUY\n" +
                    "US30         WAIT\n" +
                    "GOLD         BUY\n" +
                    "USDJPY       WAIT\n" +
                    "GBPUSD       SELL\n" +
                    "SYNTHETIC    WAIT"
            );
        });

        // STOP
        stop.setOnClickListener(v -> {

            eaRunning = false;

            eaStatus.setText("● EA OFFLINE");
            eaStatus.setTextColor(red);

            scanner.setText(
                    "NASDAQ       WAIT\n" +
                    "US30         WAIT\n" +
                    "GOLD         WAIT\n" +
                    "USDJPY       WAIT\n" +
                    "GBPUSD       WAIT\n" +
                    "SYNTHETIC    WAIT"
            );
        });

        // CLOSE ALL
        closeAll.setOnClickListener(v -> {

            positions.setText(
                    "Open Trades: 0\n" +
                    "Floating P/L: R0.00\n\n" +
                    "ALL POSITIONS CLOSED"
            );
        });

        // TRADES
        trades.setOnClickListener(v -> {

            positions.setText(
                    "OPEN POSITIONS\n\n" +
                    "No live positions\n\n" +
                    "BUY: 0\n" +
                    "SELL: 0"
            );
        });

        // HISTORY
        history.setOnClickListener(v -> {

            positions.setText(
                    "TRADE HISTORY\n\n" +
                    "No completed trades"
            );
        });

        // DASHBOARD
        dashboard.setOnClickListener(v -> {

            positions.setText(
                    "Open Trades: 0\n" +
                    "Floating P/L: R0.00"
            );
        });

        setContentView(scroll);
    }
}
