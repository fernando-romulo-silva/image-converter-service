package org.imageconverter.domain.conversion;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Image Conversion's repository
 * 
 * @author Fernando Romulo da Silva
 */
public interface ImageConversionRepository extends JpaRepository<ImageConversion, Long>, JpaSpecificationExecutor<ImageConversion>, PagingAndSortingRepository<ImageConversion, Long> {

//    @Query("select o from ImageConversion o where o.fileName = :fileName")
    /**
     * Find a image conversion by file name.
     * 
     * @param fileName The conversion file name
     * @return A {@link Optional} with {@link ImageConversion} or a empty {@link Optional}
     */
    Optional<ImageConversion> findByFileName(String fileName);
    
    // Not supported yet
    // Slice<ImageConversion> findAllSlice(Pageable page, Specification<ImageConversion> spec);

}
