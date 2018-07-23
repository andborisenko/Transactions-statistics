package com.borisenko.transaction.data;

import java.util.Objects;

public class TransactionsStatistics {
    public static final TransactionsStatistics ZERO_STATISTICS = new TransactionsStatistics(0, 0, 0, 0, 0);
    private final double sum;
    private final double avg;
    private final double max;
    private final double min;
    private final long count;

    public TransactionsStatistics(double sum, double avg, double max, double min, long count) {
        this.sum = sum;
        this.avg = avg;
        this.max = max;
        this.min = min;
        this.count = count;
    }

    public double getSum() {
        return sum;
    }

    public double getAvg() { return avg; }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public long getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "TransactionsStatistics{" +
                "sum=" + sum +
                ", avg=" + avg +
                ", max=" + max +
                ", min=" + min +
                ", count=" + count +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionsStatistics that = (TransactionsStatistics) o;
        return Double.compare(that.sum, sum) == 0 &&
                Double.compare(that.avg, avg) == 0 &&
                Double.compare(that.max, max) == 0 &&
                Double.compare(that.min, min) == 0 &&
                count == that.count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sum, avg, max, min, count);
    }
}
