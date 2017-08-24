package me.chanjar.annotation.overrideac;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoConfigurationEnableLogger {

  private static final Logger LOGGER = LoggerFactory.getLogger(AutoConfigurationEnableLogger.class);

  public AutoConfigurationEnableLogger() {
    LOGGER.info("Auto Configuration Enabled");
  }

}
