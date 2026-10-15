
package com.moneyprinter.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private static final String EA_ID = "moneymaker_autoscalp_01";
    private static final String DATABASE_URL =
            "https://moneyprinter-638b5-default-rtdb.firebaseio.com";

    private static final int BG = Color.rgb(7, 10, 15);
    private static final int CARD = Color.rgb(17, 22, 30);
    private static final int INNER = Color.rgb(29, 37, 48);
    private static final int WHITE = Color.WHITE;
    private static final int MUTED = Color.rgb(145, 154, 168);
    private static final int GREEN = Color.rgb(0, 230, 118);
    private static final int RED = Color.rgb(255, 82, 82);
    private static final int GOLD = Color.rgb(255, 193, 7);

    private String selectedMode = "SCALP";
    private String selectedSymbol = "NAS100";
    private String selectedRisk = "1.0%";

    private TextView hostStatus;
    private TextView totalEA;
    private TextView runningEA;
    private TextView dashboardEquity;
    private TextView dashboardToday;
    private TextView dashboardTrades;
    private TextView scannerResult;

    private LinearLayout eaContainer;

    private DatabaseReference rootDatabase;
    private DatabaseReference statusDatabase;
    private DatabaseReference commandDatabase;
    private ValueEventListener statusListener;

    private Uri selectedChartUri;
    private ImageView chartPreview;

    private final Map<String, Map<String, Object>> eaItems =
            new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            rootDatabase = FirebaseDatabase.getInstance(DATABASE_URL)
                    .getReference();

            statusDatabase = rootDatabase.child("eas")
                    .child(EA_ID).child("status");

            commandDatabase = rootDatabase.child("eas")
                    .child(EA_ID).child("command");

        } catch (Exception e) {
            Toast.makeText(this,
                    "Firebase initialization failed",
                    Toast.LENGTH_LONG).show();
        }

        createDashboard();
        connectFirebase();
        refreshEAList();
    }

    private GradientDrawable modernBackground(
            int color, int radius, int stroke) {

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);

        if (stroke != 0) {
            drawable.setStroke(2, stroke);
        }

        return drawable;
    }

    private TextView modernText(
            String value, int size, int color, boolean bold) {

        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setPadding(0, 5, 0, 5);

        if (bold) {
            view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }

        return view;
    }

    private LinearLayout modernCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(15, 15, 15, 15);
        card.setBackground(modernBackground(
                CARD, 20, Color.rgb(39, 52, 69)));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(0, 7, 0, 7);
        card.setLayoutParams(params);

        return card;
    }

    private Button modernButton(
            String label, int background, int foreground) {

        Button button = new Button(this);
        button.setText(label);
        button.setTextColor(foreground);
        button.setTextSize(12);
        button.setAllCaps(false);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setBackground(modernBackground(
                background, 15, 0));

        return button;
    }

    private LinearLayout modernRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        return row;
    }

    private void createDashboard() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 18, 16, 25);

        scroll.addView(root);

        // HEADER
        LinearLayout header = modernRow();
        header.addView(modernText("♛", 31, GOLD, true));

        LinearLayout brand = new LinearLayout(this);
        brand.setOrientation(LinearLayout.VERTICAL);
        brand.setPadding(10, 0, 0, 0);

        LinearLayout brandLine = modernRow();
        brandLine.addView(modernText(
                "MoneyPrinter ", 20, WHITE, true));
        brandLine.addView(modernText("EA", 20, GOLD, true));

        brand.addView(brandLine);
        brand.addView(modernText(
                "MT5 CONTROL CENTER", 10, MUTED, true));

        header.addView(brand,
                new LinearLayout.LayoutParams(0, -2, 1));

        TextView online = modernText(
                "● CONNECTING", 10, GOLD, true);
        online.setPadding(10, 8, 10, 8);
        online.setBackground(modernBackground(
                Color.rgb(35, 30, 10), 30, GOLD));

        header.addView(online);
        root.addView(header);

        // HERO
        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(5, 27, 5, 20);

        hero.addView(modernText(
                "AUTOMATED TRADING  ━━━━━", 11, GOLD, true));
        hero.addView(modernText(
                "MT5 CONTROL", 31, WHITE, true));
        hero.addView(modernText(
                "CENTER", 31, GOLD, true));
        hero.addView(modernText(
                "Manage your trading robot from anywhere.",
                15, MUTED, false));

        root.addView(hero);

        // ACCOUNT CONNECTION
        LinearLayout account = modernCard();
        account.setOrientation(LinearLayout.HORIZONTAL);
        account.setGravity(Gravity.CENTER_VERTICAL);

        TextView mtIcon = modernText("⑤", 30, GREEN, true);
        mtIcon.setGravity(Gravity.CENTER);
        mtIcon.setBackground(modernBackground(INNER, 15, 0));

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(60, 60);
        iconParams.setMargins(0, 0, 13, 0);

        account.addView(mtIcon, iconParams);

        LinearLayout accountInfo = new LinearLayout(this);
        accountInfo.setOrientation(LinearLayout.VERTICAL);
        accountInfo.addView(modernText(
                "MetaTrader 5", 17, WHITE, true));

        hostStatus = modernText(
                "● WAITING FOR MT5", 12, GOLD, true);
        accountInfo.addView(hostStatus);

        accountInfo.addView(modernText(
                "Status supplied by your MT5 bridge",
                10, MUTED, false));

        account.addView(accountInfo);
        root.addView(account);

        // START / STOP
        LinearLayout actions = modernRow();

        Button start = modernButton(
                "▶ START EA", GREEN, Color.BLACK);
        Button stop = modernButton(
                "■ STOP EA", Color.rgb(35, 15, 22), RED);

        stop.setBackground(modernBackground(
                Color.rgb(25, 12, 19), 15, RED));

        LinearLayout.LayoutParams startParams =
                new LinearLayout.LayoutParams(0, 60, 1);
        startParams.setMargins(0, 6, 5, 8);

        LinearLayout.LayoutParams stopParams =
                new LinearLayout.LayoutParams(0, 60, 1);
        stopParams.setMargins(5, 6, 0, 8);

        actions.addView(start, startParams);
        actions.addView(stop, stopParams);
        root.addView(actions);

        start.setOnClickListener(v -> sendCommand("START"));
        stop.setOnClickListener(v -> sendCommand("STOP"));

        // ACCOUNT STATISTICS
        LinearLayout stats = modernRow();

        dashboardEquity = addModernStat(
                stats, "◈", "EQUITY", "—", GOLD, false);

        dashboardToday = addModernStat(
                stats, "↗", "TODAY P/L", "—", GREEN, true);

        dashboardTrades = addModernStat(
                stats, "☰", "OPEN TRADES", "—", MUTED, true);

        root.addView(stats);

        // ROBOT SETTINGS
        LinearLayout settings = modernCard();
        settings.addView(modernText(
                "⚙ ROBOT SETTINGS", 16, GOLD, true));

        LinearLayout modes = modernRow();
        modes.setPadding(4, 4, 4, 4);
        modes.setBackground(modernBackground(INNER, 15, 0));

        Button scalp = modernButton(
                "ϟ SCALP", GREEN, Color.BLACK);
        Button swing = modernButton(
                "▥ SWING", INNER, MUTED);

        modes.addView(scalp,
                new LinearLayout.LayoutParams(0, 49, 1));
        modes.addView(swing,
                new LinearLayout.LayoutParams(0, 49, 1));

        settings.addView(modes);

        scalp.setOnClickListener(v -> {
            selectedMode = "SCALP";
            sendMode(selectedMode);
            updateModeButtons(scalp, swing);
        });

        swing.setOnClickListener(v -> {
            selectedMode = "SWING";
            sendMode(selectedMode);
            updateModeButtons(scalp, swing);
        });

        settings.addView(modernText(
                "⌖ SYMBOLS", 14, WHITE, true));

        LinearLayout symbols = modernRow();
        String[] symbolNames = {
                "NAS100", "US30", "XAUUSD", "USDJPY"
        };

        for (String symbol : symbolNames) {
            Button symbolButton = modernButton(
                    symbol,
                    symbol.equals(selectedSymbol)
                            ? Color.rgb(8, 47, 36) : INNER,
                    symbol.equals(selectedSymbol) ? GREEN : MUTED);

            symbolButton.setTextSize(9);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(0, 44, 1);
            params.setMargins(2, 3, 2, 3);

            symbols.addView(symbolButton, params);

            symbolButton.setOnClickListener(v -> {
                selectedSymbol = symbol;
                sendSetting("SYMBOL", symbol);
                Toast.makeText(this,
                        "Symbol request sent: " + symbol,
                        Toast.LENGTH_SHORT).show();

                // Refresh button colours.
                for (int i = 0; i < symbols.getChildCount(); i++) {
                    View child = symbols.getChildAt(i);
                    if (child instanceof Button) {
                        Button b = (Button) child;
                        boolean chosen =
                                b.getText().toString().equals(symbol);

                        b.setBackground(modernBackground(
                                chosen ? Color.rgb(8, 47, 36) : INNER,
                                15, 0));
                        b.setTextColor(chosen ? GREEN : MUTED);
                    }
                }
            });
        }

        settings.addView(symbols);

        LinearLayout riskRow = modernRow();

        riskRow.addView(modernText(
                "Risk per trade", 14, WHITE, true),
                new LinearLayout.LayoutParams(0, -2, 1));

        Button risk = modernButton(
                selectedRisk + "  ▾", INNER, WHITE);

        riskRow.addView(risk,
                new LinearLayout.LayoutParams(115, 47));

        settings.addView(riskRow);

        risk.setOnClickListener(v -> {
            String[] risks = {
                    "0.25%", "0.5%", "1.0%", "1.5%", "2.0%"
            };

            new AlertDialog.Builder(this)
                    .setTitle("Risk per trade")
                    .setItems(risks, (dialog, which) -> {
                        selectedRisk = risks[which];
                        risk.setText(selectedRisk + "  ▾");
                        sendSetting("RISK", selectedRisk);
                    })
                    .show();
        });

        root.addView(settings);

        // EA MANAGER
        LinearLayout manager = modernCard();
        manager.addView(modernText(
                "EA MANAGER", 16, WHITE, true));

        totalEA = modernText(
                "TOTAL EAs: 0", 12, MUTED, false);
        runningEA = modernText(
                "RUNNING: 0", 12, GREEN, false);

        manager.addView(totalEA);
        manager.addView(runningEA);

        eaContainer = new LinearLayout(this);
        eaContainer.setOrientation(LinearLayout.VERTICAL);
        manager.addView(eaContainer);

        Button addEA = modernButton(
                "+ ADD EA", INNER, GOLD);
        manager.addView(addEA,
                new LinearLayout.LayoutParams(-1, 50));

        addEA.setOnClickListener(v -> showAddEADialog());

        Button stopAll = modernButton(
                "STOP ALL EAs", Color.rgb(35, 15, 22), RED);
        manager.addView(stopAll,
                new LinearLayout.LayoutParams(-1, 50));

        stopAll.setOnClickListener(v -> sendCommand("STOP"));

        root.addView(manager);

        // CHART IMAGE SELECTOR
        createAIChartScanner(root);

        setContentView(scroll);
    }

    private void updateModeButtons(Button scalp, Button swing) {
        boolean isScalp = "SCALP".equals(selectedMode);

        scalp.setBackground(modernBackground(
                isScalp ? GREEN : INNER, 12, 0));
        scalp.setTextColor(isScalp ? Color.BLACK : MUTED);

        swing.setBackground(modernBackground(
                isScalp ? INNER : GREEN, 12, 0));
        swing.setTextColor(isScalp ? MUTED : Color.BLACK);
    }

    private TextView addModernStat(
            LinearLayout row,
            String icon,
            String title,
            String initial,
            int accent,
            boolean margin) {

        LinearLayout stat = new LinearLayout(this);
        stat.setOrientation(LinearLayout.VERTICAL);
        stat.setPadding(10, 12, 7, 12);
        stat.setBackground(modernBackground(
                CARD, 15, Color.rgb(39, 52, 69)));

        stat.addView(modernText(icon, 22, accent, true));
        stat.addView(modernText(title, 9, MUTED, false));

        TextView value = modernText(initial, 15, accent, true);
        stat.addView(value);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(0, -2, 1);
        params.setMargins(margin ? 5 : 0, 0, 0, 0);

        row.addView(stat, params);
        return value;
    }

    // FIREBASE STATUS LISTENER
    private void connectFirebase() {
        if (statusDatabase == null) {
            hostStatus.setText("● FIREBASE UNAVAILABLE");
            return;
        }

        statusListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    hostStatus.setText("● WAITING FOR MT5");
                    hostStatus.setTextColor(GOLD);
                    dashboardEquity.setText("—");
                    dashboardToday.setText("—");
                    dashboardTrades.setText("—");
                    return;
                }

                String status = readString(snapshot, "status", "UNKNOWN");

                hostStatus.setText("● " + status.toUpperCase());

                boolean connected =
                        status.toLowerCase().contains("online")
                        || status.toLowerCase().contains("connected")
                        || status.toLowerCase().contains("running");

                hostStatus.setTextColor(connected ? GREEN : GOLD);

                String equity = readString(snapshot, "equity", "");
                if (equity.isEmpty()) {
                    equity = readString(snapshot, "balance", "—");
                }
                dashboardEquity.setText(
                        equity.isEmpty() ? "—" : equity);

                String today = readString(snapshot, "today_pl", "");
                dashboardToday.setText(
                        today.isEmpty() ? "—" : today);

                String trades = readString(
                        snapshot, "open_trades", "—");
                dashboardTrades.setText(trades);

                if (snapshot.hasChild("mode")) {
                    selectedMode = readString(
                            snapshot, "mode", selectedMode);
                }

                if (snapshot.hasChild("symbol")) {
                    selectedSymbol = readString(
                            snapshot, "symbol", selectedSymbol);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                hostStatus.setText("● FIREBASE ERROR");
                hostStatus.setTextColor(RED);
                Toast.makeText(MainActivity.this,
                        "Status read failed: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        };

        statusDatabase.addValueEventListener(statusListener);
    }

    private String readString(
            DataSnapshot snapshot, String key, String fallback) {

        Object value = snapshot.child(key).getValue();

        if (value == null) {
            return fallback;
        }

        return String.valueOf(value);
    }

    // SEND COMMAND TO THE MT5 BRIDGE
    private void sendCommand(String command) {
        if (commandDatabase == null) {
            Toast.makeText(this,
                    "Firebase is not available",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("command", command);
        data.put("time", System.currentTimeMillis());

        commandDatabase.setValue(data)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this,
                                command + " request sent",
                                Toast.LENGTH_SHORT).show())
                .addOnFailureListener(error ->
                        Toast.makeText(this,
                                "Command failed: " + error.getMessage(),
                                Toast.LENGTH_LONG).show());
    }

    private void sendMode(String mode) {
        if (commandDatabase == null) return;

        Map<String, Object> data = new HashMap<>();
        data.put("command", "MODE");
        data.put("mode", mode);
        data.put("time", System.currentTimeMillis());

        commandDatabase.setValue(data)
                .addOnFailureListener(error ->
                        Toast.makeText(this,
                                "Mode request failed",
                                Toast.LENGTH_SHORT).show());
    }

    private void sendSetting(String setting, String value) {
        if (commandDatabase == null) {
            Toast.makeText(this,
                    "Firebase is not available",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("command", setting);
        data.put("value", value);
        data.put("time", System.currentTimeMillis());

        commandDatabase.setValue(data)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this,
                                setting + " request sent",
                                Toast.LENGTH_SHORT).show())
                .addOnFailureListener(error ->
                        Toast.makeText(this,
                                "Setting failed: " + error.getMessage(),
                                Toast.LENGTH_LONG).show());
    }

    // EA MANAGER
    private void refreshEAList() {
        if (rootDatabase == null || eaContainer == null) return;

        rootDatabase.child("eas")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        eaContainer.removeAllViews();

                        int total = 0;
                        int running = 0;

                        for (DataSnapshot ea : snapshot.getChildren()) {
                            DataSnapshot state = ea.child("status");

                            if (!state.exists()) continue;

                            total++;

                            String name = readString(
                                    state, "ea_name", ea.getKey());

                            String status = readString(
                                    state, "status", "UNKNOWN");

                            if (status.toLowerCase().contains("running")
                                    || status.toLowerCase().contains("online")) {
                                running++;
                            }

                            addEACard(ea.getKey(), name, status);
                        }

                        totalEA.setText("TOTAL EAs: " + total);
                        runningEA.setText("RUNNING: " + running);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(MainActivity.this,
                                "Could not load EA list",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addEACard(
            String id, String name, String status) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(10, 10, 10, 10);
        card.setBackground(modernBackground(INNER, 12, 0));

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(-1, -2);
        cardParams.setMargins(0, 5, 0, 5);
        card.setLayoutParams(cardParams);

        card.addView(modernText(name, 14, WHITE, true));
        card.addView(modernText(
                "ID: " + id, 10, MUTED, false));
        card.addView(modernText(
                "Status: " + status, 11, GREEN, false));

        LinearLayout buttons = modernRow();

        Button start = modernButton("START", GREEN, Color.BLACK);
        Button stop = modernButton(
                "STOP", Color.rgb(35, 15, 22), RED);

        buttons.addView(start,
                new LinearLayout.LayoutParams(0, 45, 1));
        buttons.addView(stop,
                new LinearLayout.LayoutParams(0, 45, 1));

        card.addView(buttons);

        start.setOnClickListener(v -> sendEACommand(id, "START"));
        stop.setOnClickListener(v -> sendEACommand(id, "STOP"));

        eaContainer.addView(card);
    }

    private void sendEACommand(String id, String command) {
        DatabaseReference ref = rootDatabase.child("eas")
                .child(id).child("command");

        Map<String, Object> data = new HashMap<>();
        data.put("command", command);
        data.put("time", System.currentTimeMillis());

        ref.setValue(data)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this,
                                command + " request sent to " + id,
                                Toast.LENGTH_SHORT).show())
                .addOnFailureListener(error ->
                        Toast.makeText(this,
                                "Command failed",
                                Toast.LENGTH_SHORT).show());
    }

    private void showAddEADialog() {
        EditText nameInput = new EditText(this);
        nameInput.setHint("Robot name");

        new AlertDialog.Builder(this)
                .setTitle("Add EA")
                .setMessage(
                        "Register a robot entry. This does not install an EA in MT5.")
                .setView(nameInput)
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("ADD", (dialog, which) -> {
                    String name = nameInput.getText()
                            .toString().trim();

                    if (name.isEmpty()) {
                        Toast.makeText(this,
                                "Enter a robot name",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String id = rootDatabase.child("eas")
                            .push().getKey();

                    if (id == null) return;

                    Map<String, Object> state = new HashMap<>();
                    state.put("ea_name", name);
                    state.put("status", "REGISTERED");
                    state.put("mode", selectedMode);
                    state.put("symbol", selectedSymbol);
                    state.put("equity", "");
                    state.put("today_pl", "");
                    state.put("open_trades", "0");

                    rootDatabase.child("eas")
                            .child(id).child("status")
                            .setValue(state)
                            .addOnFailureListener(error ->
                                    Toast.makeText(this,
                                            "Could not add EA",
                                            Toast.LENGTH_SHORT).show());
                })
                .show();
    }

    // CHART IMAGE PICKER
    // Selecting an image does not itself perform AI analysis.
    private void createAIChartScanner(LinearLayout root) {
        LinearLayout scanner = modernCard();

        scanner.addView(modernText(
                "AI CHART SCANNER", 16, GOLD, true));

        scanner.addView(modernText(
                "Select a chart screenshot for analysis.",
                12, MUTED, false));

        Button upload = modernButton(
                "UPLOAD CHART IMAGE", INNER, WHITE);

        scanner.addView(upload,
                new LinearLayout.LayoutParams(-1, 50));

        chartPreview = new ImageView(this);
        chartPreview.setAdjustViewBounds(true);
        chartPreview.setScaleType(ImageView.ScaleType.FIT_CENTER);

        LinearLayout.LayoutParams imageParams =
                new LinearLayout.LayoutParams(-1, 250);
        imageParams.setMargins(0, 10, 0, 10);

        scanner.addView(chartPreview, imageParams);

        scannerResult = modernText(
                "Waiting for chart image", 12, MUTED, false);
        scanner.addView(scannerResult);

        upload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            startActivityForResult(intent, 1001);
        });

        root.addView(scanner);
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            selectedChartUri = data.getData();

            try {
                getContentResolver().takePersistableUriPermission(
                        selectedChartUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (Exception ignored) {
                // The picker may not grant persistent permission.
            }

            chartPreview.setImageURI(selectedChartUri);
            scannerResult.setText(
                    "Image selected. AI analysis is not connected yet.");
            scannerResult.setTextColor(GOLD);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (statusDatabase != null && statusListener != null) {
            statusDatabase.removeEventListener(statusListener);
        }
    }
}
