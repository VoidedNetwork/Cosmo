package gg.voided.cosmo.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.StringWriter;

@UtilityClass
public class ExceptionUtils {

    /**
     * Gets the stacktrace as a string.
     *
     * @param throwable The throwable.
     * @return The stacktrace as a string.
     */
    @NotNull
    public String getStackTrace(@NotNull Throwable throwable) {
        StringWriter writer = new StringWriter();
        PrintWriter printer = new PrintWriter(writer, true);
        throwable.printStackTrace(printer);
        return writer.getBuffer().toString();
    }
}
