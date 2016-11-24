package org.datazup.template.engine;

import com.github.jknack.handlebars.Handlebars;
import org.datazup.ring.IClosableWrapper;

/**
 * Created by ninel on 11/24/16.
 */
public class HandlebarsWrapper implements IClosableWrapper<Handlebars> {
    private Handlebars handlebars;

    public HandlebarsWrapper(Handlebars handlebars){
        this.handlebars = handlebars;
    }

    @Override
    public void close() {

    }

    @Override
    public Handlebars get() {
        return handlebars;
    }
}
