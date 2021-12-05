package org.imageconverter.domain.convertion;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageConvertionRepository extends JpaRepository<ImageConvertion, Long> , JpaSpecificationExecutor<ImageConvertion> {

//    @Query("select o from ImageConvertion o where o.fileName = :fileName")
    Optional<ImageConvertion> findByFileName(final String fileName);
    
}
