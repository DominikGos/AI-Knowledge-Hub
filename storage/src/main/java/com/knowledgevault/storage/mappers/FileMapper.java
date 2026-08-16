package com.knowledgevault.storage.mappers;

import com.knowledgevault.storage.dto.UploadedFileResponse;
import com.knowledgevault.storage.entities.StoredFile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMapper {
    UploadedFileResponse toEntity(StoredFile entity);

    StoredFile toDto(UploadedFileResponse dto);
}
