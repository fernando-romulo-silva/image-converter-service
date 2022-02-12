package org.imageconverter.domain.convertion;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Image Convertion's repository
 * 
 * @author Fernando Romulo da Silva
 */
@Repository
public interface ImageConvertionRepository extends JpaRepository<ImageConvertion, Long>, JpaSpecificationExecutor<ImageConvertion> {

//    @Query("select o from ImageConvertion o where o.fileName = :fileName")
    /**
     * Find a image convertion by file name.
     * 
     * @param fileName The convertion file name
     * @return A {@link Optional} with {@link ImageConvertion} or a empty {@link Optional}
     */
    Optional<ImageConvertion> findByFileName(final String fileName);

}
