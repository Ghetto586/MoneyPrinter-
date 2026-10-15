// MODERN MONEYPRINTER MT5 CONTROL CENTER

private String selectedMode = "SCALP";
private String selectedSymbol = "NAS100";
private String selectedRisk = "1.0%";

private TextView dashboardEquity;
private TextView dashboardToday;
private TextView dashboardTrades;

private GradientDrawable modernBackground(int color, int radius, int stroke) {
    GradientDrawable drawable = new GradientDrawable();
    drawable.setColor(color);
    drawable.setCornerRadius(radius);
    if (stroke != 0) drawable.setStroke(2, stroke);
    return drawable;
}

private TextView modernText(String value, int size, int color, boolean bold) {
    TextView view = new TextView(this);
    view.setText(value);
    view.setTextSize(size);
    view.setTextColor(color);
    view.setPadding(0, 5, 0, 5);
    if (bold) view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
    return view;
}

private LinearLayout modernCard() {
    LinearLayout card = new LinearLayout(this);
    card.setOrientation(LinearLayout.VERTICAL);
    card.setPadding(15, 15, 15, 15);
    card.setBackground(modernBackground(CARD, 20, Color.rgb(39, 52, 69)));

    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
    );
    params.setMargins(0, 7, 0, 7);
    card.setLayoutParams(params);
    return card;
}

