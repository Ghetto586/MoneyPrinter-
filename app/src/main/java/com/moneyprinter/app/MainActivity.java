package com.moneyprinter.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Handler;

public class MainActivity extends Activity {

    private TextView status;
    private TextView scanner;
    private TextView account;

    private final Handler handler = new Handler();
    private boolean eaRunning = false;
    private int signalIndex = 0;

    private final String[] markets = {
            "NASDAQ",
            "US30",
            "GOLD",
            "USDJPY",
            "GBPUSD",
            "SYNTHETIC"
    };

    private final String[] signals = {
            "BUY",
            "SELL",
            "WAIT"
    };

    private final Runnable scannerRunnable = new Runnable() {
        @Override
        public void run() {

            if (!eaRunning) {
                return;
            }

            StringBuilder result = new StringBuilder();

            for (int i = 0; i < markets.length; i++) {

                String signal = signals[(signalIndex + i) % signals.length];

                result.append(markets[i])
                        .append("       ")
                        .append(signal)
                        .append("\n");
            }

            scanner.setText(result.toString());

            signalIndex++;

            handler.postDelayed(this, 2000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(24, 60, 24, 30);
        main.setGravity(Gravity.CENTER_HORIZONTAL);
        main.setBackgroundColor(Color.rgb(10, 12, 18));

        TextView title = new TextView(this);
        title.setText("MONEYPRINTER");
        title.setTextColor(Color.WHITE);
        title.setTextSize(28);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        main.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("AI TRADING • AUTOSCALP");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(14);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 8, 0, 20);
        main.addView(subtitle);

        status = new TextView(this);
        status.setText("● EA OFFLINE");
        status.setTextColor(Color.RED);
        status.setTextSize(18);
        status.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 15, 0, 30);
        main.addView(status);

        TextView scannerTitle = new TextView(this);
        scannerTitle.setText("SCANNER");
        scannerTitle.setTextColor(Color.WHITE);
        scannerTitle.setTextSize(20);
        scannerTitle.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        scannerTitle.setGravity(Gravity.CENTER);
        main.addView(scannerTitle);

        scanner = new TextView(this);
        scanner.setText(
                "NASDAQ       WAIT\n" +
                "US30         WAIT\n" +
                "GOLD         WAIT\n" +
                "USDJPY       WAIT\n" +
                "GBPUSD       WAIT\n" +
                "SYNTHETIC    WAIT"
        );
        scanner.setTextColor(Color.WHITE);
        scanner.setTextSize(17);
        scanner.setPadding(10, 20, 10, 20);
        main.addView(scanner);

        TextView mode = new TextView(this);
        mode.setText("MODE: SCALP");
        mode.setTextColor(Color.WHITE);
        mode.setTextSize(18);
        mode.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        mode.setGravity(Gravity.CENTER);
        mode.setPadding(0, 20, 0, 15);
        main.addView(mode);

        account = new TextView(this);
        account.setText(
                "Balance: R0.00\n" +
                "Equity: R0.00\n" +
                "Daily P/L: R0.00\n" +
                "Open Trades: 0"
        );
        account.setTextColor(Color.WHITE);
        account.setTextSize(16);
        account.setGravity(Gravity.CENTER);
        main.addView(account);

        Button startButton = new Button(this);
        startButton.setText("START EA");
        main.addView(startButton);

        Button stopButton = new Button(this);
        stopButton.setText("STOP EA");
        main.addView(stopButton);

        Button closeAllButton = new Button(this);
        closeAllButton.setText("CLOSE ALL");
        main.addView(closeAllButton);

        startButton.setOnClickListener(v -> {

            eaRunning = true;

            status.setText("● EA ONLINE");
            status.setTextColor(Color.GREEN);

            handler.removeCallbacks(scannerRunnable);
            handler.post(scannerRunnable);
        });

        stopButton.setOnClickListener(v -> {

            eaRunning = false;

            handler.removeCallbacks(scannerRunnable);

            status.setText("● EA OFFLINE");
            status.setTextColor(Color.RED);

            scanner.setText(
                    "NASDAQ       WAIT\n" +
                    "US30         WAIT\n" +
                    "GOLD         WAIT\n" +
                    "USDJPY       WAIT\n" +
                    "GBPUSD       WAIT\n" +
                    "SYNTHETIC    WAIT"
            );
        });

        closeAllButton.setOnClickListener(v -> {

            account.setText(
                    "Balance: R0.00\n" +
                    "Equity: R0.00\n" +
                    "Daily P/L: R0.00\n" +
                    "Open Trades: 0\n\n" +
                    "ALL POSITIONS CLOSED"
            );
        });

        setContentView(main);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(scannerRunnable);
        super.onDestroy();
    }
            }
