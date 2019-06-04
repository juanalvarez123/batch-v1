package com.uniandes.service;

import com.uniandes.encoding.audio.Consumer;
import com.uniandes.entity.Option;
import com.uniandes.enums.OptionNames;
import com.uniandes.repository.OptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AudioService {

    private final Consumer consumer;

    private final OptionRepository optionRepository;

    public AudioService(Consumer consumer, OptionRepository optionRepository) {
        this.consumer = consumer;
        this.optionRepository = optionRepository;
    }

    public void askingForPendingAudios() {
        Option batchProcess = optionRepository.findByName(OptionNames.BATCH_PROCESS);

        if (batchProcess.isActive()) {
            log.info("Begin process");
            consumer.audioConvert();
        } else {
            log.warn("Batch processing is inactive");
        }
    }
}
