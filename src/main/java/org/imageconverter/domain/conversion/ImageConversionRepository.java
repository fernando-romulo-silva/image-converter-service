package org.imageconverter.domain.conversion;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Image Conversion's repository
 * 
 * @author Fernando Romulo da Silva
 */
@Repository
public interface ImageConversionRepository extends JpaRepository<ImageConversion, Long>, JpaSpecificationExecutor<ImageConversion> {

//    @Query("select o from ImageConversion o where o.fileName = :fileName")
    /**
     * Find a image conversion by file name.
     * 
     * @param fileName The conversion file name
     * @return A {@link Optional} with {@link ImageConversion} or a empty {@link Optional}
     */
    Optional<ImageConversion> findByFileName(final String fileName);

}
