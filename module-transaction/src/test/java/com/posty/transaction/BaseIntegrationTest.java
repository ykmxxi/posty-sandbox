package com.posty.transaction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
public abstract class BaseIntegrationTest {

    @Autowired
    protected TransactionTemplate transactionTemplate;

    @Autowired
    protected DataSource dataSource;

    protected ExecutorService executor = Executors.newFixedThreadPool(4);
}
