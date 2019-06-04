package com.uniandes.store;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "file.store", havingValue = "file-system")
public class StoreFileSystem implements StoreFiles {

    @Override
    public void storeByService(String path) {
        log.info("Path from file {}", path);
    }
}
