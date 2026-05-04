package ru.hh.plugins.geminio.templating

/**
 * Exception throws when we have some troubles with Freemarker's templates.
 */
class FreemarkerException(message: String, cause: Throwable?) : RuntimeException(message, cause)
