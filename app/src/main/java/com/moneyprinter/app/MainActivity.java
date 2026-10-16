
package com.moneyprinter.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        buildDashboard();
    }

    private GradientDrawable shape(int color, int radius, int stroke) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        if (stroke != 0) d.setStroke(2, stroke);
        return d;
    }

    private TextView text(String value, int size, int color,
                          boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setPadding(0, 5, 0, 5);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private LinearLayout row() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        return l;
    }

    private LinearLayout card() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(15, 15, 15, 15);
        l.setBackground(shape(CARD, 20, Color.rgb(39, 52, 69)));

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 7, 0, 7);
        l.setLayoutParams(p);
        return l;
    }

    private Button button(String label, int bg, int fg) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextSize(13);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setTextColor(fg);
        b.setGravity(Gravity.CENTER);
        b.setMinHeight(48);
        b.setBackgroundTintList(null);
        b.setBackground(shape(bg, 14, 0));
        return b;
    }

    private void buildDashboard() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BG);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 20, 16, 28);
        scroll.addView(root);

        LinearLayout header = row();
        header.addView(text("♛", 32, GOLD, true));

        LinearLayout brand = new LinearLayout(this);
        brand.setOrientation(LinearLayout.VERTICAL);
        brand.setPadding(10, 0, 0, 0);
        brand.addView(text("MoneyPrinter EA", 21, WHITE, true));
        brand.addView(text("MT5 CONTROL CENTER", 10, MUTED, true));
        header.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));
        root.addView(header);

        root.addView(text("AUTOMATED TRADING", 11, GOLD, true));
        root.addView(text("MT5 CONTROL", 31, WHITE, true));
        root.addView(text("CENTER", 31, GOLD, true));
        root.addView(text(
                "Manage your EA commands from anywhere.",
                14, MUTED, false));

        LinearLayout connection = card();
        connection.addView(text("META TRADER 5", 17, WHITE, true));

        connectionStatus = text(
                "● WAITING FOR MT5 BRIDGE", 13, GOLD, true);
        connection.addView(connectionStatus);

        connection.addView(text(
                "Render API connected does not mean MT5 is connected.",
                11, MUTED, false));
        root.addView(connection);

        LinearLayout actions = row();
        Button start = button("▶ START EA", GREEN, Color.BLACK);
        Button stop = button("■ STOP EA",
                Color.rgb(45, 15, 22), RED);

        LinearLayout.LayoutParams half =
                new LinearLayout.LayoutParams(0, 58, 1);
        half.setMargins(0, 5, 5, 5);
        actions.addView(start, half);

        LinearLayout.LayoutParams half2 =
                new LinearLayout.LayoutParams(0, 58, 1);
        half2.setMargins(5, 5, 0, 5);
        actions.addView(stop, half2);
        root.addView(actions);

        start.setOnClickListener(v -> sendCommand("START"));
        stop.setOnClickListener(v -> sendCommand("STOP"));

        LinearLayout stats = row();
        equity = addStat(stats, "EQUITY", "—", GOLD);
        todayPL = addStat(stats, "TODAY P/L", "—", GREEN);
        trades = addStat(stats, "OPEN TRADES", "—", MUTED);
        root.addView(stats);

        LinearLayout settings = card();
        settings.addView(text("⚙ ROBOT SETTINGS", 16, GOLD, true));

        modeLabel = text("Mode: SCALP", 13, WHITE, true);
        settings.addView(modeLabel);

        LinearLayout modes = row();
        Button scalp = button("ϟ SCALP", GREEN, Color.BLACK);
        Button swing = button("▥ SWING", INNER, WHITE);

        modes.addView(scalp, new LinearLayout.LayoutParams(0, 48, 1));
        modes.addView(swing, new LinearLayout.LayoutParams(0, 48, 1));
        settings.addView(modes);

        scalp.setOnClickListener(v -> {
            mode = "SCALP";
            modeLabel.setText("Mode: " + mode);
            scalp.setBackground(shape(GREEN, 14, 0));
            scalp.setTextColor(Color.BLACK);
            swing.setBackground(shape(INNER, 14, 0));
            swing.setTextColor(WHITE);
            sendCommand("MODE");
        });

        swing.setOnClickListener(v -> {
            mode = "SWING";
            modeLabel.setText("Mode: " + mode);
            swing.setBackground(shape(GREEN, 14, 0));
            swing.setTextColor(Color.BLACK);
            scalp.setBackground(shape(INNER, 14, 0));
            scalp.setTextColor(WHITE);
            sendCommand("MODE");
        });

        settings.addView(text("Trading symbol", 13, WHITE, true));
        symbolLabel = text("Selected: " + symbol, 12, GOLD, true);
        settings.addView(symbolLabel);

        String[] symbols = {
                "NAS100", "US30", "XAUUSD",
                "USDJPY", "GBPUSD", "EURUSD"
        };

        LinearLayout symbolRows = new LinearLayout(this);
        symbolRows.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < symbols.length; i += 3) {
            LinearLayout symbolRow = row();

            for (int j = i; j < Math.min(i + 3, symbols.length); j++) {
                String s = symbols[j];
                Button sb = button(s, INNER, WHITE);

                LinearLayout.LayoutParams sp =
                        new LinearLayout.LayoutParams(0, 44, 1);
                sp.setMargins(2, 3, 2, 3);
                symbolRow.addView(sb, sp);

                sb.setOnClickListener(v -> {
                    symbol = s;
                    symbolLabel.setText("Selected: " + symbol);
                    sendCommand("SYMBOL");
                });
            }
            symbolRows.addView(symbolRow);
        }
        settings.addView(symbolRows);

        LinearLayout riskRow = row();
        riskRow.addView(text("Risk per trade", 13, WHITE, true),
                new LinearLayout.LayoutParams(0, -2, 1));

        Button riskButton = button(risk, INNER, WHITE);
        riskRow.addView(riskButton,
                new LinearLayout.LayoutParams(110, 46));
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

        LinearLayout manager = card();
        manager.addView(text("EA MANAGER", 16, WHITE, true));
        manager.addView(text(
                "1 configured EA · " + EA_ID,
                11, MUTED, false));

        Button managerStart = button(
                "START MONEYMAKER AUTOSCALP", GREEN, Color.BLACK);
        Button managerStop = button(
                "STOP MONEYMAKER AUTOSCALP",
                Color.rgb(45, 15, 22), RED);

        manager.addView(managerStart);
        manager.addView(managerStop);

        managerStart.setOnClickListener(v -> sendCommand("START"));
        managerStop.setOnClickListener(v -> sendCommand("STOP"));

        Button stopAll = button(
                "STOP ALL (CONFIGURED EA)",
                Color.rgb(45, 15, 22), RED);
        manager.addView(stopAll);
        stopAll.setOnClickListener(v -> sendCommand("STOP"));
        root.addView(manager);

        LinearLayout scanner = card();
        scanner.addView(text("CHART IMAGE", 16, GOLD, true));
        scanner.addView(text(
                "Choose a chart screenshot. AI analysis is not connected yet.",
                12, MUTED, false));

        Button upload = button("UPLOAD CHART IMAGE", INNER, WHITE);
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
        box.setPadding(9, 12, 5, 12);
        box.setBackground(shape(CARD, 14, Color.rgb(39, 52, 69)));

        box.addView(text(label, 9, MUTED, true));
        TextView val = text(value, 15, color, true);
        box.addView(val);

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(0, -2, 1);
        p.setMargins(2, 3, 2, 3);
        parent.addView(box, p);
        return val;
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

                    runOnUiThread(() -> {
                        try {
                            JSONObject result =
                                    new JSONObject(responseText);

                            if (response.code() == 401) {
                                commandStatus.setText(
                                        "Session expired. Sign in again.");
                                commandStatus.setTextColor(RED);
                                return;
                            }

                            if (!response.isSuccessful()
                                    || !result.optBoolean("success", false)) {
                                commandStatus.setText(
                                        result.optString("message",
                                                "Command was rejected."));
                                commandStatus.setTextColor(RED);
                                return;
                            }

                            commandStatus.setText(
                                    command + " queued. Waiting for MT5 bridge.");
                            commandStatus.setTextColor(GREEN);

                            Toast.makeText(MainActivity.this,
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
                .edit().clear().apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            chartUri = data.getData();
            chartImage.setImageURI(chartUri);
            Toast.makeText(this,
                    "Chart image selected. AI analysis is not connected.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
