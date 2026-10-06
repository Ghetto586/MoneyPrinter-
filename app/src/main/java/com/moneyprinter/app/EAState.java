package com.moneyprinter.app;

public class EAState {

    private boolean online = false;
    private boolean running = false;
    private String mode = "SCALP";
    private double riskPercent = 1.0;
    private double balance = 0.0;
    private double equity = 0.0;
    private double dailyProfitLoss = 0.0;
    private int openTrades = 0;

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public double getRiskPercent() {
        return riskPercent;
    }

    public void setRiskPercent(double riskPercent) {
        this.riskPercent = riskPercent;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getEquity() {
        return equity;
    }

    public void setEquity(double equity) {
        this.equity = equity;
    }

    public double getDailyProfitLoss() {
        return dailyProfitLoss;
    }

    public void setDailyProfitLoss(double dailyProfitLoss) {
        this.dailyProfitLoss = dailyProfitLoss;
    }

    public int getOpenTrades() {
        return openTrades;
    }

    public void setOpenTrades(int openTrades) {
        this.openTrades = openTrades;
    }
}
