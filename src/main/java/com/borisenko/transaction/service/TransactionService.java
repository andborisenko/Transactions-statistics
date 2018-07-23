package com.borisenko.transaction.service;

import com.borisenko.transaction.data.Transaction;
import com.borisenko.transaction.data.TransactionsStatistics;

public interface TransactionService {
    TransactionProcessingResult register(Transaction transaction);

    TransactionsStatistics getStatistics();

    enum TransactionProcessingResult {OK, TRANSACTION_IS_TOO_OLD;}
}