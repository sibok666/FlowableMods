package org.activiti.spring.boot;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;

public class MessagePublishingActivitiEventListener implements ActivitiEventListener {
  
  @Override
  public void onEvent(ActivitiEvent event) {
    
  }
  
  @Override
  public boolean isFailOnException() {
    return false;
  }

}
