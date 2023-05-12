package com.wellnr.ddd.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wellnr.ddd.commands.Command;
import com.wellnr.ddd.commands.CommandResult;
import com.wellnr.ddd.commands.DomainCommands;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.core.model.user.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class DomainCommandController {

    private final ObjectMapper objectMapper;

    private final DomainCommands domainCommands;

    private final ApplicationContext applicationContext;

    @SuppressWarnings("unchecked")
    public DomainCommandController(
        @Autowired ObjectMapper objectMapper,
        @Autowired ApplicationContext applicationContext) {

        this.objectMapper = objectMapper;
        this.applicationContext = applicationContext;

        /*
         * Initialize domain commands.
         */
        var packages = AutoConfigurationPackages.get(applicationContext.getAutowireCapableBeanFactory());
        var provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(AbstractSchoolTripCommand.class));

        Set<Class<? extends Command<?>>> allCommands = new HashSet<>();

        for (String pkg : packages) {
            var candidates = provider.findCandidateComponents(pkg);

            var commands = candidates
                .stream()
                .<Class<? extends Command<?>>>map(beanDefinition -> {
                    try {
                        return (Class<? extends Command<?>>) this
                            .getClass()
                            .getClassLoader()
                            .loadClass(beanDefinition.getBeanClassName());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());

            allCommands.addAll(commands);
        }

        this.domainCommands = DomainCommands.apply(Set.copyOf(allCommands));
    }

    @SneakyThrows
    @PostMapping(
        path = "/api/commands",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE
        })
    @SuppressWarnings("unchecked")
    public CommandResult runCommand(HttpEntity<String> request, HttpServletRequest servletRequest) {
        String json = request.getBody();

        try (var parser = objectMapper.createParser(json)) {
            var objectNode = (ObjectNode) parser.readValueAsTree();
            var cmd = objectNode.get("command").asText();
            var cmdClass = domainCommands.getCommand(cmd);
            var cmdInstance = (Command<Object>) objectMapper.readValue(json, cmdClass);
            var domainRegistry = applicationContext.getBean(cmdInstance.getDomainRegistryType());
            var user = (User) servletRequest.getAttribute("APP__USER");

            return cmdInstance.run(user, domainRegistry);
        }
    }

}
