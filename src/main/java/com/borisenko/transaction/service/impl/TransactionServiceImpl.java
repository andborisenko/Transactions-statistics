package com.borisenko.transaction.service.impl;

import com.borisenko.transaction.data.Transaction;
import com.borisenko.transaction.data.TransactionsStatistics;
import com.borisenko.transaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.DoubleSummaryStatistics;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final ConcurrentMap<Long, TransactionStatsHolder> statistics;
    private int timeSlotInMillis;
    private int numberOfStatsEntries;

    public TransactionServiceImpl(@Value("${transactions.timeslot.seconds}") int timeslotSeconds) {
        timeSlotInMillis = (int) TimeUnit.SECONDS.toMillis(timeslotSeconds);
        numberOfStatsEntries = timeSlotInMillis * 2;
        statistics = new ConcurrentHashMap<>(numberOfStatsEntries);
    }

    private boolean areInTimeSlot(long timestamp1, long timestamp2) {
        return Math.abs(timestamp1 - timestamp2) <= timeSlotInMillis;
    }

    @Override
    public void register(Transaction transaction) {
        long now = Instant.now().toEpochMilli();
        if (areInTimeSlot(now, transaction.getTimestamp())) {
            for (int i = 0; i < timeSlotInMillis; i++) {
                statistics.compute((transaction.getTimestamp() + i) % numberOfStatsEntries,
                        (idx, value) -> updateStatsHolder(value, transaction));
            }
        }
    }

    private TransactionStatsHolder updateStatsHolder(TransactionStatsHolder curStatsHolder, Transaction transaction) {
        TransactionStatsHolder result = curStatsHolder;
        if (curStatsHolder == null || !areInTimeSlot(curStatsHolder.timestamp, transaction.getTimestamp())) {
            result = new TransactionStatsHolder();
        }

        result.addTransaction(transaction);
        return result;
    }

    @Override
    public TransactionsStatistics getStatistics() {
        long nowMillis = Instant.now().toEpochMilli();
        TransactionStatsHolder statsHolder = statistics.get(nowMillis % numberOfStatsEntries);

        if (statsHolder == null || !areInTimeSlot(nowMillis, statsHolder.timestamp)) {
            return TransactionsStatistics.ZERO_STATISTICS;
        } else {
            return statsHolder.getTransactionsStatistics();
        }
    }

    private class TransactionStatsHolder {
        private final DoubleSummaryStatistics statistics = new DoubleSummaryStatistics();
        private long timestamp;

        @Override
        public String toString() {
            return "TransactionStatsHolder{" +
                    "transaction=" + statistics +
                    ", timestamp=" + timestamp +
                    '}';
        }

        private TransactionsStatistics getTransactionsStatistics() {
            return new TransactionsStatistics(
                    statistics.getSum(),
                    statistics.getAverage(),
                    statistics.getMax(),
                    statistics.getMin(),
                    statistics.getCount()
            );
        }

        private void addTransaction(Transaction transaction) {
            statistics.accept(transaction.getAmount());
            this.timestamp = Math.max(timestamp, transaction.getTimestamp());
        }
    }
}