package com.knowledgevault.storage.mappers;

import com.knowledgevault.storage.dto.UploadedFileResponse;
import com.knowledgevault.storage.entities.StoredFile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMapper {
    StoredFile toEntity(UploadedFileResponse dto);

    UploadedFileResponse toDto(StoredFile entity);
}
