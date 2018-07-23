package com.borisenko.transaction.controller;

import com.borisenko.transaction.data.Transaction;
import com.borisenko.transaction.service.TransactionService;
import com.borisenko.transaction.service.TransactionService.TransactionProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.controllers.transactions}")
public class TransactionsController {
    private TransactionService transactionService;

    @Autowired
    public TransactionsController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity addTransaction(@RequestBody Transaction transaction) {
        TransactionProcessingResult register = transactionService.register(transaction);
        return new ResponseEntity(register == TransactionProcessingResult.OK ? HttpStatus.CREATED : HttpStatus.NO_CONTENT);
    }
}