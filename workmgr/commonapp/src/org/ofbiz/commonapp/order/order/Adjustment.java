package org.ofbiz.commonapp.order.order;

import org.ofbiz.core.util.UtilFormatOut;

public class Adjustment {
    private String description;
    private double amount;
    
    public Adjustment(String description, double amount) {
        this.description = description;
        this.amount = amount;
    }

    public Adjustment(String description, double percentage, double basePrice) {
        this(description, percentage * basePrice);
        prependDescription(UtilFormatOut.formatPercentage(percentage) + " ");
    }

    /** only and at least one of the amount or percentage must be specified */
    public Adjustment(String description, Double amount, Double percentage, double basePrice) {
        this.description = description;
        if ((amount != null) != (percentage != null)) {
            if (amount != null) {
                this.amount = amount.doubleValue();
            } else {
                this.amount = percentage.doubleValue() * basePrice;
                prependDescription(UtilFormatOut.formatPercentage(percentage) + " ");
            }
        } else {
            throw new IllegalArgumentException(
                    "Either amount or percentage must be specified for adjustment");
        }
    }

    /** include the percentage amount, if applicable */
    public String getDescription() {
        return description;
    }

    public void prependDescription(String prefix) {
        description = prefix + description;
    }
    
    /** uses either the amount or percentage */
    public double getAmount() {
        return amount;
    }
}