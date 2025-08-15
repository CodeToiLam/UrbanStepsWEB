package vn.urbansteps.config;

import org.apache.catalina.Manager;
import org.apache.catalina.session.StandardManager;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatSessionConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> factory.addContextCustomizers(context -> {
            Manager m = context.getManager();
            if (m instanceof StandardManager manager) {
                // Disable session persistence to disk to avoid EOFException on restart (devtools)
                manager.setPathname(null);
            }
        });
    }
}
