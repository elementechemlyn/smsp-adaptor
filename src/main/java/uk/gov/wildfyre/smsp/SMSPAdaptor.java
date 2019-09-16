package uk.gov.wildfyre.smsp;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import uk.gov.wildfyre.smsp.support.CorsFilter;
import uk.gov.wildfyre.smsp.support.SpineSecuritySocketFactory;

@SpringBootApplication
@EnableSwagger2
public class SMSPAdaptor {

    @Autowired
    ApplicationContext context;


    public static void main(String[] args) {
        System.setProperty("hawtio.authenticationEnabled", "false");
        System.setProperty("management.security.enabled", "false");
        System.setProperty("management.contextPath", "");

        SpringApplication.run(SMSPAdaptor.class, args);

    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new CustomRestfulServer(context), "/STU3/*");
        registration.setName("FhirServlet");
        registration.setLoadOnStartup(1);
        return registration;
    }

    @Bean
    CorsConfigurationSource
    corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    @Bean
    @Primary
    public FhirContext fhirContextBean() {
        return FhirContext.forDstu3();
    }


    @Bean
    public SpineSecuritySocketFactory spineSecurityContext()
    {
        try {
            return new SpineSecuritySocketFactory();
        } catch (Exception ex) {
            throw new InternalErrorException("Unable to create Spine Security Context");
        }
    }



    @Bean
    public FilterRegistrationBean corsFilter() {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter());
        bean.setOrder(0);
        return bean;
    }


}
