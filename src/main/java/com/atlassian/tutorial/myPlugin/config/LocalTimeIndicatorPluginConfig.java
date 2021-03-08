package com.atlassian.tutorial.myPlugin.config;

import static com.atlassian.plugins.osgi.javaconfig.OsgiServices.exportOsgiService;

import com.atlassian.plugins.osgi.javaconfig.configs.beans.ModuleFactoryBean;
import com.atlassian.plugins.osgi.javaconfig.configs.beans.PluginAccessorBean;
import com.atlassian.tutorial.myPlugin.api.LocalTimeIndicatorPlugin;
import com.atlassian.tutorial.myPlugin.impl.LocalTimeIndicatorPluginImpl;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        ModuleFactoryBean.class,
        PluginAccessorBean.class
})
public class LocalTimeIndicatorPluginConfig {
    @Bean
    public LocalTimeIndicatorPlugin localTimeComponent() {
        return new LocalTimeIndicatorPluginImpl();
    }

    @Bean
    public FactoryBean<ServiceRegistration> registerMyDelegatingService(
            final LocalTimeIndicatorPlugin localTimeIndicatorPlugin) {
        return exportOsgiService(localTimeIndicatorPlugin, null, LocalTimeIndicatorPlugin.class);
    }
}
