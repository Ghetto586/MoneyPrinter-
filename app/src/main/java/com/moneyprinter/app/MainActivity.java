
package com.moneyprinter.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.type.GenerativeBackend;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private final int BG = Color.rgb(7, 10, 15);
    private final int CARD = Color.rgb(17, 22, 30);
    private final int INNER = Color.rgb(25, 32, 42);
    private final int WHITE = Color.WHITE;
    private final int MUTED = Color.rgb(145, 154, 168);
    private final int GREEN = Color.rgb(0, 230, 118);
    private final int RED = Color.rgb(255, 82, 82);
    private final int GOLD = Color.rgb(255, 193, 7);
    private final int BLUE = Color.rgb(80, 150, 255);

    private static final String EA_ID = "moneymaker_autoscalp_01";
    private DatabaseReference eaDatabase;

    private final ArrayList<EAItem> eaList = new ArrayList<>();

    private LinearLayout eaContainer;
    private TextView hostStatus;
    private TextView totalEA;
    private TextView runningEA;

    private ImageView chartPreview;
    private TextView chartStatus;
    private TextView aiResult;
    private Button analyzeButton;

    private Uri selectedChartUri;
    private static final int PICK_CHART = 501;

    private static class EAItem {
        String id;
        String name;
        String mode;
        boolean running;

        String symbol = "—";
        String signal = "—";
        String balance = "—";
        String equity = "—";
        String floating = "—";
        String trades = "—";

        EAItem(String id, String name, String mode, boolean running) {
            this.id = id;
            this.name = name;
            this.mode = mode;
            this.running = running;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);

        FirebaseApp.initializeApp(this);

        // Development testing only. Do not ship the debug provider in production.
        FirebaseAppCheck appCheck = FirebaseAppCheck.getInstance();
        appCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
        );

        eaList.add(new EAItem(
                EA_ID,
                "MoneyMaker AutoScalp",
                "SCALP",
                false
        ));

        createDashboard();
        connectFirebase();
    }

    // FIREBASE CONNECTION

    private void connectFirebase() {
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            eaDatabase = database.getReference("eas")
                    .child(EA_ID)
                    .child("status");

            eaDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        hostStatus.setText("● WAITING FOR MT5");
                        hostStatus.setTextColor(GOLD);
                        return;
                    }

                    updateEAFromFirebase(snapshot);

                    hostStatus.setText("● MT5 HOST CONNECTED");
                    hostStatus.setTextColor(GREEN);
                    refreshEAList();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    hostStatus.setText("● FIREBASE ERROR");
                    hostStatus.setTextColor(RED);

                    Log.e(
                            "MoneyPrinter",
                            "Firebase listener cancelled",
                            error.toException()
                    );
                }
            });
        } catch (Exception e) {
            hostStatus.setText("● FIREBASE NOT CONNECTED");
            hostStatus.setTextColor(RED);

            Log.e("MoneyPrinter", "Firebase setup failed", e);
        }
    }

    private void updateEAFromFirebase(DataSnapshot snapshot) {
        if (eaList.isEmpty()) return;

        EAItem ea = eaList.get(0);

        String name = snapshot.child("ea_name").getValue(String.class);
        String status = snapshot.child("status").getValue(String.class);
        String mode = snapshot.child("mode").getValue(String.class);
        String symbol = snapshot.child("symbol").getValue(String.class);
        String signal = snapshot.child("signal").getValue(String.class);

        if (name != null) ea.name = name;

        if (status != null) {
            ea.running = status.equalsIgnoreCase("RUNNING");
        }

        if (mode != null) ea.mode = mode;
        if (symbol != null) ea.symbol = symbol;
        if (signal != null) ea.signal = signal;

        Object balance = snapshot.child("balance").getValue();
        Object equity = snapshot.child("equity").getValue();
        Object floating = snapshot.child("floating_pl").getValue();
        Object trades = snapshot.child("open_trades").getValue();

        if (balance != null) ea.balance = String.valueOf(balance);
        if (equity != null) ea.equity = String.valueOf(equity);
        if (floating != null) ea.floating = String.valueOf(floating);
        if (trades != null) ea.trades = String.valueOf(trades);
    }

    // SEND EA COMMAND
    // A successful Firebase write confirms only that the command was written.

    private void sendCommand(String command) {
        if (eaDatabase == null) {
            Toast.makeText(
                    this,
                    "Firebase is not connected",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        DatabaseReference commandRef = FirebaseDatabase.getInstance()
                .getReference("eas")
                .child(EA_ID)
                .child("command");

        Map<String, Object> data = new HashMap<>();
        data.put("command", command);
        data.put("time", System.currentTimeMillis());

        commandRef.setValue(data)
                .addOnSuccessListener(unused ->
                        Toast.makeText(
                                MainActivity.this,
                                command + " command sent to Firebase",
                                Toast.LENGTH_SHORT
                        ).show()
                )
                .addOnFailureListener(error ->
                        Toast.makeText(
                                MainActivity.this,
                                "Command failed: " + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void sendMode(String mode) {
        if (eaDatabase == null) {
            Toast.makeText(
                    this,
                    "Firebase is not connected",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        DatabaseReference commandRef = FirebaseDatabase.getInstance()
                .getReference("eas")
                .child(EA_ID)
                .child("command");

        Map<String, Object> data = new HashMap<>();
        data.put("command", "MODE");
        data.put("mode", mode);
        data.put("time", System.currentTimeMillis());

        commandRef.setValue(data)
                .addOnSuccessListener(unused ->
                        Toast.makeText(
                                MainActivity.this,
                                mode + " mode command sent",
                                Toast.LENGTH_SHORT
                        ).show()
                )
                .addOnFailureListener(error ->
                        Toast.makeText(
                                MainActivity.this,
                                "Mode update failed: " + error.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    // TEXT AND CARD HELPERS

    private TextView text(String value, float size, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setPadding(0, 4, 0, 4);
        return view;
    }

    private TextView heading(String value) {
        TextView view = text(value, 17, WHITE);
        view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return view;
    }

    private LinearLayout card() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 18, 20, 18);

        GradientDrawable background = new GradientDrawable();
        background.setColor(CARD);
        background.setCornerRadius(22);
        layout.setBackground(background);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(0, 8, 0, 8);
        layout.setLayoutParams(params);
        return layout;
    }

    private Button button(String value) {
        Button button = new Button(this);
        button.setText(value);
        button.setTextSize(13);
        button.setTextColor(WHITE);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setAllCaps(false);
        button.setGravity(Gravity.CENTER);
        return button;
    }

    // MAIN DASHBOARD

    private void createDashboard() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BG);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18, 24, 18, 25);
        scroll.addView(root);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView money = text("MONEY", 24, WHITE);
        money.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        TextView printer = text("PRINTER", 24, GREEN);
        printer.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        header.addView(money);
        header.addView(printer);

        TextView settings = text("⚙", 25, MUTED);
        settings.setGravity(Gravity.RIGHT);

        header.addView(settings, new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        ));

        root.addView(header);
        root.addView(text("MULTI-EA HOST  •  MT5", 12, MUTED));

        hostStatus = text("● CONNECTING TO FIREBASE...", 15, GOLD);
        hostStatus.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        hostStatus.setPadding(0, 18, 0, 5);
        root.addView(hostStatus);

        root.addView(text(
                "Live MT5 data appears when the EA connects.",
                12,
                MUTED
        ));

        LinearLayout summary = card();
        summary.addView(heading("EA MANAGER"));

        totalEA = text("TOTAL EAs: 0", 14, WHITE);
        runningEA = text("RUNNING: 0", 14, GREEN);

        summary.addView(totalEA);
        summary.addView(runningEA);
        root.addView(summary);

        LinearLayout listCard = card();
        listCard.addView(heading("YOUR EAs"));

        eaContainer = new LinearLayout(this);
        eaContainer.setOrientation(LinearLayout.VERTICAL);
        listCard.addView(eaContainer);
        root.addView(listCard);

        Button addEA = button("+ ADD EA");
        addEA.setTextSize(15);

        root.addView(addEA, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                62
        ));

        addEA.setOnClickListener(v -> showAddEADialog());

        Button stopAll = button("STOP ALL EAs");
        stopAll.setTextColor(RED);

        root.addView(stopAll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                62
        ));

        stopAll.setOnClickListener(v -> {
            sendCommand("STOP");
            for (EAItem ea : eaList) ea.running = false;
            refreshEAList();
        });

        refreshEAList();
        createAIChartScanner(root);
        setContentView(scroll);
    }

    // AI CHART SCANNER UI

    private void createAIChartScanner(LinearLayout root) {
        LinearLayout aiCard = card();

        aiCard.addView(heading("🤖 AI CHART SCANNER"));
        aiCard.addView(text(
                "Upload an MT5 chart for a BUY, SELL or WAIT assessment.",
                12,
                MUTED
        ));

        chartPreview = new ImageView(this);
        chartPreview.setScaleType(ImageView.ScaleType.FIT_CENTER);

        GradientDrawable previewBackground = new GradientDrawable();
        previewBackground.setColor(Color.rgb(10, 14, 20));
        previewBackground.setCornerRadius(18);
        chartPreview.setBackground(previewBackground);
        chartPreview.setImageResource(android.R.drawable.ic_menu_gallery);

        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                430
        );

        imageParams.setMargins(0, 15, 0, 12);
        aiCard.addView(chartPreview, imageParams);

        chartStatus = text("WAITING FOR CHART", 13, GOLD);
        chartStatus.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        aiCard.addView(chartStatus);

        Button upload = button("📷 UPLOAD MT5 CHART");
        upload.setTextSize(15);

        aiCard.addView(upload, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                62
        ));

        analyzeButton = button("🤖 ANALYZE CHART");
        analyzeButton.setTextSize(15);
        analyzeButton.setTextColor(GREEN);

        aiCard.addView(analyzeButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                62
        ));

        aiResult = text(
                "MONEYPRINTER AI\n\nUpload a chart screenshot to begin.",
                14,
                WHITE
        );

        aiResult.setPadding(0, 18, 0, 5);
        aiCard.addView(aiResult);

        upload.setOnClickListener(v -> openChartPicker());
        analyzeButton.setOnClickListener(v -> analyzeChart());

        // This control intentionally does not execute trades.
        // Actual order execution requires a verified MT5 bridge.
        Button execute = button("EXECUTE TRADE — MT5 BRIDGE REQUIRED");
        execute.setTextColor(MUTED);
        execute.setEnabled(false);

        aiCard.addView(execute, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                58
        ));

        aiCard.addView(text(
                "AI analysis is informational. Verify live prices and risk " +
                "before making any trading decision.",
                11,
                MUTED
        ));

        root.addView(aiCard);
    }

    // PICK A CHART IMAGE

    private void openChartPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(intent, PICK_CHART);
        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Could not open image picker",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != PICK_CHART ||
                resultCode != RESULT_OK ||
                data == null ||
                data.getData() == null) {
            return;
        }

        selectedChartUri = data.getData();

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            InputStream boundsInput =
                    getContentResolver().openInputStream(selectedChartUri);

            if (boundsInput != null) {
                try {
                    BitmapFactory.decodeStream(boundsInput, null, options);
                } finally {
                    boundsInput.close();
                }
            }

            options.inJustDecodeBounds = false;
            options.inSampleSize = 2;

            InputStream imageInput =
                    getContentResolver().openInputStream(selectedChartUri);

            if (imageInput == null) {
                throw new Exception("Cannot open selected image");
            }

            Bitmap bitmap;

            try {
                bitmap = BitmapFactory.decodeStream(
                        imageInput,
                        null,
                        options
                );
            } finally {
                imageInput.close();
            }

            if (bitmap == null) {
                throw new Exception("Invalid image");
            }

            chartPreview.setImageBitmap(bitmap);
            chartStatus.setText("● CHART LOADED — READY FOR AI");
            chartStatus.setTextColor(GREEN);

            aiResult.setText(
                    "MONEYPRINTER AI\n\n" +
                    "Chart loaded successfully.\n" +
                    "Press ANALYZE CHART to begin."
            );

        } catch (Exception e) {
            Log.e("MoneyPrinterAI", "Image preview failed", e);

            Toast.makeText(
                    this,
                    "Could not load chart image",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // AI CHART ANALYSIS: REQUEST A CLEAR BUY / SELL / WAIT DECISION

    private void analyzeChart() {
        if (selectedChartUri == null) {
            Toast.makeText(
                    this,
                    "Upload an MT5 chart first",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        analyzeButton.setEnabled(false);
        chartStatus.setText("● PREPARING AI ANALYSIS...");
        chartStatus.setTextColor(BLUE);

        aiResult.setText(
                "MONEYPRINTER AI\n\n" +
                "Analyzing the chart and evaluating BUY / SELL / WAIT..."
        );

        final Uri chartUri = selectedChartUri;

        Thread worker = new Thread(() -> {
            Bitmap bitmap = null;

            try {
                InputStream input =
                        getContentResolver().openInputStream(chartUri);

                if (input == null) {
                    throw new Exception("Could not open chart image");
                }

                try {
                    BitmapFactory.Options options =
                            new BitmapFactory.Options();

                    options.inSampleSize = 2;

                    bitmap = BitmapFactory.decodeStream(
                            input,
                            null,
                            options
                    );
                } finally {
                    input.close();
                }

                if (bitmap == null) {
                    throw new Exception("Invalid chart image");
                }

                int maxDimension = 1280;
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                if (width > maxDimension || height > maxDimension) {
                    float scale = Math.min(
                            (float) maxDimension / width,
                            (float) maxDimension / height
                    );

                    Bitmap resized = Bitmap.createScaledBitmap(
                            bitmap,
                            Math.max(1, Math.round(width * scale)),
                            Math.max(1, Math.round(height * scale)),
                            true
                    );

                    if (resized != bitmap) {
                        bitmap.recycle();
                        bitmap = resized;
                    }
                }

                final Bitmap chartBitmap = bitmap;

                Content prompt = new Content.Builder()
                        .addImage(chartBitmap)
                        .addText(
                                "You are MoneyPrinter AI, a cautious trading " +
                                "chart analysis assistant. Analyze the uploaded " +
                                "chart screenshot and use the exact headings below.\n\n" +

                                "DECISION: BUY, SELL, or WAIT\n" +
                                "SYMBOL: State only if clearly readable; " +
                                "otherwise Unknown\n" +
                                "TIMEFRAME: State only if clearly readable; " +
                                "otherwise Unknown\n" +
                                "CONFIDENCE: Low, Medium, or High, with a short " +
                                "explanation\n" +
                                "TREND: Bullish, Bearish, Sideways, or Unclear\n" +
                                "REASON: Explain the visible evidence\n" +
                                "ENTRY: Give a price only if reliably readable " +
                                "and justified; otherwise Unavailable\n" +
                                "STOP-LOSS: Give a price only if reliably " +
                                "justified; otherwise Unavailable\n" +
                                "TAKE-PROFIT: Give a price only if reliably " +
                                "justified; otherwise Unavailable\n" +
                                "CONFIRMATION NEEDED: State what must happen " +
                                "before entry\n" +
                                "RISK WARNING: State the main uncertainty or risk\n\n" +

                                "Choose WAIT if the chart is unclear, contradictory, " +
                                "missing essential information, or lacks a " +
                                "convincing setup. Do not invent prices, indicators, " +
                                "symbol names, or timeframes. Confidence describes " +
                                "your visual assessment, not the probability of " +
                                "profit. A screenshot may be delayed and does not " +
                                "provide reliable live prices by itself. Never " +
                                "claim that a trade was executed. This analysis " +
                                "must not trigger a trade. Use clear, concise language."
                        )
                        .build();

                GenerativeModel ai = FirebaseAI.getInstance(
                        GenerativeBackend.googleAI()
                ).generativeModel("gemini-3.8-flash");

                GenerativeModelFutures model =
                        GenerativeModelFutures.from(ai);

                ListenableFuture<GenerateContentResponse> response =
                        model.generateContent(prompt);

                Futures.addCallback(
                        response,
                        new FutureCallback<GenerateContentResponse>() {
                            @Override
                            public void onSuccess(GenerateContentResponse result) {
                                String resultText = result.getText();

                                if (resultText == null ||
                                        resultText.trim().isEmpty()) {
                                    resultText =
                                            "The AI returned an empty response. " +
                                            "Please try again.";
                                }

                                aiResult.setText(
                                        "MONEYPRINTER AI — CHART DECISION\n\n" +
                                        resultText +
                                        "\n\nIMPORTANT: This is visual chart " +
                                        "analysis, not a guaranteed signal. " +
                                        "Verify live prices and risk before acting. " +
                                        "No trade has been executed."
                                );

                                chartStatus.setText("● AI ANALYSIS COMPLETE");
                                chartStatus.setTextColor(GREEN);
                                analyzeButton.setEnabled(true);

                                if (!chartBitmap.isRecycled()) {
                                    chartBitmap.recycle();
                                }
                            }

                            @Override
                            public void onFailure(Throwable error) {
                                Log.e(
                                        "MoneyPrinterAI",
                                        "AI request failed",
                                        error
                                );

                                chartStatus.setText("● AI REQUEST FAILED");
                                chartStatus.setTextColor(RED);
                                analyzeButton.setEnabled(true);

                                String message = error.getLocalizedMessage();

                                aiResult.setText(
                                        "AI ANALYSIS FAILED\n\n" +
                                        error.getClass().getSimpleName() +
                                        "\n" +
                                        (message == null
                                                ? "No extra details available."
                                                : message) +
                                        "\n\nCheck internet access, Firebase AI " +
                                        "Logic setup, App Check debug-token " +
                                        "registration, model availability and quota."
                                );

                                if (!chartBitmap.isRecycled()) {
                                    chartBitmap.recycle();
                                }
                            }
                        },
                        ContextCompat.getMainExecutor(MainActivity.this)
                );

            } catch (Exception error) {
                Log.e(
                        "MoneyPrinterAI",
                        "Could not start chart analysis",
                        error
                );

                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }

                runOnUiThread(() -> {
                    chartStatus.setText("● CHART ANALYSIS ERROR");
                    chartStatus.setTextColor(RED);
                    analyzeButton.setEnabled(true);

                    aiResult.setText(
                            "Could not prepare the chart.\n\n" +
                            (error.getLocalizedMessage() == null
                                    ? "Please try again."
                                    : error.getLocalizedMessage())
                    );
                });
            }
        });

        worker.start();
    }

    // REFRESH EA LIST

    private void refreshEAList() {
        if (eaContainer == null) return;

        eaContainer.removeAllViews();

        int runningCount = 0;

        for (int i = 0; i < eaList.size(); i++) {
            EAItem ea = eaList.get(i);

            if (ea.running) runningCount++;

            createEACard(ea, i);
        }

        totalEA.setText("TOTAL EAs: " + eaList.size());
        runningEA.setText("RUNNING: " + runningCount);
    }

    // EA CARD

    private void createEACard(final EAItem ea, final int index) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setPadding(16, 16, 16, 16);

        GradientDrawable background = new GradientDrawable();
        background.setColor(INNER);
        background.setCornerRadius(18);
        item.setBackground(background);

        LinearLayout.LayoutParams itemParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        itemParams.setMargins(0, 7, 0, 7);
        item.setLayoutParams(itemParams);

        TextView name = text(ea.name, 17, WHITE);
        name.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        item.addView(name);

        String statusText = ea.running
                ? "● RUNNING     " + ea.mode
                : "● STOPPED     " + ea.mode;

        item.addView(text(
                statusText,
                13,
                ea.running ? GREEN : MUTED
        ));

        item.addView(text(
                "SYMBOL    " + ea.symbol +
                "\nSIGNAL    " + ea.signal +
                "\nBALANCE   " + ea.balance +
                "\nEQUITY    " + ea.equity +
                "\nFLOATING  " + ea.floating +
                "\nTRADES    " + ea.trades,
                13,
                WHITE
        ));

        LinearLayout modeRow = new LinearLayout(this);
        modeRow.setOrientation(LinearLayout.HORIZONTAL);

        Button scalp = button("SCALP");
        Button swing = button("SWING");

        modeRow.addView(scalp, new LinearLayout.LayoutParams(0, 55, 1));
        modeRow.addView(swing, new LinearLayout.LayoutParams(0, 55, 1));
        item.addView(modeRow);

        LinearLayout controls = new LinearLayout(this);
        controls.setOrientation(LinearLayout.HORIZONTAL);

        Button start = button("START");
        Button stop = button("STOP");

        controls.addView(start, new LinearLayout.LayoutParams(0, 55, 1));
        controls.addView(stop, new LinearLayout.LayoutParams(0, 55, 1));
        item.addView(controls);

        scalp.setOnClickListener(v -> {
            ea.mode = "SCALP";

            if (ea.id.equals(EA_ID)) sendMode("SCALP");

            refreshEAList();
        });

        swing.setOnClickListener(v -> {
            ea.mode = "SWING";

            if (ea.id.equals(EA_ID)) sendMode("SWING");

            refreshEAList();
        });

        start.setOnClickListener(v -> {
            if (ea.id.equals(EA_ID)) {
                sendCommand("START");
            } else {
                Toast.makeText(
                        MainActivity.this,
                        "This EA has no configured MT5 connection yet",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            // Display is optimistic; actual running status must come from MT5.
            ea.running = true;
            refreshEAList();
        });

        stop.setOnClickListener(v -> {
            if (ea.id.equals(EA_ID)) {
                sendCommand("STOP");
            }

            ea.running = false;
            refreshEAList();
        });

        eaContainer.addView(item);
    }

    // ADD EA DIALOG

    private void showAddEADialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(35, 25, 35, 20);

        layout.addView(heading("ADD NEW EA"));

        final EditText nameInput = new EditText(this);
        nameInput.setHint("EA name");
        nameInput.setTextColor(WHITE);
        nameInput.setHintTextColor(MUTED);
        layout.addView(nameInput);

        Button add = button("ADD EA");
        layout.addView(add);

        add.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(
                        MainActivity.this,
                        "Enter an EA name",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            eaList.add(new EAItem(
                    "local_" + System.currentTimeMillis(),
                    name,
                    "SCALP",
                    false
            ));

            refreshEAList();
            dialog.dismiss();

            Toast.makeText(
                    MainActivity.this,
                    name + " added locally; MT5 connection is not configured",
                    Toast.LENGTH_LONG
            ).show();
        });

        dialog.setView(layout);
        dialog.show();
    }
}
