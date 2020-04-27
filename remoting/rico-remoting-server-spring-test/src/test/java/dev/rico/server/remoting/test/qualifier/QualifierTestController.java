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
package dev.rico.server.remoting.test.qualifier;

import dev.rico.remoting.BeanManager;
import dev.rico.core.functional.Binding;
import dev.rico.remoting.server.RemotingAction;
import dev.rico.remoting.server.RemotingController;
import dev.rico.remoting.server.RemotingModel;
import dev.rico.remoting.server.binding.PropertyBinder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static dev.rico.server.remoting.test.qualifier.QualifierTestConstants.BIND_ACTION;
import static dev.rico.server.remoting.test.qualifier.QualifierTestConstants.BOOLEAN_QUALIFIER;
import static dev.rico.server.remoting.test.qualifier.QualifierTestConstants.DUMMY_ACTION;
import static dev.rico.server.remoting.test.qualifier.QualifierTestConstants.INTEGER_QUALIFIER;
import static dev.rico.server.remoting.test.qualifier.QualifierTestConstants.QUALIFIER_CONTROLLER_NAME;
import static dev.rico.server.remoting.test.qualifier.QualifierTestConstants.STRING_QUALIFIER;
import static dev.rico.server.remoting.test.qualifier.QualifierTestConstants.UNBIND_ACTION;

@RemotingController(QUALIFIER_CONTROLLER_NAME)
public class QualifierTestController {

    @RemotingModel
    private QualifierTestModel model;

    @Autowired
    private BeanManager beanManager;

    @Autowired
    private PropertyBinder binder;

    private final List<Binding> bindings = new ArrayList<>();

    @PostConstruct
    public void init() {
        QualifierTestSubModelOne model1 = beanManager.create(QualifierTestSubModelOne.class);
        model.subModelOneProperty().set(model1);

        QualifierTestSubModelTwo model2 = beanManager.create(QualifierTestSubModelTwo.class);
        model.subModelTwoProperty().set(model2);

        bind();
    }

    private void bind() {
        bindings.add(binder.bind(model.subModelOneProperty().get().booleanProperty(), BOOLEAN_QUALIFIER));
        bindings.add(binder.bind(model.subModelOneProperty().get().stringProperty(), STRING_QUALIFIER));
        bindings.add(binder.bind(model.subModelOneProperty().get().integerProperty(), INTEGER_QUALIFIER));

        bindings.add(binder.bind(model.subModelTwoProperty().get().booleanProperty(), BOOLEAN_QUALIFIER));
        bindings.add(binder.bind(model.subModelTwoProperty().get().stringProperty(), STRING_QUALIFIER));
        bindings.add(binder.bind(model.subModelTwoProperty().get().integerProperty(), INTEGER_QUALIFIER));
    }

    private void unbind() {
        for(Binding binding : bindings) {
            binding.unbind();
        }
        bindings.clear();
    }

    @RemotingAction(DUMMY_ACTION)
    public void dummyAction() {

    }

    @RemotingAction(BIND_ACTION)
    public void bindAction() {
        bind();
    }

    @RemotingAction(UNBIND_ACTION)
    public void unbindAction() {
        unbind();
    }
}
