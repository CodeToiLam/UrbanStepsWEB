package vn.urbansteps.config;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HttpSessionConfig implements HttpSessionListener {

    private static final Logger logger = LoggerFactory.getLogger(HttpSessionConfig.class);
    
    @Override
    public void sessionCreated(HttpSessionEvent event) {
        logger.info("Session created: {}", event.getSession().getId());
        // Ensure session ID is stable
        event.getSession().setAttribute("session_stabilizer", "active");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        logger.info("Session destroyed: {}", event.getSession().getId());
    }
}
