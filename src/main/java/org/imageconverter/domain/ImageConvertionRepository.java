package org.imageconverter.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageConvertionRepository extends JpaRepository<ImageConvertion, Long> {

}
