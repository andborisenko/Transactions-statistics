package com.borisenko.transaction.controller;

import com.borisenko.transaction.data.TransactionsStatistics;
import com.borisenko.transaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.controllers.statistics}")
public class StatisticsController {

    private TransactionService transactionService;

    @Autowired
    public StatisticsController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping(produces = "application/json")
    public TransactionsStatistics getTransactionStatistics() {
        return transactionService.getStatistics();
    }
}