package io.github.alexcheng1982.springai.dashscope.autoconfigure;

import io.github.alexcheng1982.springai.dashscope.DashscopeChatClient;
import io.github.alexcheng1982.springai.dashscope.api.DashscopeApi;
import org.springframework.ai.model.function.FunctionCallbackContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@ConditionalOnClass(DashscopeApi.class)
@EnableConfigurationProperties({DashscopeChatProperties.class})
public class DashscopeAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public DashscopeApi dashscopeApi() {
    return new DashscopeApi();
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(prefix = DashscopeChatProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true",
      matchIfMissing = true)
  public DashscopeChatClient dashscopeChatClient(DashscopeApi dashscopeApi,
      DashscopeChatProperties properties,
      FunctionCallbackContext functionCallbackContext) {
    return new DashscopeChatClient(dashscopeApi, properties.getOptions(),
        functionCallbackContext);
  }

  @Bean
  @ConditionalOnMissingBean
  public FunctionCallbackContext springAiFunctionManager(
      ApplicationContext context) {
    FunctionCallbackContext manager = new FunctionCallbackContext();
    manager.setApplicationContext(context);
    return manager;
  }
}