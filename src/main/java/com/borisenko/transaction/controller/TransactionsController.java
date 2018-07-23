package com.borisenko.transaction.controller;

import com.borisenko.transaction.data.Transaction;
import com.borisenko.transaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.controllers.transactions}")
public class TransactionsController {
    private TransactionService transactionService;

    @Autowired
    public TransactionsController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addTransaction(@RequestBody Transaction transaction) {
        transactionService.register(transaction);
    }
}