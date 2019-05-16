package com.mhd.superwifidirect.Bean;

public class EventMessage_MAtoFragmentGroupMap {

    private boolean isRefresh;

    public EventMessage_MAtoFragmentGroupMap(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }
}
