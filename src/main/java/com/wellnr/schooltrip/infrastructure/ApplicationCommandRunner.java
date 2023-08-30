package com.wellnr.schooltrip.infrastructure;

import com.wellnr.ddd.DomainException;
import com.wellnr.ddd.commands.CommandResult;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.ui.components.ApplicationNotifications;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * A simple class to hide command execution from UI components. UI components should never use
 * or see a domain registry directly. They must always communicate via commands with the
 * application.
 * <p>
 * The usage of this class ensures this.
 */
@Component
@AllArgsConstructor
public class ApplicationCommandRunner {

    private final SchoolTripDomainRegistry domainRegistry;

    private final ApplicationUserSession userSession;

    /**
     * Executes a command by injecting required resources to the commands run command.
     *
     * @param command The command to be executed.
     * @param <T>     The type of the result.
     * @return The result of the command execution.
     */
    public <T extends CommandResult> T run(AbstractSchoolTripCommand<T> command) {
        return command.run(
            userSession.getUser(),
            domainRegistry
        );
    }

    /**
     * This function executes a command and display notification messages on the UI.
     *
     * @param command The command to be executed.
     * @param <T>     The type of the result.
     * @return The result of the command (in case of success).
     */
    public <T extends MessageResult<?>> Optional<T> runAndNotify(AbstractSchoolTripCommand<T> command) {
        try {
            var result = run(command);
            ApplicationNotifications.success(result.getMessage());
            return Optional.of(result);
        } catch (DomainException e) {
            ApplicationNotifications.error(e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            ApplicationNotifications.error("An exception occurred communicating with the server.");
            return Optional.empty();
        }
    }

}
