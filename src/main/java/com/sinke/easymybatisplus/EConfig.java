package com.sinke.easymybatisplus;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration //表示这个类为配置类
@EnableConfigurationProperties({EProperties.class})
public class EConfig {
}
