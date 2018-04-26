package io.spring.initializr.metadata;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ModulePropertiesResolver {
    private static final String PROP_TEMPLATE_ROOT_PATH = "/properties/";
    private static final String SHARED_TEMPLATE_PATH = PROP_TEMPLATE_ROOT_PATH + "shared/";

    private static final String CONFIG_SERVER_MODULE = "cloud-config-server";
    private static final String GATEWAY_SERVER_MODULE = "cloud-gateway";

    private static final String[] BASIC_CLOUD_MODULES = {"cloud-config-server", "cloud-eureka-server", "cloud-gateway"};

    private static Map<String, String> bootstrapPropTemplateMap = new HashMap<>();
    private static Map<String, String> sharedPropTemplateMap = new HashMap<>();
    static {
        bootstrapPropTemplateMap.put(CONFIG_SERVER_MODULE, "bootstrap-config-server.yml");

        sharedPropTemplateMap.put("cloud-eureka-server", "discovery.yml");
        sharedPropTemplateMap.put("cloud-gateway", "gateway.yml");
    }

    public static String getBootstrapTemplate(String module) {
        String templateFile = bootstrapPropTemplateMap.get(module);
        if(StringUtils.isEmpty(templateFile)) {
            templateFile = "bootstrap-non-config-server.yml";
        }

        return PROP_TEMPLATE_ROOT_PATH + templateFile;
    }

    public static String getSharedPropTemplate(String module) {
        String templateFile = sharedPropTemplateMap.get(module);
        if(StringUtils.isEmpty(templateFile)) {
            templateFile = "common.yml";
        }

        return SHARED_TEMPLATE_PATH + templateFile;
    }

    public static String getSharedCommonPropTemplate() {
        return SHARED_TEMPLATE_PATH + "application.yml";
    }

    public static boolean isConfigServer(String module) {
        return CONFIG_SERVER_MODULE.equals(module);
    }

    public static boolean isInfraModule(String module) {
        if(StringUtils.isEmpty(module)) {
            return false;
        }

        return Arrays.asList(BASIC_CLOUD_MODULES).contains(module);
    }

    public static boolean isGatewayModule(String module) {
        return GATEWAY_SERVER_MODULE.equals(module);
    }
}
