package com.uniandes.repository;

import com.uniandes.entity.Option;
import com.uniandes.enums.OptionNames;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {

    Option findByName(OptionNames name);
}
