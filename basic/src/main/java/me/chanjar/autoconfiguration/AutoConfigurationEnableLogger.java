package me.chanjar.autoconfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * Created by qianjia on 2017/7/6.
 */
@Configuration
public class AutoConfigurationEnableLogger {

  private static final Logger LOGGER = LoggerFactory.getLogger(AutoConfigurationEnableLogger.class);

  public AutoConfigurationEnableLogger() {
    LOGGER.info("Auto Configuration Enabled");
  }
  
}
