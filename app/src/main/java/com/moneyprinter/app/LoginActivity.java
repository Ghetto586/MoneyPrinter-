package com.moneyprinter.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

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
    private ProgressBar progressBar;
    private TextView message;

    private final int BG = Color.rgb(7, 10, 15);
    private final int CARD = Color.rgb(17, 22, 30);
    private final int GREEN = Color.rgb(0, 230, 118);
    private final int MUTED = Color.rgb(145, 154, 168);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        getWindow().getDecorView().setSystemUiVisibility(0);

        SharedPreferences prefs =
                getSharedPreferences("MoneyPrinterSession", MODE_PRIVATE);

        String savedToken = prefs.getString("access_token", "");

        if (savedToken != null && !savedToken.isEmpty()) {
            openDashboard();
            return;
        }

        buildLoginScreen();
    }

    private GradientDrawable rounded(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextColor(color);
        view.setTextSize(size);
        if (bold) {
            view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }
        return view;
    }

    private void buildLoginScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(28, 35, 28, 28);
        root.setBackgroundColor(BG);

        TextView logo = text("MP", 42, GREEN, true);
        logo.setGravity(Gravity.CENTER);
        logo.setBackground(rounded(CARD, 28));

        LinearLayout.LayoutParams logoParams =
                new LinearLayout.LayoutParams(100, 100);
        logoParams.bottomMargin = 20;
        root.addView(logo, logoParams);

        TextView title = text("MoneyPrinter", 30, Color.WHITE, true);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView subtitle = text(
                "EA CONTROL CENTER", 13, MUTED, true);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subParams =
                new LinearLayout.LayoutParams(-1, -2);
        subParams.topMargin = 8;
        subParams.bottomMargin = 32;
        root.addView(subtitle, subParams);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(22, 24, 22, 24);
        card.setBackground(rounded(CARD, 24));

        TextView heading = text("Sign in", 23, Color.WHITE, true);
        card.addView(heading);

        TextView hint = text(
                "Sign in with your MoneyPrinter account",
                14, MUTED, false);
        LinearLayout.LayoutParams hintParams =
                new LinearLayout.LayoutParams(-1, -2);
        hintParams.topMargin = 8;
        hintParams.bottomMargin = 24;
        card.addView(hint, hintParams);

        emailInput = new EditText(this);
        emailInput.setSingleLine(true);
        emailInput.setTextColor(Color.WHITE);
        emailInput.setHintTextColor(MUTED);
        emailInput.setHint("Email address");
        emailInput.setTextSize(16);
        emailInput.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT
                | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailInput.setPadding(16, 14, 16, 14);
        emailInput.setBackground(rounded(
                Color.rgb(29, 37, 48), 14));

        card.addView(emailInput, new LinearLayout.LayoutParams(-1, 56));

        passwordInput = new EditText(this);
        passwordInput.setSingleLine(true);
        passwordInput.setTextColor(Color.WHITE);
        passwordInput.setHintTextColor(MUTED);
        passwordInput.setHint("Password");
        passwordInput.setTextSize(16);
        passwordInput.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT
                | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordInput.setPadding(16, 14, 16, 14);
        passwordInput.setBackground(rounded(
                Color.rgb(29, 37, 48), 14));

        LinearLayout.LayoutParams passParams =
                new LinearLayout.LayoutParams(-1, 56);
        passParams.topMargin = 14;
        card.addView(passwordInput, passParams);

        loginButton = new Button(this);
        loginButton.setText("SIGN IN");
        loginButton.setTextColor(Color.BLACK);
        loginButton.setTextSize(15);
        loginButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        loginButton.setAllCaps(false);
        loginButton.setBackground(rounded(GREEN, 16));

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(-1, 56);
        buttonParams.topMargin = 22;
        card.addView(loginButton, buttonParams);

        progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.GONE);
        LinearLayout.LayoutParams progressParams =
                new LinearLayout.LayoutParams(-2, -2);
        progressParams.gravity = Gravity.CENTER;
        progressParams.topMargin = 16;
        card.addView(progressBar, progressParams);

        message = text("", 13, Color.RED, false);
        message.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams messageParams =
                new LinearLayout.LayoutParams(-1, -2);
        messageParams.topMargin = 14;
        card.addView(message, messageParams);

        root.addView(card, new LinearLayout.LayoutParams(-1, -2));

        TextView footer = text(
                "Secure access • MoneyPrinter",
                12, MUTED, false);
        footer.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams footerParams =
                new LinearLayout.LayoutParams(-1, -2);
        footerParams.topMargin = 28;
        root.addView(footer, footerParams);

        setContentView(root);

        loginButton.setOnClickListener(v -> signIn());
    }

    private void signIn() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            message.setText("Enter your email and password.");
            return;
        }

        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("password", password);

            Request request = new Request.Builder()
                    .url(SUPABASE_URL
                            + "/auth/v1/token?grant_type=password")
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(body.toString(), JSON))
                    .build();

            loginButton.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            message.setTextColor(MUTED);
            message.setText("Signing in…");

            ((InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(
                            passwordInput.getWindowToken(), 0);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        loginButton.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        message.setTextColor(Color.RED);
                        message.setText(
                                "Network error. Check your internet and try again.");
                    });
                }

                @Override
                public void onResponse(Call call, Response response)
                        throws IOException {
                    String responseText = response.body() != null
                            ? response.body().string() : "";

                    if (!response.isSuccessful()) {
                        String errorMessage = "Sign-in failed.";
                        try {
                            JSONObject error =
                                    new JSONObject(responseText);
                            errorMessage = error.optString(
                                    "msg",
                                    error.optString(
                                            "message", errorMessage));
                        } catch (Exception ignored) {
                        }

                        final String displayError = errorMessage;
                        runOnUiThread(() -> {
                            loginButton.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                            message.setTextColor(Color.RED);
                            message.setText(displayError);
                        });
                        return;
                    }

                    try {
                        JSONObject result = new JSONObject(responseText);
                        String token = result.getString("access_token");
                        String refreshToken =
                                result.optString("refresh_token", "");
                        String userId = result.getJSONObject("user")
                                .getString("id");

                        getSharedPreferences(
                                "MoneyPrinterSession", MODE_PRIVATE)
                                .edit()
                                .putString("access_token", token)
                                .putString("refresh_token", refreshToken)
                                .putString("user_id", userId)
                                .putString("email", email)
                                .apply();

                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            openDashboard();
                        });

                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            loginButton.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                            message.setTextColor(Color.RED);
                            message.setText(
                                    "Could not read the sign-in response.");
                        });
                    }
                }
            });

        } catch (Exception e) {
            loginButton.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            message.setTextColor(Color.RED);
            message.setText("Could not start sign-in.");
        }
    }

    private void openDashboard() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
