package ljas.application.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ljas.application.cache.Cache;

/**
 * Return values of methods with this annotation get stored in a cache. When the
 * client invokes the same method with the same parameters again, the cached
 * object gets returned and no request will be sent to the server.
 * 
 * <br />
 * <br />
 * Annotated methods mustn't return void as a return value. The server will
 * throw an exception on startup.
 * 
 * <br />
 * <br />
 * Use the {@link Cache#flush()} method to clear all objects from the cache.
 * 
 * @author jonashansen
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cached {

}
