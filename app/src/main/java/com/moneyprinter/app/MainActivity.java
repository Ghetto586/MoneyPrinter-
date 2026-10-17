
package com.moneyprinter.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

public class MainActivity extends Activity {

    private static final String API =
            "https://moneyprinterv1.onrender.com";

    private static final String EA_ID =
            "moneymaker_autoscalp_01";

    private static final int BG = Color.rgb(7, 10, 15);
    private static final int CARD = Color.rgb(17, 22, 30);
    private static final int INNER = Color.rgb(29, 37, 48);
    private static final int WHITE = Color.WHITE;
    private static final int MUTED = Color.rgb(145, 154, 168);
    private static final int GREEN = Color.rgb(0, 230, 118);
    private static final int RED = Color.rgb(255, 82, 82);
    private static final int GOLD = Color.rgb(255, 193, 7);

    private static final MediaType JSON =
            MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();

    private String mode = "SCALP";
    private String symbol = "NAS100";
    private String risk = "1.0%";

    private TextView connectionStatus;
    private TextView commandStatus;
    private TextView equity;
    private TextView todayPL;
    private TextView trades;
    private TextView modeLabel;
    private TextView symbolLabel;
    private ImageView chartImage;

    private Uri chartUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(
                "MoneyPrinterSession", MODE_PRIVATE);

        if (prefs.getString("access_token", "").isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);

