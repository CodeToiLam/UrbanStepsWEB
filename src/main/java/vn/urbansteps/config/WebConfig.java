package vn.urbansteps.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve classpath static assets
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/")
                .setCachePeriod(0);

        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/")
                .setCachePeriod(0);

        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCachePeriod(0);

        registry.addResourceHandler("/image/**")
                .addResourceLocations("classpath:/static/image/")
                .setCachePeriod(0);

    // Serve uploaded files from both classpath static/uploads (if files were placed there)
    // and from filesystem uploads folder (app.upload.dir) so the app can find uploads saved
    // by different code paths (some controllers save under src/main/resources/static/uploads,
    // others write to <projectRoot>/uploads/).
    String locationFs = "file:" + System.getProperty("user.dir") + "/" + uploadDir + "/";
    String classpathUploads = "classpath:/static/uploads/";
    registry.addResourceHandler("/uploads/**")
        .addResourceLocations(classpathUploads, locationFs)
        .setCachePeriod(3600);
    }

    // i18n: use cookie-based resolver to allow manual change via ?lang and persist across sessions
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver cookieResolver = new CookieLocaleResolver();
        cookieResolver.setDefaultLocale(new Locale("vi"));
        cookieResolver.setCookieName("LANG");
        cookieResolver.setCookieMaxAge(60 * 60 * 24 * 30); // 30 days
        return cookieResolver;
    }

    // i18n: change locale via ?lang=en|vi
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
