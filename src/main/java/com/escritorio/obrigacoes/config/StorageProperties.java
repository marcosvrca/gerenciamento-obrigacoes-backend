package com.escritorio.obrigacoes.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage")
public class StorageProperties {

    private String path = "./uploads";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