        buildDashboard();
    }

    private GradientDrawable shape(int color, int radius, int stroke) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);

        if (stroke != 0) {
            drawable.setStroke(2, stroke);
        }

        return drawable;
    }

    private TextView text(String value, int size, int color,
                          boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setPadding(0, 5, 0, 5);
        view.setIncludeFontPadding(true);

        if (bold) {
            view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }

        return view;
    }

    private LinearLayout row() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setBaselineAligned(false);
        return layout;
    }

    private LinearLayout card() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(15, 15, 15, 15);
        layout.setBackground(shape(
                CARD, 20, Color.rgb(39, 52, 69)));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 7, 0, 7);
        layout.setLayoutParams(params);

        return layout;
    }

    // Consistent, readable button styling across the dashboard.
    private Button button(String label, int bg, int fg) {
        Button button = new Button(this);

        button.setText(label);
        button.setVisibility(View.VISIBLE);
        button.setEnabled(true);
        button.setAlpha(1.0f);
        button.setAllCaps(false);
        button.setTextSize(14);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setTextColor(fg);
        button.setGravity(Gravity.CENTER);
        button.setMinHeight(52);
        button.setPadding(8, 8, 8, 8);
        button.setIncludeFontPadding(true);
        button.setSingleLine(false);
        button.setMaxLines(2);

        if (Build.VERSION.SDK_INT >= 21) {
            button.setBackgroundTintList(null);
            button.setStateListAnimator(null);
        }

        button.setBackground(shape(bg, 14, 0));

        return button;
    }

    private void buildDashboard() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BG);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(10, 14, 10, 28);
        root.setBackgroundColor(BG);

        scroll.addView(root, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT));

        // Header
        LinearLayout header = row();
        header.addView(text("♛", 32, GOLD, true));

        LinearLayout brand = new LinearLayout(this);
        brand.setOrientation(LinearLayout.VERTICAL);
        brand.setPadding(10, 0, 0, 0);
        brand.addView(text("MoneyPrinter EA", 21, WHITE, true));
        brand.addView(text("MT5 CONTROL CENTER", 11, MUTED, true));

        header.addView(brand,
                new LinearLayout.LayoutParams(0, -2, 1));

        root.addView(header);
        root.addView(text("AUTOMATED TRADING", 12, GOLD, true));
        root.addView(text("MT5 CONTROL", 31, WHITE, true));
        root.addView(text("CENTER", 31, GOLD, true));
        root.addView(text(
                "Manage your EA commands from anywhere.",
                14, MUTED, true));

        // MT5 connection status
        LinearLayout connection = card();
        connection.addView(text("META TRADER 5", 18, WHITE, true));

        connectionStatus = text(
                "● WAITING FOR MT5 BRIDGE", 14, GOLD, true);
        connection.addView(connectionStatus);

        connection.addView(text(
                "The Render API does not confirm that MT5 is connected.",
                12, MUTED, false));

        root.addView(connection);

        // Main START / STOP controls
        LinearLayout actions = row();

        Button start = button(
                "▶ START EA", GREEN, Color.BLACK);

        Button stop = button(
                "■ STOP EA", Color.rgb(55, 18, 25), RED);

        LinearLayout.LayoutParams startParams =
                new LinearLayout.LayoutParams(0, 58, 1);
        startParams.setMargins(0, 4, 4, 4);

        LinearLayout.LayoutParams stopParams =
                new LinearLayout.LayoutParams(0, 58, 1);
        stopParams.setMargins(4, 4, 0, 4);

        actions.addView(start, startParams);
        actions.addView(stop, stopParams);
        root.addView(actions);

        start.setOnClickListener(v -> sendCommand("START"));
        stop.setOnClickListener(v -> sendCommand("STOP"));

        // Statistics
        LinearLayout stats = row();

        equity = addStat(stats, "EQUITY", "—", GOLD);
        todayPL = addStat(stats, "TODAY P/L", "—", GREEN);
        trades = addStat(stats, "OPEN TRADES", "—", MUTED);

        root.addView(stats);

        // Robot settings
        LinearLayout settings = card();
        settings.addView(text(
                "⚙ ROBOT SETTINGS", 17, GOLD, true));

        modeLabel = text("Mode: SCALP", 15, WHITE, true);
        settings.addView(modeLabel);

        LinearLayout modes = row();

        Button scalp = button("SCALP", GREEN, Color.BLACK);
        Button swing = button("SWING", INNER, WHITE);

        modes.addView(scalp,
                new LinearLayout.LayoutParams(0, 50, 1));
        modes.addView(swing,
                new LinearLayout.LayoutParams(0, 50, 1));

        settings.addView(modes);

        scalp.setOnClickListener(v -> {
            mode = "SCALP";
            modeLabel.setText("Mode: SCALP");

            scalp.setBackground(shape(GREEN, 14, 0));
            scalp.setTextColor(Color.BLACK);

            swing.setBackground(shape(INNER, 14, 0));
            swing.setTextColor(WHITE);

            sendCommand("MODE");
        });

        swing.setOnClickListener(v -> {
            mode = "SWING";
            modeLabel.setText("Mode: SWING");

            swing.setBackground(shape(GREEN, 14, 0));
            swing.setTextColor(Color.BLACK);

            scalp.setBackground(shape(INNER, 14, 0));
            scalp.setTextColor(WHITE);

            sendCommand("MODE");
        });

        settings.addView(text(
                "Trading symbol", 15, WHITE, true));

        symbolLabel = text(
                "Selected: " + symbol, 13, GOLD, true);
        settings.addView(symbolLabel);

        String[] symbols = {
                "NAS100", "US30", "XAUUSD",
                "USDJPY", "GBPUSD", "EURUSD"
        };

        LinearLayout symbolRows = new LinearLayout(this);
        symbolRows.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < symbols.length; i += 3) {
            LinearLayout symbolRow = row();

            for (int j = i;
                 j < Math.min(i + 3, symbols.length); j++) {

                String selectedSymbol = symbols[j];

                Button symbolButton = button(
                        selectedSymbol,
                        selectedSymbol.equals(symbol) ? GREEN : INNER,
                        selectedSymbol.equals(symbol) ? Color.BLACK : WHITE);

                LinearLayout.LayoutParams symbolParams =
                        new LinearLayout.LayoutParams(0, 48, 1);
                symbolParams.setMargins(2, 3, 2, 3);

                symbolRow.addView(symbolButton, symbolParams);

                symbolButton.setOnClickListener(v -> {
                    symbol = selectedSymbol;
                    symbolLabel.setText("Selected: " + symbol);

                    for (int k = 0; k < symbolRow.getChildCount(); k++) {
                        View child = symbolRow.getChildAt(k);

                        if (child instanceof Button) {
                            Button b = (Button) child;
                            boolean selected =
                                    b.getText().toString().equals(symbol);

                            b.setBackground(shape(
                                    selected ? GREEN : INNER, 14, 0));
                            b.setTextColor(
                                    selected ? Color.BLACK : WHITE);
                        }
                    }

                    sendCommand("SYMBOL");
                });
            }

            symbolRows.addView(symbolRow);
        }

        settings.addView(symbolRows);

        // Risk selector
        LinearLayout riskRow = row();

        riskRow.addView(
                text("Risk per trade", 14, WHITE, true),
                new LinearLayout.LayoutParams(0, -2, 1));

        Button riskButton = button(risk, INNER, WHITE);

        riskRow.addView(riskButton,
                new LinearLayout.LayoutParams(115, 48));

        settings.addView(riskRow);

        riskButton.setOnClickListener(v -> {
            String[] risks = {
                    "0.25%", "0.5%", "1.0%", "1.5%", "2.0%"
            };

            new AlertDialog.Builder(this)
                    .setTitle("Risk per trade")
                    .setItems(risks, (dialog, which) -> {
                        risk = risks[which];
                        riskButton.setText(risk);
                        sendCommand("RISK");
                    })
                    .show();
        });

        root.addView(settings);

        // EA manager
        LinearLayout manager = card();

        manager.addView(text("EA MANAGER", 18, WHITE, true));
        manager.addView(text(
                "1 configured EA · " + EA_ID, 12, MUTED, false));

        Button managerStart = button(
                "START MONEYMAKER AUTOSCALP",
                GREEN, Color.BLACK);

        Button managerStop = button(
                "STOP MONEYMAKER AUTOSCALP",
                Color.rgb(55, 18, 25), RED);

        Button stopAll = button(
                "STOP ALL (CONFIGURED EA)",
                Color.rgb(55, 18, 25), RED);

        manager.addView(managerStart);
        manager.addView(managerStop);
        manager.addView(stopAll);

        managerStart.setOnClickListener(v -> sendCommand("START"));
        managerStop.setOnClickListener(v -> sendCommand("STOP"));
        stopAll.setOnClickListener(v -> sendCommand("STOP"));

        root.addView(manager);

        // Chart image upload
        LinearLayout scanner = card();

        scanner.addView(text("CHART IMAGE", 17, GOLD, true));
        scanner.addView(text(
                "Choose a chart screenshot. AI analysis is not connected yet.",
                12, MUTED, false));

        Button upload = button(
                "UPLOAD CHART IMAGE", INNER, WHITE);
        scanner.addView(upload);

        chartImage = new ImageView(this);
        chartImage.setAdjustViewBounds(true);
        chartImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

        LinearLayout.LayoutParams imageParams =
                new LinearLayout.LayoutParams(-1, 250);
        imageParams.topMargin = 10;

        scanner.addView(chartImage, imageParams);

        upload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, 1001);
        });

        root.addView(scanner);

        commandStatus = text(
                "Commands have not been sent yet.", 12, MUTED, false);
        root.addView(commandStatus);

        Button logout = button("SIGN OUT", INNER, WHITE);
        root.addView(logout);
        logout.setOnClickListener(v -> signOut());

        setContentView(scroll);
    }

    private TextView addStat(LinearLayout parent, String label,
                             String value, int color) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(8, 12, 5, 12);
        box.setBackground(shape(
                CARD, 14, Color.rgb(39, 52, 69)));

        box.addView(text(label, 10, MUTED, true));

        TextView valueView = text(value, 17, color, true);
        box.addView(valueView);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(0, -2, 1);
        params.setMargins(2, 3, 2, 3);

        parent.addView(box, params);

        return valueView;
    }

    private void sendCommand(String command) {
        SharedPreferences prefs = getSharedPreferences(
                "MoneyPrinterSession", MODE_PRIVATE);

        String token = prefs.getString("access_token", "");

        if (token.isEmpty()) {
            signOut();
            return;
        }

        try {
            JSONObject body = new JSONObject();
            body.put("ea_id", EA_ID);
            body.put("command", command);
            body.put("mode", mode);

            if ("SYMBOL".equals(command)) {
                body.put("symbol", symbol);
            }

            if ("RISK".equals(command)) {
                body.put("risk", risk);
            }

            Request request = new Request.Builder()
                    .url(API + "/api/ea/commands")
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(body.toString(), JSON))
                    .build();

            commandStatus.setText("Sending " + command + "...");
            commandStatus.setTextColor(GOLD);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        commandStatus.setText(
                                "Network error. Command not confirmed.");
                        commandStatus.setTextColor(RED);
                    });
                }

                @Override
                public void onResponse(Call call, Response response)
                        throws IOException {

                    String responseText = response.body() != null
                            ? response.body().string() : "";

                    final int statusCode = response.code();
                    final boolean successful = response.isSuccessful();

                    runOnUiThread(() -> {
                        try {
                            JSONObject result =
                                    new JSONObject(responseText);

                            if (statusCode == 401) {
                                commandStatus.setText(
                                        "Session expired. Sign in again.");
                                commandStatus.setTextColor(RED);
                                return;
                            }

                            if (!successful ||
                                    !result.optBoolean("success", false)) {
                                commandStatus.setText(
                                        result.optString("message",
                                                "Command was rejected."));
                                commandStatus.setTextColor(RED);
                                return;
                            }

                            commandStatus.setText(
                                    command
                                    + " queued. Waiting for MT5 bridge.");
                            commandStatus.setTextColor(GREEN);

                            Toast.makeText(
                                    MainActivity.this,
                                    command + " command queued",
                                    Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            commandStatus.setText(
                                    "Unexpected server response.");
                            commandStatus.setTextColor(RED);
                        }
                    });
                }
            });

        } catch (Exception e) {
            commandStatus.setText("Could not prepare command.");
            commandStatus.setTextColor(RED);
        }
    }

    private void signOut() {
        getSharedPreferences(
                "MoneyPrinterSession", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            chartUri = data.getData();

            try {
                getContentResolver().takePersistableUriPermission(
                        chartUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (Exception ignored) {
            }

            chartImage.setImageURI(chartUri);

            Toast.makeText(
                    this,
                    "Chart image selected. AI analysis is not connected.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
