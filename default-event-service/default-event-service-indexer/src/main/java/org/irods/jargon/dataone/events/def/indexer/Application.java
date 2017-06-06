/**
 * 
 */
package org.irods.jargon.dataone.events.def.indexer;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.integration.amqp.inbound.AmqpInboundChannelAdapter;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

/**
 * Master flow core
 * 
 * @author mcc
 *
 */

@SpringBootApplication
@EnableIntegration
@IntegrationComponentScan
@ImportResource({ "/event-dao-hibernate-spring.cfg.xml", "/event-service-beans.xml" })
public class Application {

	/**
	 * 
	 */
	public Application() {
	}

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext ctx = new SpringApplication(Application.class).run(args);
		System.out.println("Hit Enter to terminate");
		System.in.read();
		ctx.close();
	}

	@Bean
	public MessageChannel amqpInputChannel() {
		return new DirectChannel();
	}

	@Bean
	public AmqpInboundChannelAdapter inbound(SimpleMessageListenerContainer listenerContainer,
			@Qualifier("amqpInputChannel") MessageChannel channel) {
		AmqpInboundChannelAdapter adapter = new AmqpInboundChannelAdapter(listenerContainer);
		adapter.setOutputChannel(channel);
		return adapter;
	}

	@Bean
	public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
		container.setQueueNames("irods.dataone.logger");
		container.setConcurrentConsumers(2);
		// ...
		return container;
	}

	@Bean
	@ServiceActivator(inputChannel = "amqpInputChannel")
	public MessageHandler handler() {
		return new MessageHandler() {

			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				System.out.println(message.getPayload());
			}

		};
	}

}
