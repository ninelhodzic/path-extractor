package org.datazup.template.engine;


import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import org.datazup.ring.IObjectBuilder;
import org.datazup.ring.LoadBalancedInstanceAccessor;
import org.datazup.utils.JsonUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.Validate.validIndex;

/**
 * Created by admin@datazup on 7/23/16.
 */
public class HandlerBarRenderer {

    private final int numOfHandlers = Runtime.getRuntime().availableProcessors();
    private static LoadBalancedInstanceAccessor<HandlebarsWrapper> engineAccessor;

    private final Map<String, Helper> handleBarsHelpers = new HashMap<>();

    public HandlerBarRenderer(){

    }

    public void init(){
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

    public void registerHelper(String name, Helper helper){
        this.handleBarsHelpers.put(name, helper);
    }

    private class HandlebarsBuilder implements IObjectBuilder<HandlebarsWrapper>{

        @Override
        public HandlebarsWrapper build() {
            Handlebars engine =  new Handlebars();

            engine.registerHelpers(org.beryx.hbs.Helpers.class);
            engine.registerHelpers(com.github.jknack.handlebars.helper.StringHelpers.class);
            engine.registerHelpers(com.github.jknack.handlebars.HumanizeHelper.class);

            engine.registerHelper("substring", new Helper<Object>() {
                @Override
                public CharSequence apply(final Object value, final Options options) throws IOException {
                    validIndex(options.params, 0, "Required start offset: ");

                    String str = value.toString();
                    Integer start = options.param(0);
                    Integer end = options.param(1, str.length());
                    if (str.length()<end)
                        end = str.length();

                    return str.substring(start, end);
                }
            });

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
            for(String name: handleBarsHelpers.keySet()){
                engine.registerHelper(name, handleBarsHelpers.get(name));
            }
            HandlebarsWrapper handlebarsWrapper = new HandlebarsWrapper(engine);
            return handlebarsWrapper;
        }
    }

}
