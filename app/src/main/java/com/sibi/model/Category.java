package com.sibi.model;

/**
 * Created by adway on 23/11/17.
 */

public class Category {
    private String name = "Uncategorized";
    private double allocationAmount = -1;
    private int colorCode = -1;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAllocationAmount() {
        return allocationAmount;
    }

    public void setAllocationAmount(double allocationAmount) {
        this.allocationAmount = allocationAmount;
    }

    @Override public int hashCode() {
        return name.hashCode();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        return name.equals(category.name);
    }

    public int getColorCode() {
        return colorCode;
    }

    public void setColorCode(int colorCode) {
        this.colorCode = colorCode;
    }
}
