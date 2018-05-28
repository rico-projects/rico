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
/**
 * This package contains the SPI for Rico server modules. All modules that are based on this API and are at the classpath at the server start will be automatically loaded and started. A module is defined by a {@link dev.rico.server.spi.ServerModule} implementation and must be annotated by {@link dev.rico.server.spi.ModuleDefinition}. All modules will be loaded by the default Java SPI implementation. See {@link java.util.ServiceLoader} for more information.
 *
 *
 *
 * @author Hendrik Ebbers
 *
 * @see dev.rico.server.spi.ServerModule
 * @see dev.rico.server.spi.ModuleDefinition
 * @see java.util.ServiceLoader
 */
package dev.rico.server.spi;