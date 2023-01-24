package com.github.rccookie.math.calculator;

import com.github.rccookie.util.Arguments;

/**
 * A command for a calculator.
 */
public abstract class Command {

    private final String description;

    protected Command(String description) {
        this.description = Arguments.checkNull(description, "description");
    }

    /**
     * Invokes the command.
     *
     * @param calculator The calculator to apply the command to
     * @param args The command args. args[0] should be the name as which the command
     *             is registered in the calculator
     * @throws IllegalCommandException If the command cannot be executed properly
     */
    public abstract void invoke(Calculator calculator, String[] args) throws IllegalCommandException;

    /**
     * Returns the description for the command.
     *
     * @return The command's description
     */
    public String getDescription() {
        return description;
    }
}
