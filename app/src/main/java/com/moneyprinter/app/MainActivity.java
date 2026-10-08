package com.moneyprinter.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.graphics.Bitmap;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class MainActivity extends Activity {

    // =========================
    // MONEYPRINTER COLORS
    // =========================

    private final int BG = Color.rgb(7, 10, 15);
    private final int CARD = Color.rgb(17, 22, 30);
    private final int INNER = Color.rgb(25, 32, 42);
    private final int WHITE = Color.rgb(255, 255, 255);
    private final int MUTED = Color.rgb(145, 154, 168);
    private final int GREEN = Color.rgb(0, 230, 118);
    private final int RED = Color.rgb(255, 82, 82);
    private final int GOLD = Color.rgb(255, 193, 7);
    private final int BLUE = Color.rgb(80, 150, 255);

    // =========================
    // FIREBASE
    // =========================

    private static final String EA_ID =
            "moneymaker_autoscalp_01";

    private DatabaseReference eaDatabase;

    // =========================
    // EA DATA
    // =========================

    private ArrayList<EAItem> eaList =
            new ArrayList<>();

    private LinearLayout eaContainer;

    private TextView hostStatus;
    private TextView totalEA;
    private TextView runningEA;

    // =========================
    // AI SCANNER
    // =========================

    private ImageView chartPreview;
    private TextView chartStatus;
    private TextView aiResult;

    private Uri selectedChartUri;

    private static final int PICK_CHART =
            501;

    // =========================
    // EA MODEL
    // =========================

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

        EAItem(
                String id,
                String name,
                String mode,
                boolean running
        ) {
            this.id = id;
            this.name = name;
            this.mode = mode;
            this.running = running;
        }
    }

    // =========================
    // ACTIVITY
    // =========================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);

        eaList.add(
                new EAItem(
                        EA_ID,
                        "MoneyMaker AutoScalp",
                        "SCALP",
                        false
                )
        );

        createDashboard();

        connectFirebase();
    }

    // =========================
    // FIREBASE CONNECTION
    // =========================

    private void connectFirebase() {

        try {

            FirebaseDatabase database =
                    FirebaseDatabase.getInstance();

            eaDatabase =
                    database
                            .getReference("eas")
                            .child(EA_ID)
                            .child("status");

            eaDatabase.addValueEventListener(
                    new ValueEventListener() {

                        @Override
                        public void onDataChange(
                                DataSnapshot snapshot
                        ) {

                            if (!snapshot.exists()) {

                                hostStatus.setText(
                                        "●  WAITING FOR MT5"
                                );

                                hostStatus.setTextColor(
                                        GOLD
                                );

                                return;
                            }

                            updateEAFromFirebase(snapshot);

                            hostStatus.setText(
                                    "●  MT5 HOST CONNECTED"
                            );

                            hostStatus.setTextColor(
                                    GREEN
                            );

                            refreshEAList();
                        }

                        @Override
                        public void onCancelled(
                                DatabaseError error
                        ) {

                            hostStatus.setText(
                                    "●  FIREBASE ERROR"
                            );

                            hostStatus.setTextColor(
                                    RED
                            );
                        }
                    }
            );

        } catch (Exception e) {

            hostStatus.setText(
                    "●  FIREBASE NOT CONNECTED"
            );

            hostStatus.setTextColor(
                    RED
            );
        }
    }

    // =========================
    // READ FIREBASE DATA
    // =========================

    private void updateEAFromFirebase(
            DataSnapshot snapshot
    ) {

        if (eaList.size() == 0) {
            return;
        }

        EAItem ea = eaList.get(0);

        String name =
                snapshot.child("ea_name")
                        .getValue(String.class);

        String status =
                snapshot.child("status")
                        .getValue(String.class);

        String mode =
                snapshot.child("mode")
                        .getValue(String.class);

        String symbol =
                snapshot.child("symbol")
                        .getValue(String.class);

        String signal =
                snapshot.child("signal")
                        .getValue(String.class);

        if (name != null)
            ea.name = name;

        if (status != null)
            ea.running =
                    status.equalsIgnoreCase("RUNNING");

        if (mode != null)
            ea.mode = mode;

        if (symbol != null)
            ea.symbol = symbol;

        if (signal != null)
            ea.signal = signal;

        Object balance =
                snapshot.child("balance")
                        .getValue();

        Object equity =
                snapshot.child("equity")
                        .getValue();

        Object floating =
                snapshot.child("floating_pl")
                        .getValue();

        Object trades =
                snapshot.child("open_trades")
                        .getValue();

        if (balance != null)
            ea.balance =
                    String.valueOf(balance);

        if (equity != null)
            ea.equity =
                    String.valueOf(equity);

        if (floating != null)
            ea.floating =
                    String.valueOf(floating);

        if (trades != null)
            ea.trades =
                    String.valueOf(trades);
    }

    // =========================
    // SEND COMMAND
    // =========================

    private void sendCommand(
            String command
    ) {

        if (eaDatabase == null) {

            Toast.makeText(
                    this,
                    "Firebase is not connected",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        DatabaseReference commandRef =
                FirebaseDatabase
                        .getInstance()
                        .getReference("eas")
                        .child(EA_ID)
                        .child("command");

        Map<String, Object> data =
                new HashMap<>();

        data.put(
                "command",
                command
        );

        data.put(
                "time",
                System.currentTimeMillis()
        );

        commandRef.setValue(data);

        Toast.makeText(
                this,
                command + " command sent",
                Toast.LENGTH_SHORT
        ).show();
    }

    // =========================
    // SEND MODE
    // =========================

    private void sendMode(
            String mode
    ) {

        if (eaDatabase == null) {
            return;
        }

        DatabaseReference commandRef =
                FirebaseDatabase
                        .getInstance()
                        .getReference("eas")
                        .child(EA_ID)
                        .child("command");

        Map<String, Object> data =
                new HashMap<>();

        data.put(
                "command",
                "MODE"
        );

        data.put(
                "mode",
                mode
        );

        data.put(
                "time",
                System.currentTimeMillis()
        );

        commandRef.setValue(data);
    }

    // =========================
    // TEXT
    // =========================

    private TextView text(
            String value,
            float size,
            int color
    ) {

        TextView view =
                new TextView(this);

        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);

        view.setPadding(
                0,
                4,
                0,
                4
        );

        return view;
    }

    private TextView heading(
            String value
    ) {

        TextView view =
                text(
                        value,
                        17,
                        WHITE
                );

        view.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        return view;
    }

    // =========================
    // CARD
    // =========================

    private LinearLayout card() {

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.setPadding(
                20,
                18,
                20,
                18
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(CARD);
        background.setCornerRadius(22);

        layout.setBackground(
                background
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                0,
                8,
                0,
                8
        );

        layout.setLayoutParams(params);

        return layout;
    }

    // =========================
    // BUTTON
    // =========================

    private Button button(
            String value
    ) {

        Button button =
                new Button(this);

        button.setText(value);
        button.setTextSize(13);
        button.setTextColor(WHITE);

        button.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        button.setAllCaps(false);

        button.setGravity(
                Gravity.CENTER
        );

        return button;
    }

    // =========================
    // DASHBOARD
    // =========================

    private void createDashboard() {

        ScrollView scroll =
                new ScrollView(this);

        scroll.setBackgroundColor(BG);

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setPadding(
                18,
                24,
                18,
                25
        );

        scroll.addView(root);

        // =========================
        // HEADER
        // =========================

        LinearLayout header =
                new LinearLayout(this);

        header.setOrientation(
                LinearLayout.HORIZONTAL
        );

        header.setGravity(
                Gravity.CENTER_VERTICAL
        );

        TextView money =
                text(
                        "MONEY",
                        24,
                        WHITE
                );

        money.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        TextView printer =
                text(
                        "PRINTER",
                        24,
                        GREEN
                );

        printer.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        header.addView(money);
        header.addView(printer);

        TextView settings =
                text(
                        "⚙",
                        25,
                        MUTED
                );

        settings.setGravity(
                Gravity.RIGHT
        );

        header.addView(
                settings,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        root.addView(header);

        root.addView(
                text(
                        "MULTI-EA HOST  •  MT5",
                        12,
                        MUTED
                )
        );

        // =========================
        // HOST STATUS
        // =========================

        hostStatus =
                text(
                        "●  CONNECTING TO FIREBASE...",
                        15,
                        GOLD
                );

        hostStatus.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        hostStatus.setPadding(
                0,
                18,
                0,
                5
        );

        root.addView(hostStatus);

        root.addView(
                text(
                        "Live MT5 data will appear when the EA connects.",
                        12,
                        MUTED
                )
        );

        // =========================
        // EA SUMMARY
        // =========================

        LinearLayout summary =
                card();

        summary.addView(
                heading("EA MANAGER")
        );

        totalEA =
                text(
                        "TOTAL EAs: 0",
                        14,
                        WHITE
                );

        runningEA =
                text(
                        "RUNNING: 0",
                        14,
                        GREEN
                );

        summary.addView(totalEA);
        summary.addView(runningEA);

        root.addView(summary);

        // =========================
        // EA LIST
        // =========================

        LinearLayout listCard =
                card();

        listCard.addView(
                heading("YOUR EAs")
        );

        eaContainer =
                new LinearLayout(this);

        eaContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        listCard.addView(
                eaContainer
        );

        root.addView(listCard);

        // =========================
        // ADD EA
        // =========================

        Button addEA =
                button(
                        "+ ADD EA"
                );

        addEA.setTextSize(15);

        root.addView(
                addEA,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        62
                )
        );

        // =========================
        // STOP ALL
        // =========================

        Button stopAll =
                button(
                        "STOP ALL EAs"
                );

        stopAll.setTextColor(
                RED
        );

        root.addView(
                stopAll,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        62
                )
        );

        addEA.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        showAddEADialog();
                    }
                }
        );

        stopAll.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        for (EAItem ea : eaList) {
                            ea.running = false;
                        }

                        sendCommand("STOP");

                        refreshEAList();

                        Toast.makeText(
                                MainActivity.this,
                                "STOP ALL command sent",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );

        refreshEAList();

        // =========================
        // AI CHART SCANNER
        // =========================

        createAIChartScanner(root);

        setContentView(scroll);
    }

    // =========================
    // AI CHART SCANNER
    // =========================

    private void createAIChartScanner(
            LinearLayout root
    ) {

        LinearLayout aiCard =
                card();

        aiCard.addView(
                heading(
                        "🤖 AI CHART SCANNER"
                )
        );

        aiCard.addView(
                text(
                        "Upload an MT5 chart screenshot for AI analysis.",
                        12,
                        MUTED
                )
        );

        // Chart preview

        chartPreview =
                new ImageView(this);

        chartPreview.setScaleType(
                ImageView.ScaleType.CENTER_CROP
        );

        GradientDrawable previewBackground =
                new GradientDrawable();

        previewBackground.setColor(
                Color.rgb(10, 14, 20)
        );

        previewBackground.setCornerRadius(
                18
        );

        chartPreview.setBackground(
                previewBackground
        );

        chartPreview.setImageResource(
                android.R.drawable.ic_menu_gallery
        );

        LinearLayout.LayoutParams imageParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        430
                );

        imageParams.setMargins(
                0,
                15,
                0,
                12
        );

        aiCard.addView(
                chartPreview,
                imageParams
        );

        // Status

        chartStatus =
                text(
                        "WAITING FOR CHART",
                        13,
                        GOLD
                );

        chartStatus.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        aiCard.addView(chartStatus);

        // Upload button

        Button upload =
                button(
                        "📷  UPLOAD MT5 CHART"
                );

        upload.setTextSize(15);

        aiCard.addView(
                upload,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        62
                )
        );

        // Analyze button

        Button analyze =
                button(
                        "🤖  ANALYZE CHART"
                );

        analyze.setTextSize(15);
        analyze.setTextColor(GREEN);

        aiCard.addView(
                analyze,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        62
                )
        );

        // AI result

        aiResult =
                text(
                        "AI ANALYSIS\n\n" +
                        "Upload an MT5 screenshot to begin.",
                        14,
                        WHITE
                );

        aiResult.setPadding(
                0,
                18,
                0,
                5
        );

        aiCard.addView(aiResult);

        // Upload click

        upload.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        openChartPicker();
                    }
                }
        );

        // Analyze click

        analyze.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        analyzeChart();
                    }
                }
        );

        root.addView(aiCard);
    }

    // =========================
    // OPEN IMAGE PICKER
    // =========================

    private void openChartPicker() {

        Intent intent =
                new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );

        startActivityForResult(
                intent,
                PICK_CHART
        );
    }

    // =========================
    // RECEIVE IMAGE
    // =========================

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (
                requestCode == PICK_CHART &&
                resultCode == RESULT_OK &&
                data != null
        ) {

            selectedChartUri =
                    data.getData();

            try {

                Bitmap bitmap =
                        MediaStore.Images.Media.getBitmap(
                                getContentResolver(),
                                selectedChartUri
                        );

                chartPreview.setImageBitmap(
                        bitmap
                );

                chartStatus.setText(
                        "● CHART LOADED — READY FOR AI"
                );

                chartStatus.setTextColor(
                        GREEN
                );

                aiResult.setText(
                        "AI ANALYSIS\n\n" +
                        "Chart loaded successfully.\n" +
                        "Press ANALYZE CHART to continue."
                );

            } catch (Exception e) {

                Toast.makeText(
                        this,
                        "Could not load chart",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    // =========================
    // ANALYZE CHART
    // =========================

    private void analyzeChart() {

        if (selectedChartUri == null) {

            Toast.makeText(
                    this,
                    "Upload an MT5 chart first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        chartStatus.setText(
                "● AI ANALYSIS READY"
        );

        chartStatus.setTextColor(
                BLUE
        );

        aiResult.setText(
                "AI MARKET ANALYSIS\n\n" +

                "TREND        WAITING\n" +
                "MOMENTUM     WAITING\n" +
                "STRUCTURE    WAITING\n" +
                "SUPPORT      WAITING\n" +
                "RESISTANCE   WAITING\n" +
                "CONFIDENCE   —\n\n" +

                "SIGNAL\n" +
                "WAIT\n\n" +

                "AI SUMMARY\n" +
                "The chart has been loaded. " +
                "The live AI vision service " +
                "will be connected in the next " +
                "MoneyPrinter AI layer.\n\n" +

                "⚠️ No trading decision has been " +
                "generated yet."
        );

        Toast.makeText(
                this,
                "Chart prepared for AI analysis",
                Toast.LENGTH_SHORT
        ).show();
    }

    // =========================
    // REFRESH EA LIST
    // =========================

    private void refreshEAList() {

        if (eaContainer == null) {
            return;
        }

        eaContainer.removeAllViews();

        int runningCount = 0;

        for (
                int i = 0;
                i < eaList.size();
                i++
        ) {

            EAItem ea =
                    eaList.get(i);

            if (ea.running) {
                runningCount++;
            }

            createEACard(
                    ea,
                    i
            );
        }

        totalEA.setText(
                "TOTAL EAs: " +
                        eaList.size()
        );

        runningEA.setText(
                "RUNNING: " +
                        runningCount
        );
    }

    // =========================
    // EA CARD
    // =========================

    private void createEACard(
            final EAItem ea,
            final int index
    ) {

        LinearLayout item =
                new LinearLayout(this);

        item.setOrientation(
                LinearLayout.VERTICAL
        );

        item.setPadding(
                16,
                16,
                16,
                16
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(INNER);
        background.setCornerRadius(18);

        item.setBackground(
                background
        );

        LinearLayout.LayoutParams itemParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        itemParams.setMargins(
                0,
                7,
                0,
                7
        );

        item.setLayoutParams(itemParams);

        TextView name =
                text(
                        ea.name,
                        17,
                        WHITE
                );

        name.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        item.addView(name);

        String statusText;

        if (ea.running) {

            statusText =
                    "● RUNNING     " +
                            ea.mode;

        } else {

            statusText =
                    "● STOPPED     " +
                            ea.mode;
        }

        TextView state =
                text(
                        statusText,
                        13,
                        ea.running
                                ? GREEN
                                : MUTED
                );

        item.addView(state);

        item.addView(
                text(
                        "SYMBOL    " + ea.symbol +
                        "\nSIGNAL    " + ea.signal +
                        "\nBALANCE   " + ea.balance +
                        "\nEQUITY    " + ea.equity +
                        "\nFLOATING  " + ea.floating +
                        "\nTRADES    " + ea.trades,
                        13,
                        WHITE
                )
        );

        LinearLayout modeRow =
                new LinearLayout(this);

        modeRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        Button scalp =
                button("SCALP");

        Button swing =
                button("SWING");

        modeRow.addView(
                scalp,
                new LinearLayout.LayoutParams(
                        0,
                        55,
                        1
                )
        );

        modeRow.addView(
                swing,
                new LinearLayout.LayoutParams(
                        0,
                        55,
                        1
                )
        );

        item.addView(modeRow);

        LinearLayout controls =
                new LinearLayout(this);

        controls.setOrientation(
                LinearLayout.HORIZONTAL
        );

        Button start =
                button("START");

        Button stop =
                button("STOP");

        controls.addView(
                start,
                new LinearLayout.LayoutParams(
                        0,
                        55,
                        1
                )
        );

        controls.addView(
                stop,
                new LinearLayout.LayoutParams(
                        0,
                        55,
                        1
                )
        );

        item.addView(controls);

        scalp.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        ea.mode = "SCALP";

                        if (ea.id.equals(EA_ID)) {
                            sendMode("SCALP");
                        }

                        refreshEAList();
                    }
                }
        );

        swing.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        ea.mode = "SWING";

                        if (ea.id.equals(EA_ID)) {
                            sendMode("SWING");
                        }

                        refreshEAList();
                    }
                }
        );

        start.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        ea.running = true;

                        if (ea.id.equals(EA_ID)) {
                            sendCommand("START");
                        }

                        refreshEAList();
                    }
                }
        );

        stop.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        ea.running = false;

                        if (ea.id.equals(EA_ID)) {
                            sendCommand("STOP");
                        }

                        refreshEAList();
                    }
                }
        );

        eaContainer.addView(item);
    }

    // =========================
    // ADD EA DIALOG
    // =========================

    private void showAddEADialog() {

        final android.app.AlertDialog dialog =
                new android.app.AlertDialog.Builder(
                        this
                ).create();

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.setPadding(
                35,
                25,
                35,
                20
        );

        layout.addView(
                heading("ADD NEW EA")
        );

        final EditText nameInput =
                new EditText(this);

        nameInput.setHint("EA name");
        nameInput.setTextColor(WHITE);
        nameInput.setHintTextColor(MUTED);

        layout.addView(nameInput);

        Button add =
                button("ADD EA");

        layout.addView(add);

        add.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        String name =
                                nameInput
                                        .getText()
                                        .toString()
                                        .trim();

                        if (name.length() == 0) {

                            Toast.makeText(
                                    MainActivity.this,
                                    "Enter an EA name",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        eaList.add(
                                new EAItem(
                                        "local_" +
                                                System.currentTimeMillis(),
                                        name,
                                        "SCALP",
                                        false
                                )
                        );

                        refreshEAList();

                        dialog.dismiss();

                        Toast.makeText(
                                MainActivity.this,
                                name + " added",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );

        dialog.setView(layout);
        dialog.show();
    }
    }
