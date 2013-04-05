package dbfit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.atteo.evo.classindex.IndexAnnotated;

@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@IndexAnnotated
public @interface DatabaseEnvironment {
    String name();
    String driver();
}

