package activiti;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.impl.delegate.event.ActivitiEngineEvent;
import org.activiti.spring.boot.ActivitiEventMessageChannel;
import org.activiti.spring.integration.ActivitiInboundGateway;
import org.activiti.spring.integration.IntegrationActivityBehavior;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.SourcePollingChannelAdapterSpec;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.dsl.core.PollerFactory;
import org.springframework.integration.dsl.core.PollerSpec;
import org.springframework.integration.dsl.support.Function;
import org.springframework.integration.dsl.support.GenericHandler;
import org.springframework.integration.endpoint.SourcePollingChannelAdapter;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    IntegrationActivityBehavior activitiDelegate(ActivitiInboundGateway activitiInboundGateway) {
        return new IntegrationActivityBehavior(activitiInboundGateway);
    }

    @Bean
    ActivitiInboundGateway inboundGateway(ProcessEngine processEngine) {
        return new ActivitiInboundGateway(processEngine, "customerId", "projectId");
    }

    @Bean
    AnalysingService analysingService() {
        return new AnalysingService();
    }

    public static class AnalysingService {

        private final AtomicReference<String> stringAtomicReference
                = new AtomicReference<String>();

        public void dump(String projectId) {
            this.stringAtomicReference.set(projectId);
        }

        public AtomicReference<String> getStringAtomicReference() {
            return stringAtomicReference;
        }
    }

    @Bean
    IntegrationFlow inboundProcess(ActivitiInboundGateway inboundGateway) {
        return IntegrationFlows
                .from(inboundGateway)
                .handle(new GenericHandler<DelegateExecution>() {
                    @Override
                    public Object handle(DelegateExecution execution, Map<String, Object> headers) {
                        return MessageBuilder.withPayload(execution)
                                .setHeader("projectId", "3243549")
                                .copyHeaders(headers).build();
                    }
                })
                .get();
    }

    @Bean
    CommandLineRunner init(
            final AnalysingService analysingService,
            final RuntimeService runtimeService) {
        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {

                String integrationGatewayProcess = "integrationGatewayProcess";

                runtimeService.startProcessInstanceByKey(
                        integrationGatewayProcess, Collections.singletonMap("customerId", (Object) 232L));


                System.out.println("projectId=" + analysingService.getStringAtomicReference().get());

            }
        };
    } // ...

    
    @Bean
    @ActivitiEventMessageChannel
    public PublishSubscribeChannel activitiMessageChannel() {
      PublishSubscribeChannel channel = MessageChannels.publishSubscribe().get() ;
      return channel;
    }
    
    @Bean
    public IntegrationFlow integrationFlow() {
      return IntegrationFlows.from(activitiMessageChannel()).handle(ActivitiEvent.class, new GenericHandler<ActivitiEvent>() {
        @Override
        public Object handle(ActivitiEvent payload, Map<String, Object> headers) {
          System.out.println("From the integration flow " + payload);
          return null;
        }
      }).get();
    }

    
}

