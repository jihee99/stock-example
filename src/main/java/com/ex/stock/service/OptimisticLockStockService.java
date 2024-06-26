package com.ex.stock.service;

import com.ex.stock.domain.Stock;
import com.ex.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OptimisticLockStockService {

    private final StockRepository stockRepository;
    public OptimisticLockStockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }
    @Transactional
    public void decrease(Long id, Long quantity){
        Stock stock = stockRepository.findByIdWithOptimisticLock(id);

        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);
    }
}
