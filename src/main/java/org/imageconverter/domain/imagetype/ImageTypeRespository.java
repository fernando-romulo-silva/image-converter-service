package org.imageconverter.domain.imagetype;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Image type's repository
 * 
 * @author Fernando Romulo da Silva
 */
@Repository
public interface ImageTypeRespository extends JpaRepository<ImageType, Long>, JpaSpecificationExecutor<ImageType>, PagingAndSortingRepository<ImageType, Long> {

    /**
     * Find a image type by extension
     * 
     * @param extension The conversion file name
     * @return A {@link Optional} with {@link ImageType} or a empty {@link Optional}
     */
    @Query("select o from ImageType o where o.extension = :extension")
    Optional<ImageType> findByExtension(@Param("extension") String extension);
}
