package com.dminer.enums;

public enum EventsTime {
    NO_REPEAT("No Repeat"),
    NO_REMINDER("No Reminder"), 
    DAILY("Daily"), WEEKLY("Weekly"), MONTHLY("Monthly"), YEARLY("Yearly"), FOREVER("Forever"),
    DAYS30("30 days"), DAYS15("15 days"), DAYS7("7 days"),
    MINUTES5("5 minutes"), MINUTES10("10 minutes"), MINUTES30("30 minutes"),
    HOURS1("1 hour"), HOURS2("2 hours"), HOURS3("3 hours"), HOURS4("4 hours"), HOURS5("5 hours"), HOURS6("6 hours"), 
    HOURS7("7 hours"), HOURS8("8 hours"), HOURS9("9 hours"), HOURS10("10 hours"), HOURS11("11 hours"), HOURS12("12 hours");

    private String eventTime;

    private EventsTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventTime() {
        return this.eventTime;
    }

}