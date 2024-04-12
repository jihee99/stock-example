package com.ex.stock.service;

import com.ex.stock.domain.Stock;
import com.ex.stock.repository.StockRepository;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;


@SpringBootTest
public class StockServiceTest {

    @Autowired
    private StockService stockService;
    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void before(){
        stockRepository.saveAndFlush(new Stock(1L, 100L));
    }

    @AfterEach
    public void after(){
        stockRepository.deleteAll();
    }


    @Test
    public void 재고감소() {
        stockService.decrease(1L, 1L);

        Stock stock = stockRepository.findById(1L).orElseThrow();
        // 100 - 1 = 99

        // Optional에서 값을 가져와서 비교
//        Assert.assertEquals(Optional.of(99), Optional.of(stock.getQuantity()));
        Optional<Long> expected = Optional.of(99L);
        Optional<Long> actual = Optional.of(stock.getQuantity());

        // Optional 객체 내의 값을 비교
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void 동시에_100개의_요청() throws InterruptedException{
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        for(int i=0; i<threadCount; i++){
            executorService.submit(() -> {
                try {
                    stockService.decrease(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();

        Optional<Long> expected = Optional.of(0L);
        Optional<Long> actual = Optional.of(stock.getQuantity());

        // Optional 객체 내의 값을 비교
        Assert.assertEquals(expected, actual);

    }

}
