/*
 * Copyright 2018-2019 Karakun AG.
 * Copyright 2015-2018 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.rico.internal.remoting;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.info.ClassInfo;
import dev.rico.internal.remoting.info.PropertyInfo;
import dev.rico.internal.remoting.legacy.core.BaseAttribute;
import dev.rico.internal.remoting.legacy.core.BasePresentationModel;
import dev.rico.remoting.ObservableList;
import dev.rico.remoting.Property;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;


/**
 * A {@code BeanBuilder} is responsible for building a remoting Bean that is specified as a class. The main
 * (and only public) method is {@link #create(Class)}, which expects the {@code Class} of the remoting Bean and
 * returns the generated Bean.
 * <p>
 * The generated remoting Bean will be registered in the {@link BeanRepositoryImpl}.
 */
@API(since = "0.x", status = INTERNAL)
public abstract class AbstractBeanBuilder implements BeanBuilder {

    private final ClassRepository classRepository;
    private final BeanRepository beanRepository;
    private final ListMapper listMapper;
    private final PresentationModelBuilderFactory builderFactory;

    public AbstractBeanBuilder(final ClassRepository classRepository, final BeanRepository beanRepository, final ListMapper listMapper, final PresentationModelBuilderFactory builderFactory, final EventDispatcher dispatcher) {
        this.classRepository = Assert.requireNonNull(classRepository, "classRepository");
        this.beanRepository = Assert.requireNonNull(beanRepository, "beanRepository");
        this.listMapper = listMapper;
        this.builderFactory = Assert.requireNonNull(builderFactory, "builderFactory");

        dispatcher.addAddedHandler(new RemotingEventHandler() {
            @Override
            public void onEvent(final BasePresentationModel model) {
                Assert.requireNonNull(model, "model");
                final ClassInfo classInfo = classRepository.getClassInfo(model.getPresentationModelType());

                Assert.requireNonNull(classInfo, "classInfo");
                final Class<?> beanClass = classInfo.getBeanClass();

                createInstanceForClass(classInfo, beanClass, model, UpdateSource.OTHER);
            }
        });
    }

    public <T> T create(final Class<T> beanClass) {
        final ClassInfo classInfo = classRepository.getOrCreateClassInfo(beanClass);
        final BasePresentationModel model = buildPresentationModel(classInfo);

        return createInstanceForClass(classInfo, beanClass, model, UpdateSource.SELF);
    }

    private <T> T createInstanceForClass(final ClassInfo classInfo, final Class<T> beanClass, final BasePresentationModel model, final UpdateSource source) {
        Assert.requireNonNull(beanClass, "beanClass");
        try {
            final T bean = beanClass.newInstance();

            setupProperties(classInfo, bean, model);
            setupObservableLists(classInfo, bean, model);

            beanRepository.registerBean(bean, model, source);
            return bean;

        } catch (Exception e) {
            throw new RuntimeException("Cannot create bean of type " + beanClass, e);
        }
    }

    private BasePresentationModel buildPresentationModel(final ClassInfo classInfo) {
        try {
            Assert.requireNonNull(classInfo, "classInfo");
            final PresentationModelBuilder builder = builderFactory.createBuilder()
                    .withType(classInfo.getModelType());
            classInfo.forEachProperty(new ClassInfo.PropertyIterator() {
                @Override
                public void call(final PropertyInfo propertyInfo) {
                    Assert.requireNonNull(propertyInfo, "propertyInfo");
                    builder.withAttribute(propertyInfo.getAttributeName());
                }
            });
            return builder.create();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create presentation model for type " + classInfo.getBeanClass(), e);
        }
    }

    private void setupProperties(final ClassInfo classInfo, final Object bean, final BasePresentationModel model) {
        Assert.requireNonNull(classInfo, "classInfo");
        Assert.requireNonNull(model, "model");
        classInfo.forEachProperty(new ClassInfo.PropertyIterator() {
            @Override
            public void call(final PropertyInfo propertyInfo) {
                try {
                    Assert.requireNonNull(propertyInfo, "propertyInfo");
                    final BaseAttribute attribute = model.getAttribute(propertyInfo.getAttributeName());
                    final Property property = create(attribute, propertyInfo);
                    propertyInfo.setPriviliged(bean, property);
                } catch (Exception e) {
                    throw new RuntimeException("Can not create property " + propertyInfo.getAttributeName(), e);
                }
            }
        });
    }

    private void setupObservableLists(final ClassInfo classInfo, final Object bean, final BasePresentationModel model) {
        Assert.requireNonNull(classInfo, "classInfo");
        classInfo.forEachObservableList(new ClassInfo.PropertyIterator() {
            @Override
            public void call(final PropertyInfo observableListInfo) {
                try {
                    Assert.requireNonNull(observableListInfo, "observableListInfo");
                    final ObservableList observableList = create(observableListInfo, model, listMapper);
                    observableListInfo.setPriviliged(bean, observableList);
                } catch (Exception e) {
                    throw new RuntimeException("Can not create observable list " + observableListInfo.getAttributeName(), e);
                }
            }
        });
    }

    protected abstract <T> ObservableList<T> create(final PropertyInfo observableListInfo, final BasePresentationModel model, final ListMapper listMapper);


    protected abstract <T> Property<T> create(final BaseAttribute attribute, final PropertyInfo propertyInfo);
}
