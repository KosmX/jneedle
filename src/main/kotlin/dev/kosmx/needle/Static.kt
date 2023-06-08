package dev.kosmx.needle

import org.objectweb.asm.Opcodes
import org.slf4j.Logger
import org.slf4j.LoggerFactory

const val ASM_VERSION = Opcodes.ASM9


val logger: Logger = LoggerFactory.getLogger("Jneedle")
val currentLogLevel = LogLevel.Info

inline fun log(logLevel: LogLevel = LogLevel.Info, msg: () -> String) {
   if (currentLogLevel >= logLevel) {
       when(logLevel) {
           LogLevel.Error -> logger.error(msg())
           LogLevel.Warning -> logger.warn(msg())
           LogLevel.Info -> logger.info(msg())
           LogLevel.Debug -> logger.debug(msg())
           LogLevel.Trace -> logger.trace(msg())
       }
   }
}

enum class LogLevel(val l: Int) {
    Error(0),
    Warning(1),
    Info(2),
    Debug(3),
    Trace(4),
    ;
}
