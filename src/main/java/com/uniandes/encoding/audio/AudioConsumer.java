package com.uniandes.encoding.audio;

import com.uniandes.encoding.audio.task.ProcessingFile;
import com.uniandes.entity.Audio;
import com.uniandes.entity.Option;
import com.uniandes.enums.AudioStatus;
import com.uniandes.enums.OptionNames;
import com.uniandes.mail.sender.Notificator;
import com.uniandes.repository.AudioRepository;
import com.uniandes.repository.OptionRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AudioConsumer implements Consumer {

    private final int MAXIMUM_ATTEMPS_ALLOWED = 3;

    private final AudioRepository audioRepository;

    private final Notificator notificator;

    private final OptionRepository optionRepository;

    /**
     * Path where the converted files will be stored
     */
    @Value("${path.converted.files}")
    private String pathConverted;

    private final ExecutorService executor;

    public AudioConsumer(AudioRepository audioRepository,
        Notificator notificator,
        OptionRepository optionRepository) {
        this.audioRepository = audioRepository;
        this.notificator = notificator;
        this.executor = Executors.newCachedThreadPool();
        this.optionRepository = optionRepository;
    }

    @Override
    public void audioConvert() {
        log.info("Begin logic {}", this.getClass().getName());
        LocalDateTime now = LocalDateTime.now();

        List<Audio> audiosInProgress = audioRepository.findAudiosByStatus(AudioStatus.IN_PROGRESS);
        if (CollectionUtils.isNotEmpty(audiosInProgress)) {
            log.info("Begin audio task " + now.toString());

            updateVoicesToConverting(audiosInProgress);

            List<ProcessingFile> voices = new ArrayList<>();

            for (Audio audio : audiosInProgress) {
                ProcessingFile process = new ProcessingFile(audio, pathConverted);
                voices.add(process);
            }
            try {
                long start = System.nanoTime();
                List<CompletableFuture<Audio>> futures = voices.stream()
                    .map(voice -> CompletableFuture.supplyAsync(voice::coding, executor))
                    .collect(Collectors.toList());

                futures.forEach(future -> future.whenCompleteAsync((processedAudio, exception) -> {
                    if (processedAudio.isProcessingSuccess()) {
                        updateAudioToConverted(processedAudio);
                        sendMail(processedAudio, AudioStatus.CONVERTED);
                    } else if (processedAudio.getProcessAttempts() >= MAXIMUM_ATTEMPS_ALLOWED) {
                        updateAudioToError(processedAudio);
                        sendMail(processedAudio, AudioStatus.ERROR);
                    } else {
                        updateOneMoreAttempt(processedAudio);
                    }
                }));

                long duration = (System.nanoTime() - start) / 1_000_000;
                log.info("Processed {} task(s) in {} millis", audiosInProgress.size(), duration);
            } catch (Exception e) {
                log.error("Error: {}", e.getMessage());
            }
        } else {
            log.info("There are no pending audios in progress");
        }
    }

    private void updateVoicesToConverting(List<Audio> pendingAudios) {
        log.info("updateVoicesToConverting --> pending audios: {}", pendingAudios.size());
        pendingAudios.forEach(audio -> {
            audio.setStatus(AudioStatus.CONVERTING);
            audioRepository.save(audio);
        });
        log.info("Finish updateVoicesToConverting");
    }

    private void updateAudioToConverted(Audio audio) {
        log.info("updateAudioToConverted: {}", audio.getId());
        audio.setLocationConvertedAudio(audio.getLocationConvertedAudio());
        audio.setStatus(AudioStatus.CONVERTED);
        audio.setConvertedName(audio.getLocationConvertedAudio()
            .substring(audio.getLocationConvertedAudio().lastIndexOf('/') + 1));
        audioRepository.save(audio);
        log.info("Finish updateAudioToConverted: {}", audio.getId());
    }

    private void updateAudioToError(Audio audio) {
        log.info("updateAudioToError: {}", audio.getId());
        audio.setStatus(AudioStatus.ERROR);
        audioRepository.save(audio);
        log.info("Finish updateAudioToError: {}", audio.getId());
    }

    private void updateOneMoreAttempt(Audio audio) {
        log.info("updateOneMoreAttempt: {}", audio.getId());
        audio.setStatus(AudioStatus.IN_PROGRESS);
        audio.setProcessAttempts(audio.getProcessAttempts() + 1);
        audioRepository.save(audio);
        log.info("Finish updateOneMoreAttempt: {}", audio.getId());
    }

    private void sendMail(Audio audio, AudioStatus status) {
        Option emailService = optionRepository.findByName(OptionNames.EMAIL_SERVICE);
        if (emailService.isActive()) {
            log.info("Sending mail ... {}", audio.getParticipant().getEmail());

            String subject = "Tu audio en \"" + audio.getContest().getName() + "\"";
            String body =
                "<h1>Hola " + audio.getParticipant().getFirstName() + " " + audio.getParticipant().getLastName()
                    + "</h1>";

            switch (status) {
                case CONVERTED:
                    subject += " ya fue procesado :)";
                    body += "<p>Tu audio ya fue procesado con éxito en el concurso \"" + audio.getContest().getName()
                        + "\" y está disponible en la página web <a href=\"" + audio.getContest().getUrl() + "\">"
                        + audio.getContest().getUrl() + "</a></p>";
                    break;

                case ERROR:
                    subject += " no pudo ser procesado :(";
                    body += "<p>Lo sentimos, pero no hemos podido procesar tu audio en el concurso \""
                        + audio.getContest().getName() + "\", por favor intentalo de nuevo en la página web <a href=\""
                        + audio.getContest().getUrl() + "\">" + audio.getContest().getUrl() + "</a></p>";
                    break;
            }

            body += "<h3>Saludos, el equipo de SUPER VOICES</h3>";
            notificator.sendNotification(audio.getParticipant().getEmail(), subject, body);
        } else {
            log.warn("Email service is inactive");
        }
    }
}
