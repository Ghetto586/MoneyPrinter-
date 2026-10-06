package com.moneyprinter.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DashboardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(24, 24, 24, 24);
        main.setBackgroundColor(Color.rgb(10, 12, 18));

        TextView title = new TextView(this);
        title.setText("MONEYPRINTER");
        title.setTextColor(Color.WHITE);
        title.setTextSize(26);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        main.addView(title);

        TextView status = new TextView(this);
        status.setText("● EA OFFLINE");
        status.setTextColor(Color.RED);
        status.setTextSize(18);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 25, 0, 25);
        main.addView(status);

        TextView account = new TextView(this);
        account.setText(
                "ACCOUNT\n\n" +
                "Balance: R0.00\n" +
                "Equity: R0.00\n" +
                "Daily P/L: R0.00\n" +
                "Open Trades: 0"
        );
        account.setTextColor(Color.WHITE);
        account.setTextSize(17);
        main.addView(account);

        TextView scanner = new TextView(this);
        scanner.setText(
                "\nSCANNER\n\n" +
                "NASDAQ       WAIT\n" +
                "US30         WAIT\n" +
                "GOLD         WAIT\n" +
                "USDJPY       WAIT\n" +
                "GBPUSD       WAIT\n" +
                "SYNTHETIC    WAIT"
        );
        scanner.setTextColor(Color.WHITE);
        scanner.setTextSize(16);
        main.addView(scanner);

        TextView mode = new TextView(this);
        mode.setText("\nMODE: SCALP\nRisk: 1.00%\nLot: AUTO");
        mode.setTextColor(Color.WHITE);
        mode.setTextSize(17);
        mode.setGravity(Gravity.CENTER);
        main.addView(mode);

        setContentView(main);
    }
}
