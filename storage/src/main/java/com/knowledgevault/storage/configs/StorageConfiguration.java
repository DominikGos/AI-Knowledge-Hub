package com.knowledgevault.storage.configs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "storage")
public class StorageConfiguration {

    @NotBlank
    private String location;

    @NotEmpty
    private List<String> allowedContentTypes;

    @Min(1)
    private int maxFilesPerRequest;
}
