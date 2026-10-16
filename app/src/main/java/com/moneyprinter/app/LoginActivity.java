
package com.moneyprinter.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends Activity {

    private static final String SUPABASE_URL =
            "https://cmygqxznpvwvfziipoqe.supabase.co";

    private static final String SUPABASE_KEY =
            "sb_publishable_G_kLVUfW_pIELjDE_zeX6A_OQ2r6bCn";

    private static final MediaType JSON =
            MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();

    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs =
                getSharedPreferences("MoneyPrinterSession", MODE_PRIVATE);

        String token = prefs.getString("access_token", "");

        if (!token.isEmpty()) {
            openDashboard();
            return;
        }

        createLoginScreen();
    }

    private GradientDrawable background(int color, int radius, int stroke) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);

        if (stroke != 0) {
            drawable.setStroke(2, stroke);
        }

        return drawable;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        if (bold) {
            view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }
        return view;
    }

    private void createLoginScreen() {
        int bg = Color.rgb(7, 10, 15);
        int card = Color.rgb(17, 22, 30);
        int gold = Color.rgb(255, 193, 7);
        int muted = Color.rgb(145, 154, 168);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(24, 30, 24, 30);
        root.setBackgroundColor(bg);

        TextView logo = text("♛", 48, gold, true);
        logo.setGravity(Gravity.CENTER);
        root.addView(logo);

        TextView title = text("MoneyPrinter EA", 29, Color.WHITE, true);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView subtitle = text(
                "MT4 / MT5 CONTROL CENTER", 12, muted, true);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 8, 0, 25);
        root.addView(subtitle);

        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(20, 22, 20, 22);
        form.setBackground(background(card, 22, Color.rgb(39, 52, 69)));

        TextView heading = text("Sign in", 22, Color.WHITE, true);
        form.addView(heading);

        TextView description = text(
                "Use your existing MoneyPrinter website account.",
                13, muted, false);
        description.setPadding(0, 5, 0, 18);
        form.addView(description);

        emailInput = new EditText(this);
        emailInput.setSingleLine(true);
        emailInput.setTextSize(15);
        emailInput.setHint("Email address");
        emailInput.setHintTextColor(muted);
        emailInput.setTextColor(Color.WHITE);
        emailInput.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailInput.setPadding(14, 12, 14, 12);
        emailInput.setBackground(background(
                Color.rgb(29, 37, 48), 12, 0));
        form.addView(emailInput, new LinearLayout.LayoutParams(
                -1, 54));

        passwordInput = new EditText(this);
        passwordInput.setSingleLine(true);
        passwordInput.setTextSize(15);
        passwordInput.setHint("Password");
        passwordInput.setHintTextColor(muted);
        passwordInput.setTextColor(Color.WHITE);
        passwordInput.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordInput.setPadding(14, 12, 14, 12);
        passwordInput.setBackground(background(
                Color.rgb(29, 37, 48), 12, 0));

        LinearLayout.LayoutParams passParams =
                new LinearLayout.LayoutParams(-1, 54);
        passParams.topMargin = 12;
        form.addView(passwordInput, passParams);

        loginButton = new Button(this);
        loginButton.setText("SIGN IN");
        loginButton.setAllCaps(false);
        loginButton.setTextSize(15);
        loginButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        loginButton.setTextColor(Color.BLACK);
        loginButton.setBackground(background(gold, 14, 0));

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(-1, 54);
        buttonParams.topMargin = 18;
        form.addView(loginButton, buttonParams);

        progress = new ProgressBar(this);
        progress.setVisibility(View.GONE);

        LinearLayout.LayoutParams progressParams =
                new LinearLayout.LayoutParams(-2, -2);
        progressParams.gravity = Gravity.CENTER;
        progressParams.topMargin = 12;
        form.addView(progress, progressParams);

        TextView note = text(
                "Secure sign-in powered by Supabase",
                11, muted, false);
        note.setGravity(Gravity.CENTER);
        note.setPadding(0, 18, 0, 0);
        form.addView(note);

        root.addView(form, new LinearLayout.LayoutParams(-1, -2));

        TextView footer = text(
                "Trading commands require a connected MT5 bridge.",
                11, muted, false);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, 20, 0, 0);
        root.addView(footer);

        setContentView(root);

        loginButton.setOnClickListener(v -> signIn());
    }

    private void signIn() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,
                    "Enter your email and password.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("password", password);

            Request request = new Request.Builder()
                    .url(SUPABASE_URL +
                            "/auth/v1/token?grant_type=password")
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(body.toString(), JSON))
                    .build();

            loginButton.setEnabled(false);
            loginButton.setText("SIGNING IN...");
            progress.setVisibility(View.VISIBLE);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        loginButton.setEnabled(true);
                        loginButton.setText("SIGN IN");
                        progress.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this,
                                "Network error. Check your connection.",
                                Toast.LENGTH_LONG).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response)
                        throws IOException {

                    String responseText = response.body() != null
                            ? response.body().string() : "";

                    runOnUiThread(() -> {
                        loginButton.setEnabled(true);
                        loginButton.setText("SIGN IN");
                        progress.setVisibility(View.GONE);

                        try {
                            JSONObject result =
                                    new JSONObject(responseText);

                            if (!response.isSuccessful()) {
                                String message = result.optString(
                                        "msg",
                                        result.optString(
                                                "message",
                                                "Sign-in failed. Check your details."));
                                Toast.makeText(LoginActivity.this,
                                        message, Toast.LENGTH_LONG).show();
                                return;
                            }

                            String accessToken =
                                    result.optString("access_token", "");
                            String refreshToken =
                                    result.optString("refresh_token", "");
                            String userId = result
                                    .optJSONObject("user") != null
                                    ? result.getJSONObject("user")
                                            .optString("id", "")
                                    : "";

                            if (accessToken.isEmpty() || userId.isEmpty()) {
                                Toast.makeText(LoginActivity.this,
                                        "Login response was incomplete.",
                                        Toast.LENGTH_LONG).show();
                                return;
                            }

                            getSharedPreferences(
                                    "MoneyPrinterSession", MODE_PRIVATE)
                                    .edit()
                                    .putString("access_token", accessToken)
                                    .putString("refresh_token", refreshToken)
                                    .putString("user_id", userId)
                                    .putString("email", email)
                                    .apply();

                            openDashboard();

                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this,
                                    "Could not read the login response.",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

        } catch (Exception e) {
            Toast.makeText(this,
                    "Could not prepare sign-in request.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void openDashboard() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
