package com.tourismdata.contest.external.kakaologin;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KakaoLoginProperties.class)
public class KakaoLoginConfig {
}
