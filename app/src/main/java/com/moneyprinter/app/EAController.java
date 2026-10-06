package com.moneyprinter.app;

public class EAController {

    private final EAState state;

    public EAController(EAState state) {
        this.state = state;
    }

    public void connect() {
        state.setOnline(true);
    }

    public void disconnect() {
        state.setOnline(false);
        state.setRunning(false);
    }

    public void startEA() {
        if (state.isOnline()) {
            state.setRunning(true);
        }
    }

    public void stopEA() {
        state.setRunning(false);
    }

    public void setMode(String mode) {
        if (mode != null && (mode.equals("SCALP") || mode.equals("SWING"))) {
            state.setMode(mode);
        }
    }

    public void setRisk(double riskPercent) {
        if (riskPercent > 0) {
            state.setRiskPercent(riskPercent);
        }
    }

    public EAState getState() {
        return state;
    }
}
