package org.eclipse.tractusx.edc.agreements.retirement.store.sql;

/**
 * Statement templates and SQL table+column names required for the {@link SqlAgreementsRetirementStore}
 */
public interface SqlAgreementsRetirementStatements {

    default String getIdColumn() {
        return "contract_agreement_id";
    }

    default String getReasonColumn() {
        return "reason";
    }

    default String getRetirementDateColumn() {
        return "agreement_retirement_date";
    }

    default String getTable() {
        return "edc_agreement_retirement";
    }

    String findByIdTemplate();

    String insertTemplate();

    String getDeleteByIdTemplate();

    String getCountByIdClause();

    String getCountVariableName();
}
