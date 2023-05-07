package com.wellnr.ddd.commands;

import com.wellnr.common.markup.Nothing;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Return a single message as result of an operation.
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class MessageResult<T> implements CommandResult {

    String message;

    T data;

    public static MessageResult<Nothing> apply(String message) {
        return apply(message, Nothing.getInstance());
    }

    /**
     * Create a new message result. The method uses String.format under the hood to substitute variable placeholders.
     *
     * @param s    The messages
     * @param args Variables for substitution
     * @return The message result object
     */
    public static MessageResult<Nothing> formatted(String s, Object... args) {
        return apply(String.format(s, args), Nothing.getInstance());
    }

    /**
     * Add data to the message result.
     *
     * @param data   The data to be added.
     * @param <TYPE> The type of data.
     * @return A new {@link MessageResult}.
     */
    public <TYPE> MessageResult<TYPE> withData(TYPE data) {
        return MessageResult.apply(this.message, data);
    }

}
