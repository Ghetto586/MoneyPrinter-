
package com.moneyprinter.app;

import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView screen = new TextView(this);
        screen.setText("MoneyPrinter\n\nLogin screen is opening.");
        screen.setTextSize(24);
        screen.setTextColor(Color.WHITE);
        screen.setBackgroundColor(Color.rgb(7, 10, 15));
        screen.setGravity(Gravity.CENTER);
        screen.setPadding(24, 24, 24, 24);

        setContentView(screen);
    }
}
