package org.datazup.template.engine;


import com.github.jknack.handlebars.*;
import org.datazup.ring.IObjectBuilder;
import org.datazup.ring.LoadBalancedInstanceAccessor;
import org.datazup.utils.JsonUtils;

import java.io.IOException;

/**
 * Created by admin@datazup on 7/23/16.
 */
public class HandlerBarRenderer {

    private int numOfHandlers = Runtime.getRuntime().availableProcessors();
    private static LoadBalancedInstanceAccessor<HandlebarsWrapper> engineAccessor;

    public HandlerBarRenderer(){
        if (null==engineAccessor){
            synchronized (HandlerBarRenderer.class){
                if (null==engineAccessor){
                    LoadBalancedInstanceAccessor.InstanceBuilder<HandlebarsWrapper> bb
                            = new LoadBalancedInstanceAccessor.InstanceBuilder<>(HandlebarsWrapper.class, numOfHandlers, new HandlebarsBuilder());

                    LoadBalancedInstanceAccessor<HandlebarsWrapper> instanceAccessor = new LoadBalancedInstanceAccessor<>();
                    instanceAccessor.build(bb);

                    engineAccessor = instanceAccessor;
                }
            }
        }
    }

    public HandlebarsWrapper getNextWrapper(){
        return  engineAccessor.getNext();
    }

    public Handlebars getNext(){
        return  getNextWrapper().get();
    }

    private class HandlebarsBuilder implements IObjectBuilder<HandlebarsWrapper>{

        @Override
        public HandlebarsWrapper build() {
            Handlebars engine =  new Handlebars();

            engine.registerHelpers(org.beryx.hbs.Helpers.class);

            engine.registerHelper("isNull", new Helper<Object>() {
                @Override
                public CharSequence apply(Object o, Options options) throws IOException {
                    if (null==o){
                        return "";
                    }else{
                        return options.apply(options.fn);
                    }
                }
            });
            engine.registerHelper("json", new Helper<Object>() {
                @Override
                public CharSequence apply(Object o, Options options) throws IOException {
                    return JsonUtils.getJsonFromObject(o);
                }
            });
            engine.registerHelper("jsonpretty", new Helper<Object>() {
                @Override
                public CharSequence apply(Object o, Options options) throws IOException {
                    return JsonUtils.getJsonFromObjectPretty(o);
                }
            });
            HandlebarsWrapper handlebarsWrapper = new HandlebarsWrapper(engine);
            return handlebarsWrapper;
        }
    }

}
