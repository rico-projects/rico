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
package dev.rico.server;

import dev.rico.internal.server.SpringBootstrap;
import dev.rico.internal.server.SpringBeanFactory;
import org.apiguardian.api.API;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * Enables Rico in a Spring based application.
 *
 * <p>To be used together with @{@link Configuration Configuration}:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableRico
 * public class AppConfig {
 *
 * }</pre>
 *
 *
 * @author Hendrik Ebbers
 */
@Import({SpringBootstrap.class, SpringBeanFactory.class})
@Documented
@Target({TYPE})
@Retention(RUNTIME)
@API(since = "0.x", status = MAINTAINED)
public @interface EnableRico {
}
