package com.genai.server.gcp;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.google.cloud.bigquery.BigQuery.QueryResultsOption;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@Component
@Slf4j
public class BigqueryHelper {
    private final static BigQuery bigQuery;

    static {
        try {
            bigQuery = BigQueryOptions.newBuilder()
                            .setCredentials(GoogleCredentials.fromStream(new FileInputStream("src/main/resources/predict-reviews.json"))).build().getService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TableResult bigqueryCommand(String queryString) throws Exception {

        QueryJobConfiguration queryJobConfiguration = QueryJobConfiguration.newBuilder(queryString)
                .setUseLegacySql(false).build();
        JobId jobId = JobId.of(UUID.randomUUID().toString());
        Job queryJob = bigQuery.create(JobInfo.newBuilder(queryJobConfiguration).setJobId(jobId).build());
        queryJob = queryJob.waitFor();
        if (queryJob == null) {
            throw new RuntimeException("Job no longer exists");
        } else if(queryJob.getStatus().getError() != null) {
            throw new RuntimeException(queryJob.getStatus().getError().toString());
        }
        return queryJob.getQueryResults(QueryResultsOption.pageSize(100));
    }

    public void tableInsertRows(
            String datasetName, String tableName, Iterable<InsertAllRequest.RowToInsert> rowsContent) {
        try {
            TableId tableId = TableId.of(datasetName, tableName);
            BigQuery bigQueryInsert = BigQueryOptions.newBuilder().setProjectId("dino-team-329405")
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream("src/main/resources/predict-reviews.json"))).build().getService();
            InsertAllResponse response =
                    bigQueryInsert.insertAll(
                            InsertAllRequest.newBuilder(tableId)
                                    .setRows(rowsContent)
                                    .build());

            if (response.hasErrors()) {
                for (Map.Entry<Long, List<BigQueryError>> entry : response.getInsertErrors().entrySet()) {
                    log.error("Response error: \n" + entry.getValue());
                }
            }
            log.info("Rows successfully inserted into table");
        } catch (BigQueryException e) {
            log.error("Insert operation not performed \n" + e.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void runCreateTable(String datasetName, String tableName) {
        Schema schema =
                Schema.of(
                        Field.of("dataSource", StandardSQLTypeName.STRING),
                        Field.of("project_id", StandardSQLTypeName.STRING),
                        Field.of("operating_system", StandardSQLTypeName.STRING),
                        Field.of("event_date", StandardSQLTypeName.DATE),
                        Field.of("event_timestamp", StandardSQLTypeName.TIMESTAMP),
                        Field.of("month", StandardSQLTypeName.STRING),
                        Field.of("review_date", StandardSQLTypeName.DATETIME),
                        Field.of("app_package_name", StandardSQLTypeName.STRING),
                        Field.of("app_version_code", StandardSQLTypeName.STRING),
                        Field.of("app_version_name", StandardSQLTypeName.STRING),
                        Field.of("reviewer_language", StandardSQLTypeName.STRING),
                        Field.of("device", StandardSQLTypeName.STRING),
                        Field.of("star_rating", StandardSQLTypeName.INT64),
                        Field.of("review_text", StandardSQLTypeName.STRING),
                        Field.of("review_link", StandardSQLTypeName.STRING),
                        Field.of("developer_reply_date", StandardSQLTypeName.DATETIME),
                        Field.of("developer_reply_text", StandardSQLTypeName.STRING),
                        Field.of("resource_name", StandardSQLTypeName.STRING),
                        Field.of("review_text_translated", StandardSQLTypeName.STRING),
                        Field.of("label", StandardSQLTypeName.STRING)
                );

        createTable(datasetName, tableName, schema);
    }

    public void createTable(String datasetName, String tableName, Schema schema) {
        try {
            // Initialize client that will be used to send requests. This client only needs to be created
            // once, and can be reused for multiple requests
            BigQuery bigQueryInsert = BigQueryOptions.newBuilder().setProjectId("dino-team-329405")
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream("src/main/resources/predict-reviews.json"))).build().getService();
            TableId tableId = TableId.of(datasetName, tableName);
            TableDefinition tableDefinition = StandardTableDefinition.of(schema);
            TableInfo tableInfo = TableInfo.newBuilder(tableId, tableDefinition).build();
            bigQueryInsert.create(tableInfo);
            System.out.println("Table created successfully");
        } catch (BigQueryException e) {
            System.out.println("Table was not created. \n" + e.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteTable(String datasetName, String tableName) {
        try {
            // Initialize client that will be used to send requests. This client only needs to be created
            // once, and can be reused for multiple requests.
            BigQuery bigQueryInsert = BigQueryOptions.newBuilder().setProjectId("dino-team-329405")
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream("src/main/resources/predict-reviews.json"))).build().getService();
            boolean success = bigQueryInsert.delete(TableId.of(datasetName, tableName));
            if (success) {
                System.out.println("Table deleted successfully");
            } else {
                System.out.println("Table was not found");
            }
        } catch (BigQueryException e) {
            System.out.println("Table was not deleted. \n" + e.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
