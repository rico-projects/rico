# Rico Platform Bean Validation

This module adds bean validation (JSR 303) support to the property API of Rico. 

Properties and collections that are defined in remoting models can be annotated by a Bean Validation constraint annotation. It's plan to support all the default JSR-303 annotations by simply adding this module.
The module based on a plugin mechanism for JSR-303 and therefore you only need to add the dependency to your project to use the validation support:

```xml
<dependency>
    <groupId>dev.rico</groupId>
    <artifactId>rico-remoting-bean-validation</artifactId>
    <version>VERSION</version>
</dependency>
```

The module don't depend on a JSR-303 implementation. If your application server don't provide an implementation you need to add for example Hibernate Validation as a dependency. Here is an example for Maven:

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>5.1.3.Final</version>
</dependency>
<dependency>
    <groupId>org.glassfish</groupId>
    <artifactId>javax.el</artifactId>
    <version>3.0.0</version>
</dependency>
```

By doing so you can create a remoting model that looks like this:

```Java
@RemotingBean
public class MyModel {

    @NotNull
    private Property<String> value;

    public Property<String> valueProperty() {
        return value1;
    }
}

```

By using a validator you can now easily validate instances of the model as described in the bean validation documentation or several tutorials. Here is a basic code snippet that creates a validator by hand and validates a remoting model:

```Java
Configuration<?> validationConf = Validation.byDefaultProvider().configure();
Validator validator = validationConf.buildValidatorFactory().getValidator();
Set<ConstraintViolation<TestBean>> violations = validator.validate(remotingModel);
if(!violations.isEmpty()) {
    //Handle violations
}

```

