package com.maxchen.trubbo.spring.provider;

import com.maxchen.trubbo.cluster.ClusterProtocol;
import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.common.configuration.ConfigurationContext;
import com.maxchen.trubbo.spring.provider.annotation.TrubboService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.net.URISyntaxException;
import java.util.Properties;

@Slf4j
public class TrubboInitializer implements ApplicationListener<ContextRefreshedEvent>
        , ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static ClusterProtocol clusterProtocol;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

        scanner.addIncludeFilter(new AnnotationTypeFilter(TrubboService.class));

        //TODO
//        // 扫描 com 包下的所有类
//        String basePackage = ClassUtils.getPackageName();
        for (var beanDefinition : scanner.findCandidateComponents("com")) {
            try {
                // 获取类的全名
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

    //TODO to be finished
    private String getSpringApplication() {
        return "";
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
        log.info("Trubbo initialization finished");
    }

    private void printTrubbo() {
        System.out.println(".___________..______       __    __  .______   .______     ______   \n" +
                "|           ||   _  \\     |  |  |  | |   _  \\  |   _  \\   /  __  \\  \n" +
                "`---|  |----`|  |_)  |    |  |  |  | |  |_)  | |  |_)  | |  |  |  | \n" +
                "    |  |     |      /     |  |  |  | |   _  <  |   _  <  |  |  |  | \n" +
                "    |  |     |  |\\  \\----.|  `--'  | |  |_)  | |  |_)  | |  `--'  | \n" +
                "    |__|     | _| `._____| \\______/  |______/  |______/   \\______/  \n" +
                "                                                                    \n");
    }

}
