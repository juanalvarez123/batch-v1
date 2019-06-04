package com.uniandes.encoding.audio.task;

import com.uniandes.entity.Audio;
import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import ws.schild.jave.AudioAttributes;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderProgressListener;
import ws.schild.jave.EncodingAttributes;
import ws.schild.jave.MultimediaInfo;
import ws.schild.jave.MultimediaObject;

@Slf4j
public class ProcessingFile {

    private Audio audio;
    private final String pathConverted;

    public ProcessingFile(Audio audio, String pathConverted) {
        log.info("Path file: {}, ID: {}", audio.getLocationOriginalAudio(), audio.getId());
        this.audio = audio;
        this.pathConverted = pathConverted;
    }

    public Audio coding() {
        log.info("Begin transaction: {}", this.audio.getLocationOriginalAudio());
        File target;

        try {
            log.info("Encoding audio");
            File source = new File(audio.getLocationOriginalAudio());

            StringBuilder fileTarget = new StringBuilder(pathConverted);
            fileTarget.append(audio.getId());
            fileTarget.append("_");
            fileTarget.append((new Date()).getTime());
            fileTarget.append(".mp3");

            audio.setLocationConvertedAudio(getCorrectPath(fileTarget.toString()));
            target = new File(fileTarget.toString());
            if (target.exists()) {
                target.delete();
            }

            AudioAttributes audioAttributes = new AudioAttributes();
            audioAttributes.setCodec("libmp3lame");
            audioAttributes.setBitRate(128000);
            audioAttributes.setChannels(2);
            audioAttributes.setSamplingRate(44100);
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setFormat("mp3");
            attrs.setAudioAttributes(audioAttributes);
            Encoder encoder = new Encoder();
            ProcessingFile.PListener listener = new ProcessingFile.PListener();
            encoder.encode(new MultimediaObject(source), target, attrs, listener);

            audio.setProcessingSuccess(Boolean.TRUE);
            log.info("Encoding finish");
        } catch (Exception ex) {
            log.error("Error procesing file. Cause: {}", ex.getMessage());

            audio.setLocationConvertedAudio(null);
            audio.setProcessingSuccess(Boolean.FALSE);
        }

        return audio;
    }

    /**
     * To get the correct path in Windows
     */
    private String getCorrectPath(String path) {
        return path.replaceAll("\\\\", "/");
    }

    protected class PListener implements EncoderProgressListener {

        private MultimediaInfo _info = null;
        private final List<String> _messages = new LinkedList<>();
        private final List<Integer> _progress = new LinkedList<>();

        @Override
        public void sourceInfo(MultimediaInfo info) {
            _info = info;
        }

        @Override
        public void progress(int permil) {
            _progress.add(permil);
        }

        @Override
        public void message(String message) {
            _messages.add(message);
        }

        /**
         * @return the _info
         */
        public MultimediaInfo getInfo() {
            return _info;
        }

        /**
         * @return the _messages
         */
        public List<String> getMessages() {
            return _messages;
        }

        /**
         * @return the _progress
         */
        public List<Integer> getProgress() {
            return _progress;
        }
    }
}
