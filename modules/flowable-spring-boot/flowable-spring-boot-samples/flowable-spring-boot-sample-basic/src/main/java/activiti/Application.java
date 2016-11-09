package activiti;

import java.util.Collections;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.delegate.event.ActivitiEngineEvent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

/**
 *
 */
@SpringBootApplication
public class Application {

    //  establish that it's detected the processes and that we can deploy one.
    @Bean
    CommandLineRunner basics(final RuntimeService runtimeService) {
        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                runtimeService.startProcessInstanceByKey("waiter", Collections.singletonMap("customerId", (Object) 243L));
            }
        };
    }

    public static void main(String args[]) {
        SpringApplication.run(Application.class, args);
    }
    
    @EventListener(ActivitiEngineEvent.class)
    public void handleEvent(ActivitiEngineEvent engineEvent) {
      System.out.println(engineEvent);
    }

}
