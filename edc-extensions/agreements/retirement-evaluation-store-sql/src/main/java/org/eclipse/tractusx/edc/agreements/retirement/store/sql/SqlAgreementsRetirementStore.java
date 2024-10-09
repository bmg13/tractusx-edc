/********************************************************************************
 * Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.edc.agreements.retirement.store.sql;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.spi.persistence.EdcPersistenceException;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.result.StoreResult;
import org.eclipse.edc.sql.QueryExecutor;
import org.eclipse.edc.sql.store.AbstractSqlStore;
import org.eclipse.edc.transaction.datasource.spi.DataSourceRegistry;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.eclipse.tractusx.edc.agreements.retirement.spi.store.AgreementsRetirementStore;
import org.eclipse.tractusx.edc.agreements.retirement.spi.types.AgreementsRetirementEntry;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SqlAgreementsRetirementStore extends AbstractSqlStore implements AgreementsRetirementStore {

    private final SqlAgreementsRetirementStatements statements;

    public SqlAgreementsRetirementStore(DataSourceRegistry dataSourceRegistry, String dataSourceName,
                                        TransactionContext transactionContext, ObjectMapper objectMapper,
                                        QueryExecutor queryExecutor, SqlAgreementsRetirementStatements statements) {
        super(dataSourceRegistry, dataSourceName, transactionContext, objectMapper, queryExecutor);
        this.statements = statements;
    }

    @Override
    public StoreResult<Void> save(AgreementsRetirementEntry entry) {
        Objects.requireNonNull(entry);
        return transactionContext.execute(() -> {
            try (var connection = getConnection()) {
                if (existsById(entry.getAgreementId(), connection)) {
                    return StoreResult.alreadyExists(ALREADY_EXISTS_TEMPLATE.formatted(entry.getAgreementId()));
                }
                queryExecutor.execute(connection, statements.insertTemplate(), entry.getAgreementId(), entry.getReason(), new BigInteger(entry.getAgreementRetirementDate()));
                //INSERT INTO testschema.edc_agreement_retirement (contract_agreement_id, reason, agreement_retirement_date) VALUES ('9e63cbde-7978-4ed9-9ab3-9eab2163bc87', 'long-reason', 1728493178461)
                return StoreResult.success();
            } catch (SQLException e) {
                throw new EdcPersistenceException(e);
            }
        });
    }

    @Override
    public StoreResult<Void> delete(String contractAgreementId) {
        Objects.requireNonNull(contractAgreementId);
        return transactionContext.execute(() -> {
            try (var connection = getConnection()) {
                if (!existsById(contractAgreementId, connection)) {
                    return StoreResult.notFound(NOT_FOUND_TEMPLATE.formatted(contractAgreementId));
                }
                queryExecutor.execute(connection, statements.getDeleteByIdTemplate(), contractAgreementId);
                return StoreResult.success();
            } catch (Exception e) {
                throw new EdcPersistenceException(e.getMessage(), e);
            }
        });
    }

    @Override
    public StoreResult<List<AgreementsRetirementEntry>> findRetiredAgreements(QuerySpec querySpec) {
        Objects.requireNonNull(querySpec);
        return transactionContext.execute(() -> {
            try (var connection = getConnection()) {
                var statement = statements.createQuery(querySpec);
                var result = queryExecutor.query(connection, true, this::mapAgreementsRetirement, statement.getQueryAsString(), statement.getParameters());
                return StoreResult.success(result.collect(Collectors.toList()));
            } catch (SQLException e) {
                throw new EdcPersistenceException(e);
            }
        });
    }

    private boolean existsById(String agreementId, Connection connection) {
        var sql = statements.getCountByIdClause();
        try (var stream = queryExecutor.query(connection, false, this::mapRowCount, sql, agreementId)) {
            return stream.findFirst().orElse(0) > 0;
        }
    }

    private int mapRowCount(ResultSet resultSet) throws SQLException {
        return resultSet.getInt(statements.getCountVariableName());
    }

    private AgreementsRetirementEntry mapAgreementsRetirement(ResultSet resultSet) throws SQLException {
        return AgreementsRetirementEntry.Builder.newInstance()
                .withAgreementId(resultSet.getString(statements.getIdColumn()))
                .withReason(resultSet.getString(statements.getReasonColumn()))
                .withAgreementRetirementDate(resultSet.getString(statements.getRetirementDateColumn()))
                .build();
    }
}