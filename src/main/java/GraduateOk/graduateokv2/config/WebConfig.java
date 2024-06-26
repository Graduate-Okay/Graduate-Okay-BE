package GraduateOk.graduateokv2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        WebMvcConfigurer.super.addCorsMappings(registry);
        registry.addMapping("/**")
                .allowedOrigins("https://graduate-okay.netlify.app/", "http://localhost:3000", "https://hs-graduate-ok.site/")
                .allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE")
                .maxAge(3000);
    }
}
