package com.moneyprinter.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(24, 24, 24, 24);
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
        main.addView(subtitle);

        TextView status = new TextView(this);
        status.setText("● EA OFFLINE");
        status.setTextColor(Color.RED);
        status.setTextSize(18);
        status.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 35, 0, 35);
        main.addView(status);

        TextView scanner = new TextView(this);
        scanner.setText(
                "SCANNER\n\n" +
                "NASDAQ       WAIT\n" +
                "US30         WAIT\n" +
                "GOLD         WAIT\n" +
                "USDJPY       WAIT\n" +
                "GBPUSD       WAIT\n" +
                "SYNTHETIC    WAIT"
        );
        scanner.setTextColor(Color.WHITE);
        scanner.setTextSize(17);
        scanner.setPadding(20, 20, 20, 20);
        main.addView(scanner);

        TextView mode = new TextView(this);
        mode.setText("MODE: SCALP");
        mode.setTextColor(Color.WHITE);
        mode.setTextSize(18);
        mode.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        mode.setGravity(Gravity.CENTER);
        mode.setPadding(0, 25, 0, 25);
        main.addView(mode);

        TextView account = new TextView(this);
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

        TextView controls = new TextView(this);
        controls.setText(
                "\n[ START EA ]\n\n" +
                "[ STOP EA ]\n\n" +
                "[ CLOSE ALL ]"
        );
        controls.setTextColor(Color.WHITE);
        controls.setTextSize(17);
        controls.setGravity(Gravity.CENTER);
        controls.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        main.addView(controls);

        setContentView(main);
    }
}
