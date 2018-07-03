package com.sibi.model;

/**
 * Created by adway on 23/11/17.
 */

public class Wish {
    private String name;
    private long targetTime;
    private long addedTime;
    private double expectedAmount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(long targetTime) {
        this.targetTime = targetTime;
    }

    public long getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(long addedTime) {
        this.addedTime = addedTime;
    }

    public double getExpectedAmount() {
        return expectedAmount;
    }

    public void setExpectedAmount(long expectedAmount) {
        this.expectedAmount = expectedAmount;
    }

    @Override public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (int) (targetTime ^ (targetTime >>> 32));
        result = 31 * result + (int) (addedTime ^ (addedTime >>> 32));
        return result;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Wish wish = (Wish) o;

        return targetTime == wish.targetTime
            && addedTime == wish.addedTime
            && name.equals(wish.name);
    }
}
