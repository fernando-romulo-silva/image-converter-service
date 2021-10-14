package org.imageconverter.domain.convertion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageConvertionRepository extends JpaRepository<ImageConvertion, Long> , JpaSpecificationExecutor<ImageConvertion> {

}
