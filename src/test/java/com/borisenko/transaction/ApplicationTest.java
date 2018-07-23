package com.borisenko.transaction;

import com.borisenko.transaction.data.Transaction;
import com.borisenko.transaction.data.TransactionsStatistics;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static com.borisenko.transaction.data.TransactionsStatistics.ZERO_STATISTICS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class ApplicationTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @Value("${transactions.timeslot.seconds}")
    private int transactionsTimeSlotSeconds;

    @Value("${api.controllers.transactions}")
    private String transactionsControllerPath;

    @Value("${api.controllers.statistics}")
    private String statisticsControllerPath;

    private MockHttpServletResponse getStatisticsResponse() throws Exception {
        return mvc.perform(get(statisticsControllerPath))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
    }

    private ResultActions postTransactionRequest(Transaction t1) throws Exception {
        return mvc.perform(post(transactionsControllerPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(t1)));

    }

    @Test
    public void testNoTransactionsStatistics() throws Exception {
        MockHttpServletResponse response = getStatisticsResponse();
        assertThat(response.getContentAsString())
                .isEqualTo(objectMapper.writeValueAsString(ZERO_STATISTICS));
    }

    @Test
    @DirtiesContext
    public void testAddOneTransaction() throws Exception {
        double amount = 239.0;
        postTransactionRequest(new Transaction(amount, Instant.now().toEpochMilli())).andExpect(status().isCreated());

        MockHttpServletResponse response = getStatisticsResponse();

        TransactionsStatistics expectedStatistics = new TransactionsStatistics(amount, amount, amount, amount, 1);
        assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(expectedStatistics));
    }

    @Test
    @DirtiesContext
    public void testAddTwoSequentialTransactions() throws Exception {
        double amount1 = 100.0;
        double amount2 = 300.0;
        long timestamp1 = Instant.now().toEpochMilli();
        long timestamp2 = timestamp1 + 1;

        postTransactionRequest(new Transaction(amount1, timestamp1)).andExpect(status().isCreated());
        postTransactionRequest(new Transaction(amount2, timestamp2)).andExpect(status().isCreated());

        MockHttpServletResponse response = getStatisticsResponse();

        assertThat(response.getContentAsString())
                .isEqualTo(objectMapper.writeValueAsString(
                        new TransactionsStatistics(
                                amount1 + amount2,
                                (amount1 + amount2) / 2,
                                Math.max(amount1, amount2),
                                Math.min(amount1, amount2),
                                2)
                ));
    }

    @Test
    @DirtiesContext
    public void testOutOfTimeSlotTransactions() throws Exception {
        long now = Instant.now().toEpochMilli();
        double amount = 10.0;
        long timeSlotMillis = TimeUnit.SECONDS.toMillis(transactionsTimeSlotSeconds);
        for (int i = 0; i < 100; i++) {
            Transaction transaction = new Transaction(amount, now - timeSlotMillis - i);
            postTransactionRequest(transaction).andExpect(status().isNoContent());
        }

        assertEquals(objectMapper.writeValueAsString(ZERO_STATISTICS), getStatisticsResponse().getContentAsString());
    }

    @Test
    @DirtiesContext
    public void testAllTransactionsFitTimeSlot() throws Exception {
        long now = Instant.now().toEpochMilli();
        double amount = 10.0;
        long timeSlotMillis = TimeUnit.SECONDS.toMillis(transactionsTimeSlotSeconds);
        long numberOfTransactions = timeSlotMillis / 1000;

        for (int i = 0; i < numberOfTransactions; i++) {
            postTransactionRequest(new Transaction(amount, now - i * 100)).andExpect(status().isCreated());
        }

        TransactionsStatistics expectedStatistics = new TransactionsStatistics(
                numberOfTransactions * amount,
                amount,
                amount,
                amount,
                numberOfTransactions
        );
        assertEquals(objectMapper.writeValueAsString(expectedStatistics), getStatisticsResponse().getContentAsString());
    }

    @Test
    @DirtiesContext
    public void testAllTransactionsSameTimestamp() throws Exception {
        long now = Instant.now().toEpochMilli();
        int numberOfTransactions = 1000;

        for (int i = 0; i < numberOfTransactions; i++) {
            postTransactionRequest(new Transaction(i, now)).andExpect(status().isCreated());
            ;
        }

        TransactionsStatistics expectedStatistics = new TransactionsStatistics(
                numberOfTransactions * (numberOfTransactions - 1) / 2.0,
                (numberOfTransactions - 1) / 2.0,
                numberOfTransactions - 1,
                0,
                numberOfTransactions
        );
        assertEquals(objectMapper.writeValueAsString(expectedStatistics), getStatisticsResponse().getContentAsString());
    }
}