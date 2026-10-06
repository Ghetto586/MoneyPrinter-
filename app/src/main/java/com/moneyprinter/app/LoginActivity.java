package com.moneyprinter.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setGravity(Gravity.CENTER);
        main.setPadding(30, 30, 30, 30);
        main.setBackgroundColor(Color.rgb(10, 12, 18));

        TextView title = new TextView(this);
        title.setText("MONEYPRINTER");
        title.setTextColor(Color.WHITE);
        title.setTextSize(30);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        main.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("AI TRADING • AUTOSCALP");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(15);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 15, 0, 40);
        main.addView(subtitle);

        TextView login = new TextView(this);
        login.setText(
                "MT4 / MT5 ACCOUNT\n\n" +
                "LOGIN\n\n" +
                "PASSWORD\n\n" +
                "SERVER\n\n" +
                "[ CONNECT ACCOUNT ]"
        );
        login.setTextColor(Color.WHITE);
        login.setTextSize(17);
        login.setGravity(Gravity.CENTER);
        main.addView(login);

        setContentView(main);
    }
}
