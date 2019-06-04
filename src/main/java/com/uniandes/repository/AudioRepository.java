package com.uniandes.repository;

import com.uniandes.entity.Audio;
import com.uniandes.enums.AudioStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudioRepository extends JpaRepository<Audio, Long> {

    List<Audio> findAudiosByStatus(AudioStatus status);
}
