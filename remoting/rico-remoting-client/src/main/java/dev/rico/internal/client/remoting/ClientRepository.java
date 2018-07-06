package dev.rico.internal.client.remoting;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.UpdateSource;
import dev.rico.internal.remoting.communication.commands.Command;
import dev.rico.internal.remoting.communication.commands.impl.CreateBeanCommand;
import dev.rico.internal.remoting.communication.commands.impl.CreateBeanTypeCommand;
import dev.rico.internal.remoting.communication.commands.impl.DeleteBeanCommand;
import dev.rico.internal.remoting.communication.converters.Converters;
import dev.rico.internal.remoting.repo.ClassInfo;
import dev.rico.internal.remoting.repo.Repository;

import javax.swing.text.html.HTMLDocument;
import java.util.function.Consumer;

public class ClientRepository extends Repository {

    public ClientRepository(Consumer<Command> commandHandler) {
        super(commandHandler);
    }

    public void onCreateBeanTypeCommand(final CreateBeanTypeCommand command) throws Exception {
        final String id = command.getClassId();
        if (containsClassInfoForClassId(id)) {
            throw new RuntimeException("Class info with id " + id + " already defined!");
        }
        final Class beanClass = Class.forName(command.getClassName());
        if (containsClassInfoForClass(beanClass)) {
            throw new RuntimeException("Class info for " + beanClass + " already defined!");
        }
        final ClassInfo classInfo = ClassInfo.create(id, beanClass, getConverters());
        addClassInfo(classInfo);

    }

    public void onCreateBeanCommand(final CreateBeanCommand command) throws Exception {
        Assert.requireNonNull(command, "command");
        final String classId = command.getClassId();
        final ClassInfo classInfo = getClassInfo(classId);
        if(classInfo == null) {
            throw new RuntimeException("no Class info for id" + classId + " defined!");
        }
        final String id = command.getBeanId();
        final UpdateSource source = UpdateSource.OTHER;
        createBean(classInfo, id, source);
    }

    public void onBeanRemovedCommand(final DeleteBeanCommand command) throws Exception {
        final String beanId = Assert.requireNonNull(command, "command").getBeanId();
        final Object bean = getBean(beanId);
        deleteBean(bean);
    }

}
