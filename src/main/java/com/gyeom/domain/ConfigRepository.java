package com.gyeom.domain;

public interface ConfigRepository {

    
    boolean write(AppConfiguration config);

    boolean read(AppConfiguration config);
}