private Button modernButton(String label, int background, int foreground) {
    Button button = new Button(this);
    button.setText(label);
    button.setTextColor(foreground);
    button.setTextSize(13);
    button.setAllCaps(false);
    button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
    button.setBackground(modernBackground(background, 15, 0));
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
    brandLine.addView(modernText("MoneyPrinter ", 20, WHITE, true));
    brandLine.addView(modernText("EA", 20, GOLD, true));
    brand.addView(brandLine);
    brand.addView(modernText("MT5 CONTROL CENTER", 10, MUTED, true));

    header.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));

    TextView online = modernText("● ONLINE", 11, GREEN, true);
    online.setPadding(10, 8, 10, 8);
    online.setBackground(modernBackground(
            Color.rgb(8, 35, 29), 30, Color.rgb(0, 115, 75)));
    header.addView(online);

    root.addView(header);

    // HERO SECTION
    LinearLayout hero = new LinearLayout(this);
    hero.setOrientation(LinearLayout.VERTICAL);
    hero.setPadding(5, 27, 5, 20);

    hero.addView(modernText("AUTOMATED TRADING  ━━━━━", 11, GOLD, true));
    hero.addView(modernText("MT5 CONTROL", 31, WHITE, true));
    hero.addView(modernText("CENTER", 31, GOLD, true));
    hero.addView(modernText(
            "Manage your trading robot\nfrom anywhere.",
            16, MUTED, false));

    root.addView(hero);

    // ACCOUNT CONNECTION CARD
    LinearLayout account = modernCard();
    account.setOrientation(LinearLayout.HORIZONTAL);
    account.setGravity(Gravity.CENTER_VERTICAL);

    TextView mtIcon = modernText("⑤", 30, GREEN, true);
    mtIcon.setGravity(Gravity.CENTER);
    mtIcon.setBackground(modernBackground(INNER, 15, 0));

    LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(65, 65);
    iconParams.setMargins(0, 0, 13, 0);
    account.addView(mtIcon, iconParams);

    LinearLayout accountInfo = new LinearLayout(this);
    accountInfo.setOrientation(LinearLayout.VERTICAL);
    accountInfo.addView(modernText("MetaTrader 5", 17, WHITE, true));

    hostStatus = modernText("● WAITING FOR MT5", 12, GOLD, true);
    accountInfo.addView(hostStatus);
    accountInfo.addView(modernText(
            "Account details supplied by MT5 bridge",
            10, MUTED, false));

    account.addView(accountInfo);
    root.addView(account);

    // START AND STOP
    LinearLayout actions = modernRow();

    Button start = modernButton("▶  START EA", GREEN, Color.BLACK);
    Button stop = modernButton("■  STOP EA",
            Color.rgb(35, 15, 22), RED);
    stop.setBackground(modernBackground(
            Color.rgb(25, 12, 19), 15, RED));

    LinearLayout.LayoutParams actionParams =
            new LinearLayout.LayoutParams(0, 64, 1);
    actionParams.setMargins(0, 6, 5, 8);
    actions.addView(start, actionParams);

    LinearLayout.LayoutParams stopParams =
            new LinearLayout.LayoutParams(0, 64, 1);
    stopParams.setMargins(5, 6, 0, 8);
    actions.addView(stop, stopParams);

    root.addView(actions);

    start.setOnClickListener(v -> sendCommand("START"));
    stop.setOnClickListener(v -> sendCommand("STOP"));

    // EQUITY / DAILY P&L / OPEN TRADES
    LinearLayout stats = modernRow();

    dashboardEquity = addModernStat(
            stats, "◈", "EQUITY", "—", GOLD, false);
    dashboardToday = addModernStat(
            stats, "↗", "TODAY", "—", GREEN, true);
    dashboardTrades = addModernStat(
            stats, "☰", "OPEN TRADES", "—", MUTED, true);

    root.addView(stats);

    // ROBOT SETTINGS
    LinearLayout settings = modernCard();
    settings.addView(modernText("⚙  ROBOT SETTINGS", 16, GOLD, true));

    LinearLayout modes = modernRow();
    modes.setPadding(4, 4, 4, 4);
    modes.setBackground(modernBackground(INNER, 15, 0));

    Button scalp = modernButton(
            "ϟ  SCALP", GREEN, Color.BLACK);
    Button swing = modernButton(
            "▥  SWING", INNER, MUTED);

    modes.addView(scalp, new LinearLayout.LayoutParams(0, 49, 1));
    modes.addView(swing, new LinearLayout.LayoutParams(0, 49, 1));
    settings.addView(modes);

    scalp.setOnClickListener(v -> {
        selectedMode = "SCALP";
        sendMode("SCALP");
        Toast.makeText(this, "SCALP selected", Toast.LENGTH_SHORT).show();
        scalp.setBackground(modernBackground(GREEN, 12, 0));
        scalp.setTextColor(Color.BLACK);
        swing.setBackground(modernBackground(INNER, 12, 0));
        swing.setTextColor(MUTED);
    });

    swing.setOnClickListener(v -> {
        selectedMode = "SWING";
        sendMode("SWING");
        Toast.makeText(this, "SWING selected", Toast.LENGTH_SHORT).show();
        swing.setBackground(modernBackground(GREEN, 12, 0));
        swing.setTextColor(Color.BLACK);
        scalp.setBackground(modernBackground(INNER, 12, 0));
        scalp.setTextColor(MUTED);
    });

    settings.addView(modernText("⌖  Symbols", 14, WHITE, true));

    LinearLayout symbols = modernRow();
    String[] symbolNames = {"NAS100", "US30", "XAUUSD", "USDJPY"};

    for (String symbol : symbolNames) {
        Button symbolButton = modernButton(
                symbol,
                symbol.equals(selectedSymbol)
                        ? Color.rgb(8, 47, 36) : INNER,
                symbol.equals(selectedSymbol) ? GREEN : MUTED);

        symbolButton.setTextSize(10);

        LinearLayout.LayoutParams symbolParams =
                new LinearLayout.LayoutParams(0, 43, 1);
        symbolParams.setMargins(2, 3, 2, 3);
        symbols.addView(symbolButton, symbolParams);

        symbolButton.setOnClickListener(v -> {
            selectedSymbol = symbol;
            Toast.makeText(this,
                    symbol + " selected; MT5 must support symbol changes",
                    Toast.LENGTH_SHORT).show();

            // This is a command request, not confirmation of execution.
            sendSetting("SYMBOL", symbol);
        });
    }

    settings.addView(symbols);

    LinearLayout riskRow = modernRow();
    riskRow.addView(modernText(
            "⬡  Risk per trade", 14, WHITE, true),
            new LinearLayout.LayoutParams(0, -2, 1));

    Button risk = modernButton(
            selectedRisk + "  ▾", INNER, WHITE);
    riskRow.addView(risk, new LinearLayout.LayoutParams(115, 47));
    settings.addView(riskRow);

    risk.setOnClickListener(v -> {
        String[] risks = {"0.25%", "0.5%", "1.0%", "1.5%", "2.0%"};

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
    manager.addView(modernText("EA MANAGER", 16, WHITE, true));

    totalEA = modernText("TOTAL EAs: 1", 12, MUTED, false);
    runningEA = modernText("RUNNING: 0", 12, GREEN, false);
    manager.addView(totalEA);
    manager.addView(runningEA);

    eaContainer = new LinearLayout(this);
    eaContainer.setOrientation(LinearLayout.VERTICAL);
    manager.addView(eaContainer);

    Button addEA = modernButton("+ ADD EA", INNER, GOLD);
    manager.addView(addEA, new LinearLayout.LayoutParams(-1, 52));
    addEA.setOnClickListener(v -> showAddEADialog());

    Button stopAll = modernButton(
            "STOP ALL EAs", Color.rgb(35, 15, 22), RED);
    manager.addView(stopAll, new LinearLayout.LayoutParams(-1, 52));
    stopAll.setOnClickListener(v -> sendCommand("STOP"));

    root.addView(manager);

    // KEEP THE EXISTING AI SCANNER
    createAIChartScanner(root);

    setContentView(scroll);
    refreshEAList();
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
    stat.addView(modernText(title, 10, MUTED, false));

    TextView value = modernText(initial, 16, accent, true);
    stat.addView(value);

    LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(0, -2, 1);
    params.setMargins(margin ? 5 : 0, 0, 0, 0);
    row.addView(stat, params);

    return value;
        }
