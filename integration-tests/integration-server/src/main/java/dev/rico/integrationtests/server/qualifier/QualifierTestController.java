/*
 * Copyright 2018 Karakun AG.
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
package dev.rico.integrationtests.server.qualifier;

import dev.rico.integrationtests.qualifier.QualifierTestBean;
import dev.rico.integrationtests.qualifier.QualifierTestSubBean;
import dev.rico.server.remoting.BeanManager;
import dev.rico.core.functional.Binding;
import dev.rico.server.remoting.RemotingAction;
import dev.rico.server.remoting.RemotingController;
import dev.rico.server.remoting.RemotingModel;
import dev.rico.server.remoting.binding.PropertyBinder;
import dev.rico.server.remoting.binding.Qualifier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static dev.rico.integrationtests.qualifier.QualifierTestConstants.BIND_ACTION;
import static dev.rico.integrationtests.qualifier.QualifierTestConstants.DUMMY_ACTION;
import static dev.rico.integrationtests.qualifier.QualifierTestConstants.QUALIFIER_CONTROLLER_NAME;
import static dev.rico.integrationtests.qualifier.QualifierTestConstants.UNBIND_ACTION;

@RemotingController(QUALIFIER_CONTROLLER_NAME)
public class QualifierTestController {

    private final static Qualifier<String> STRING_QUALIFIER = Qualifier.create();

    private final static Qualifier<Boolean> BOOLEAN_QUALIFIER = Qualifier.create();

    private final static Qualifier<Integer> INTEGER_QUALIFIER = Qualifier.create();

    @RemotingModel
    private QualifierTestBean model;

    @Inject
    private BeanManager beanManager;

    @Inject
    private PropertyBinder binder;

    final private List<Binding> bindings = new ArrayList<>();

    @PostConstruct
    public void init() {
        QualifierTestSubBean qualifierTestSubBeanOne = beanManager.create(QualifierTestSubBean.class);
        model.setQualifierTestSubBeanOneValue(qualifierTestSubBeanOne);

        QualifierTestSubBean qualifierTestSubBeanTwo = beanManager.create(QualifierTestSubBean.class);
        model.setQualifierTestSubBeanTwoValue(qualifierTestSubBeanTwo);

        bind();
    }

    private void bind() {
        bindings.add(binder.bind(model.getQualifierTestSubBeanOneValue().booleanValueProperty(), BOOLEAN_QUALIFIER));
        bindings.add(binder.bind(model.getQualifierTestSubBeanOneValue().stringValueProperty(), STRING_QUALIFIER));
        bindings.add(binder.bind(model.getQualifierTestSubBeanOneValue().integerValueProperty(), INTEGER_QUALIFIER));

        bindings.add(binder.bind(model.getQualifierTestSubBeanTwoValue().booleanValueProperty(), BOOLEAN_QUALIFIER));
        bindings.add(binder.bind(model.getQualifierTestSubBeanTwoValue().stringValueProperty(), STRING_QUALIFIER));
        bindings.add(binder.bind(model.getQualifierTestSubBeanTwoValue().integerValueProperty(), INTEGER_QUALIFIER));
    }

    private void unbind() {
        for(Binding binding : bindings) {
            binding.unbind();
        }
        bindings.clear();
    }

    @RemotingAction(DUMMY_ACTION)
    public void dummyAction() {}

    @RemotingAction(BIND_ACTION)
    public void bindAction() {
        bind();
    }

    @RemotingAction(UNBIND_ACTION)
    public void unbindAction() {
        unbind();
    }
}

