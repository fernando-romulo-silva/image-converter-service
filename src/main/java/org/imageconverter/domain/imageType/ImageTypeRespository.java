package org.imageconverter.domain.imageType;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageTypeRespository extends JpaRepository<ImageType, Long>, JpaSpecificationExecutor<ImageType> {

    @Query("select o from ImageType o where o.extension = :extension")
    Optional<ImageType> findByExtension(@Param("extension") final String extension);
}
