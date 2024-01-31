package me.yhamarsheh.bridgersumo.commands.handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation containing properties of a command.
 * The annotated method will be registered by CommandRegistry.
 *
 * @version 1.1
 * @author Nova41
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RegisterAsCommand {
    /**
     * The usage of the command (without argument part, see example below). Must has "/" in the beginning.
     *
     * e.g. The usage of command "/skyblock create \<skyblock-name\>" is "/skyblock create"
     *
     * @return The usage of the command
     */
    String command();

    /**
     * The number of parameters the command needs.
     *
     * @return the number of parameters the command needs
     */
    int parameters() default 0;

    /**
     * Whether to override parameter limit. If enabled, this command can accept infinite number of parameters.
     *
     * @return whether to override parameter limit
     */
    boolean overrideParameterLimit() default false;

    /**
     * Whether the command is only open for players.
     *
     * @return whether to block non-player from executing the command.
     */
    boolean disallowNonPlayer() default false;

    /**
     * The permission of the command.
     *
     * @return the permission of the command
     */
    String permission() default "";

}