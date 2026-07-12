package com.knowledgevault.storage.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "storage")
public class StorageConfiguration {

    private String location;

    private List<String> allowedContentTypes;

    private int maxFilesPerRequest;
}
