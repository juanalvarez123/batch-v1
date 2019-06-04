package com.uniandes.store;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "file.store", havingValue = "AWS-S3")
public class StoreS3 implements StoreFiles {

    @Override
    public void storeByService(String path) {
        log.info("Clase en S3");
    }
}
