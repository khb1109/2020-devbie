package underdogs.devbie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@Configuration
public class Beans {
	@Bean
	public HttpComponentsClientHttpRequestFactory factory() {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setConnectTimeout(5000);
		factory.setReadTimeout(5000);
		return factory;
	}
}
