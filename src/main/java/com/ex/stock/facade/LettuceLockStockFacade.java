package com.ex.stock.facade;

import org.springframework.stereotype.Component;

import com.ex.stock.repository.RedisLockRepository;
import com.ex.stock.service.StockService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LettuceLockStockFacade {
	private final RedisLockRepository redisLockRepository;
	private final StockService stockService;

	public void decrease(Long key, Long quantity) throws InterruptedException {
		while (!redisLockRepository.lock(key)) {
			Thread.sleep(100);
		}

		try {
			stockService.decrease(key, quantity);
		} finally {
			redisLockRepository.unlock(key);
		}
	}
}
