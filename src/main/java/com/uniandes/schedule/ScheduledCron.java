package com.uniandes.schedule;

import com.uniandes.service.AudioService;
import java.io.IOException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledCron {

    private final AudioService audioService;

    public ScheduledCron(AudioService audioService) {
        this.audioService = audioService;
    }

    @Scheduled(cron = "${cron.expression}")
    public void askingForPendingAudios() throws IOException {
        audioService.askingForPendingAudios();
    }
}
