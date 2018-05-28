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
package dev.rico.internal.server.data;

import dev.rico.internal.core.Assert;
import dev.rico.internal.server.data.event.PersistenceListener;
import dev.rico.internal.server.data.event.PersistenceContextImpl;
import dev.rico.server.data.AbstractEntity;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.DialectName;
import org.javers.repository.sql.JaversSqlRepository;
import org.javers.repository.sql.SqlRepositoryBuilder;

import java.util.List;

public class AuditContext {

    private static final String DB_H2 = "h2";
    private static final String DB_POSTGRES = "postgres";
    private static final String DB_MYSQL = "mysql";
    private final Javers javers;

    public AuditContext(final ConnectionProvider connectionProvider, final String dialect, final String schema) {
        this(connectionProvider, convertToDialect(dialect), schema);
    }

    public AuditContext(final ConnectionProvider connectionProvider, final DialectName dialect, final String schema) {
        Assert.requireNonNull(connectionProvider , "connectionProvider");
        Assert.requireNonNull(dialect , "dialect");
        Assert.requireNonNull(schema , "schema");
        final JaversSqlRepository repository = SqlRepositoryBuilder.sqlRepository()
                .withConnectionProvider(connectionProvider)
                .withDialect(dialect)
                .withSchema(schema)
                .build();
        this.javers = JaversBuilder.javers()
                .registerJaversRepository(repository)
                .build();

        PersistenceContextImpl.getInstance().addListener(new PersistenceListener() {
            @Override
            public void onEntityPersisted(final AbstractEntity entity) {
                if(isAuditable(entity)) {
                    javers.commit(getAuthor(), entity);
                }
            }

            @Override
            public void onEntityRemoved(final AbstractEntity entity) {
                if(isAuditable(entity)) {
                    javers.commitShallowDelete(getAuthor(), entity);
                }
            }

            @Override
            public void onEntityUpdated(final AbstractEntity entity) {
                if(isAuditable(entity)) {
                    javers.commit(getAuthor(), entity);
                }
            }
        });
    }

    private static DialectName convertToDialect(final String dialectName) {
        if(dialectName.equals(DB_H2)) {
            return DialectName.H2;
        }
        if(dialectName.equals(DB_POSTGRES)) {
            return DialectName.POSTGRES;
        }
        if(dialectName.equals(DB_MYSQL)) {
            return DialectName.MYSQL;
        }
        throw new RuntimeException("Dialect not supported");
    }

    private <T extends AbstractEntity> boolean isAuditable(final T entity) {
        Assert.requireNonNull(entity , "entity");
        if(entity.getClass().isAnnotationPresent(Auditable.class)) {
            return true;
        }
        return false;
    }

    private String getAuthor() {
        return "unknown";
    }

    public <T extends AbstractEntity> List<CdoSnapshot> getChanges(final T entity) {
        Assert.requireNonNull(entity , "entity");
        if(isAuditable(entity)) {
            return javers.findSnapshots(QueryBuilder.byInstanceId(entity.getId(), entity.getClass()).build());
        } else {
            throw new RuntimeException("Entity not auditable");
        }
    }
}
