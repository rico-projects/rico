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
/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package dev.rico.internal.server.validation.validator;

import dev.rico.internal.core.Assert;
import dev.rico.internal.server.validation.InvalidJsonException;
import dev.rico.internal.server.validation.validator.impl.ConstraintsViolationSerializer;
import dev.rico.internal.server.validation.validator.impl.DefaultValidationConfiguration;
import dev.rico.internal.server.validation.validator.impl.RemoteValidator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static dev.rico.internal.server.validation.RemoteConstants.BAD_REQUEST;
import static dev.rico.internal.server.validation.RemoteConstants.CONTENT_TYPE_HEADER_NAME;
import static dev.rico.internal.server.validation.RemoteConstants.IDENTIFIER_PROPERTY_NAME;
import static dev.rico.internal.server.validation.RemoteConstants.INSTANCE_PROPERTY_NAME;
import static dev.rico.internal.server.validation.RemoteConstants.JSON_CONTENT_TYPE;
import static dev.rico.internal.server.validation.RemoteConstants.TYPE_PROPERTY_NAME;
import static dev.rico.internal.server.validation.RemoteConstants.UTF_8;


/**
 * @author Gunnar Morling
 * @author Hendrik Ebbers
 */
public class RemoteValidationServlet extends HttpServlet {

    private final RemoteValidator remoteValidator;
    private final Gson gson;

    public RemoteValidationServlet() {
        this(new DefaultValidationConfiguration());
    }

    public RemoteValidationServlet(final RemoteValidationConfiguration configuration) {
        this(configuration, Validation.buildDefaultValidatorFactory().getValidator());
    }

    public RemoteValidationServlet(final RemoteValidationConfiguration configuration, final Validator validator) {
        this(configuration, validator, RemoteValidationServlet.class.getClassLoader());
    }

    public RemoteValidationServlet(final RemoteValidationConfiguration configuration, final Validator validator, final ClassLoader classLoader) {
        this.remoteValidator = new RemoteValidator(configuration, validator, classLoader);
        this.gson = new GsonBuilder().registerTypeHierarchyAdapter(ConstraintViolation.class, new ConstraintsViolationSerializer(configuration)).create();
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        response.setHeader(CONTENT_TYPE_HEADER_NAME, JSON_CONTENT_TYPE);
        response.setCharacterEncoding(UTF_8);
        final String payload = readPayload(request);
        try {
            final JsonObject requestJson = getAsJsonObject(payload);
            final String typeName = getType(requestJson);
            final JsonElement propertiesElem = requestJson.get(INSTANCE_PROPERTY_NAME);
            if(propertiesElem == null || !propertiesElem.isJsonObject()) {
                throw new InvalidJsonException(INSTANCE_PROPERTY_NAME + " not defined");
            }
            final JsonObject properties = propertiesElem.getAsJsonObject();
            final Map<String, Set<ConstraintViolation<?>>> violationsByProperty = new HashMap<>();

            properties.keySet().forEach(propertyName -> {
                final JsonElement propertyElement = properties.get(propertyName);
                try {
                    final Set<ConstraintViolation<?>> violations = validate(typeName, propertyName, propertyElement);
                    if (!violations.isEmpty()) {
                        violationsByProperty.put(propertyName, violations);
                    }
                } catch (IllegalArgumentException e) {
                    response.setStatus(BAD_REQUEST);
                    throw new RuntimeException("Error in validation", e);
                }
            });

            final String responseString = gson.toJson(violationsByProperty);
            response.getWriter().append(responseString);
        } catch (InvalidJsonException e) {
            response.setStatus(BAD_REQUEST);
            throw new RuntimeException("Error in validation. JSON can not be parsed", e);
        }
    }

    private String readPayload(final HttpServletRequest request) throws IOException {
        Assert.requireNonNull(request, "request");
        return request.getReader().lines().reduce("", (l1, l2) -> l1 + l2);
    }

    private String getType(final JsonObject jsonObject) throws InvalidJsonException {
        Assert.requireNonNull(jsonObject, "jsonObject");
        try {
            return jsonObject
                    .get(TYPE_PROPERTY_NAME)
                    .getAsJsonObject()
                    .get(IDENTIFIER_PROPERTY_NAME)
                    .getAsString();
        } catch (Exception e) {
            throw new InvalidJsonException("Can not parse type", e);
        }
    }

    private JsonObject getAsJsonObject(final String requestPayload) throws InvalidJsonException {
        Assert.requireNonNull(requestPayload, "requestPayload");
        try {
            return new JsonParser().parse(requestPayload).getAsJsonObject();
        } catch (Exception e) {
            throw new InvalidJsonException("Can not parse JSON", e);
        }
    }

    private Set<ConstraintViolation<?>> validate(final String typeName, final String propertyName, final JsonElement propertyElement) {
        Assert.requireNonNull(propertyElement, "propertyElement");
        if (!propertyElement.isJsonPrimitive() && !propertyElement.isJsonNull()) {
            throw new IllegalArgumentException("Only primitive or null properties supported. Wrong type for " + typeName + "." + propertyName);
        }
        if (propertyElement.isJsonNull()) {
            return remoteValidator.validateValue(typeName, propertyName, null);
        }
        final JsonPrimitive primitiveValue = propertyElement.getAsJsonPrimitive();
        if (primitiveValue.isBoolean()) {
            return remoteValidator.validateValue(typeName, propertyName, primitiveValue.getAsBoolean());
        } else if (primitiveValue.isNumber()) {
            return remoteValidator.validateValue(typeName, propertyName, primitiveValue.getAsNumber());
        } else if (primitiveValue.isString()) {
            return remoteValidator.validateValue(typeName, propertyName, primitiveValue.getAsString());
        }
        throw new IllegalArgumentException("Not supported type for " + typeName + "." + propertyName);
    }

    public static void register(final ServletContext servletContext) {
        register(servletContext, "/validate");
    }

    public static void register(final ServletContext servletContext, final String mapping) {
        Assert.requireNonNull(servletContext, "servletContext");
        Assert.requireNonBlank(mapping, "mapping");

        servletContext.addServlet("remoteValidationServlet", new RemoteValidationServlet()).addMapping(mapping);
    }
}
