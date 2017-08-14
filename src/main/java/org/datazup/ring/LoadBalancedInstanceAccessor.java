package org.datazup.ring;

import java.lang.reflect.Array;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ninel on 4/28/16.
 * http://stackoverflow.com/questions/1050991/singleton-with-arguments-in-java
 */
public class LoadBalancedInstanceAccessor<T extends IClosableWrapper<?>> {

    private T[] instances = null;// new ArrayList<>();
    AtomicInteger counter = new AtomicInteger(0);

    private static class Holder {
        static final LoadBalancedInstanceAccessor INSTANCE = new LoadBalancedInstanceAccessor();
    }

    public static LoadBalancedInstanceAccessor getInstance() {
        return Holder.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public void build(InstanceBuilder<?> instanceBuilder) {
        if (null==instances){
            synchronized (LoadBalancedInstanceAccessor.class) {
                if (null==instances){
                    instances = (T[]) Array.newInstance(instanceBuilder.clazz, instanceBuilder.count);

                    for (int i=0;i<instanceBuilder.count;i++){
                        T instance = (T) instanceBuilder.initializer.build();
                        instances[i] = instance;
                    }
                }
            }
        }

    }

    public void close(){
        for (T instance: instances){
            instance.close();
        }
    }

    public int getIndex(){
        return counter.get();
    }

    public T getCurrent(){
        return instances[counter.get()];
    }


    public T getNext() {
        T instance = instances[counter.get()];

        int next = counter.incrementAndGet(); //counter++;
        if (next == instances.length - 1) {
            counter.set(0);
        }

        return instance;
    }

    public LoadBalancedInstanceAccessor() {

    }

    public static class InstanceBuilder<T> {
        private Class<T> clazz;
        private int count = 0;
        private IObjectBuilder<T> initializer;


        public InstanceBuilder(Class<T> clazz, int count, IObjectBuilder<T> initializer) {
            this.clazz = clazz;
            this.count = count;
            this.initializer = initializer;
        }

        public void build() {
            Holder.INSTANCE.build((InstanceBuilder<?>) this);
        }
    }
}
