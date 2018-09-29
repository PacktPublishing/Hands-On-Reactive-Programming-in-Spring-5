package com.example.service;

import com.example.controller.vm.UsersStatisticVM;
import com.example.service.gitter.dto.MessageResponse;
import reactor.core.publisher.Flux;

public interface StatisticService {

	Flux<UsersStatisticVM> updateStatistic(Flux<MessageResponse> messages);
}
