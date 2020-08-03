package com.zeidler.base;

import org.aeonbits.owner.Config;

@Config.Sources({
        "classpath:${env}.properties"
})
public interface Environment extends Config {

    String url();
}
