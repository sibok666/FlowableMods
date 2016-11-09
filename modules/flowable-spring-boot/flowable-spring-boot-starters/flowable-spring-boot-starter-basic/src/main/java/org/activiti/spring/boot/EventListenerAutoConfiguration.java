package org.activiti.spring.boot;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

@Configuration
public class EventListenerAutoConfiguration {
  
  @Bean
  @ConditionalOnProperty(name = "spring.activiti.events.context.enabled", havingValue = "true", matchIfMissing = false)
  public ActivitiEventApplicationEventPublisher activitiEventApplicationEventPublisher() {
    return new ActivitiEventApplicationEventPublisher();
  }

  @Configuration
  @ConditionalOnClass(MessageChannel.class)
  public static class MessageChannelConfiguration {
    
    @Autowired(required = false)
    @ActivitiEventMessageChannel
    private MessageChannel messageChannel;
    
    @Bean
    @ConditionalOnProperty(name = "spring.activiti.events.channel.enabled", havingValue = "true", matchIfMissing = false)
    public ActivitiEventListener messageChannelPublishingActivitiEventListener() {
      return new ActivitiEventListener() {
        
        @Override
        public void onEvent(ActivitiEvent event) {
          if (messageChannel != null) {
            messageChannel.send(MessageBuilder.withPayload(event).build());
          }
        }
        
        @Override
        public boolean isFailOnException() {
          return false;
        }
      };
    }
    
  }
  
 

}
