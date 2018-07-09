package dev.rico.sample;

import dev.rico.server.remoting.BeanManager;
import dev.rico.server.remoting.ClientSessionExecutor;
import dev.rico.server.remoting.RemotingContext;
import dev.rico.server.remoting.RemotingController;
import dev.rico.server.remoting.RemotingModel;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.lang.annotation.ElementType;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@RemotingController("TestController")
public class TestController {

    @Autowired
    private RemotingContext context;

    @Autowired
    private BeanManager beanManager;

    @RemotingModel
    private TestModel model;

    @PostConstruct
    public void init() {
        update();
        final ClientSessionExecutor executor = context.createSessionExecutor();
        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
     //           executor.runLaterInClientSession(() -> update()).get();
                Thread.sleep(200);
            }
        });
        model.getValueA().onChanged(e -> model.getValueB().set(e.getNewValue()));
    }

    private void update() {
        final Random random = new Random();

        model.getItems().clear();

        IntStream.range(0,  2).forEach(i -> {
            final Item item = beanManager.create(Item.class);
            item.setBooleanValue(random.nextBoolean());
            item.setDoubleValue(random.nextDouble());
            item.setFloatValue(random.nextFloat());
            item.setLongValue(random.nextLong());
            item.setIntegerValue(random.nextInt());
            item.setEnumValue(ElementType.values()[random.nextInt(ElementType.values().length - 1)]);
            item.setStringValue(UUID.randomUUID().toString());
            item.setUuidValue(UUID.randomUUID());
            item.setDateValue(new Date(random.nextLong()));
            item.setCalendarValue(Calendar.getInstance());
            model.getItems().add(item);
        });

    }

}
