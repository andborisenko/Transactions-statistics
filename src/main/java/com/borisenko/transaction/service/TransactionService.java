package com.borisenko.transaction.service;

import com.borisenko.transaction.data.Transaction;
import com.borisenko.transaction.data.TransactionsStatistics;

public interface TransactionService {
    void register(Transaction transaction);

    TransactionsStatistics getStatistics();
}
