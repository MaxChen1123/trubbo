package com.maxchen.trubbo.spring;

import com.maxchen.trubbo.cluster.ClusterProtocol;
import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.common.configuration.ConfigurationContext;
import com.maxchen.trubbo.rpc.protocol.api.Invoker;
import com.maxchen.trubbo.rpc.proxy.JdkProxyFactory;
import com.maxchen.trubbo.spring.annotation.TrubboReference;
import com.maxchen.trubbo.spring.annotation.TrubboService;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.Set;

@Slf4j
public class TrubboInitializer implements ApplicationListener<ContextRefreshedEvent>
        , ApplicationContextInitializer<ConfigurableApplicationContext>
        , BeanFactoryPostProcessor {
    private static ClusterProtocol clusterProtocol;
    private static volatile String basePackage;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(TrubboService.class));

        for (var beanDefinition : scanner.findCandidateComponents(basePackage)) {
            try {
                String className = beanDefinition.getBeanClassName();
                Class<?> clazz = Class.forName(className);

                TrubboService annotation = clazz.getAnnotation(TrubboService.class);
                Class<?> interfaceClass = annotation.value();
                String serviceName = interfaceClass.getName();

                String url = (String) ConfigurationContext.SPRING_CONFIGURATION_MAP.getOrDefault("trubbo.url", "127.0.0.1:8080");
                try {
                    clusterProtocol.export(new URL("Provider://" + url + "?service=" + serviceName + "&impl=" + className));
                    log.info("Exporting service: {}", serviceName);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String getSpringApplicationPackage() {
        Reflections reflections = new Reflections("");
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(SpringBootApplication.class);
        if (annotatedClasses.isEmpty()) {
            throw new RuntimeException("No class annotated with @SpringBootApplication found.");
        }
        Class<?> startupClazz = annotatedClasses.iterator().next();
        return startupClazz.getPackage().getName();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        printTrubbo();
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(new ClassPathResource("application.yml"));
        Properties properties = factoryBean.getObject();
        if (properties != null) {
            ConfigurationContext.SPRING_CONFIGURATION_MAP.putAll(properties);
        }
        try {
            if (!ConfigurationContext.SPRING_CONFIGURATION_MAP.containsKey("trubbo.zookeeper.address")) {
                throw new RuntimeException("trubbo.zookeeper.address is not set");
            }
            String zookeeperAddress = (String) ConfigurationContext.SPRING_CONFIGURATION_MAP.get("trubbo.zookeeper.address");
            clusterProtocol = new ClusterProtocol(new URL("Zookeeper://"
                    + zookeeperAddress));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        basePackage = getSpringApplicationPackage();
        log.info("Trubbo initialization finished");
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        Class<?> clazz = null;
        for (String beanDefinitionName : beanDefinitionNames) {
            if (beanFactory.isFactoryBean(beanDefinitionName)) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
                String beanClassName = beanDefinition.getBeanClassName();
                try {
                    clazz = Class.forName(beanClassName);
                } catch (Exception ignored) {
                }
            } else {
                clazz = beanFactory.getType(beanDefinitionName);

            }
            if (clazz == null) {
                continue;
            }
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(TrubboReference.class)) {
                    log.info("TrubboReference found in {}", clazz.getName());
                    Invoker refer = clusterProtocol.refer(field.getType().getName());
                    Object proxy = JdkProxyFactory.getProxy(field.getType(), refer);
                    BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
                    beanDefinition.getPropertyValues().addPropertyValue(field.getName(), proxy);
                }
            }

        }
    }

    private void printTrubbo() {
        System.out.println(".___________..______       __    __  .______   .______     ______   \n" +
                "|           ||   _  \\     |  |  |  | |   _  \\  |   _  \\   /  __  \\  \n" +
                "`---|  |----`|  |_)  |    |  |  |  | |  |_)  | |  |_)  | |  |  |  | \n" +
                "    |  |     |      /     |  |  |  | |   _  <  |   _  <  |  |  |  | \n" +
                "    |  |     |  |\\  \\----.|  `--'  | |  |_)  | |  |_)  | |  `--'  | \n" +
                "    |__|     | _| `._____| \\______/  |______/  |______/   \\______/  \n" +
                "                                                                    ");
    }
}
