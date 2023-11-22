package com.genai.server.services;

import com.genai.server.gcp.BigqueryHelper;
import com.genai.server.gcp.TranslationHelper;
import com.google.cloud.bigquery.*;
import com.google.cloud.bigquery.InsertAllRequest.RowToInsert;
import com.google.common.collect.Streams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ReviewService {
    private final BigqueryHelper bigqueryHelper;
    private final TranslationHelper translationHelper;
    private final static Gson GSON = new Gson();
    public ReviewService(BigqueryHelper bigqueryHelper, TranslationHelper translationHelper) {
        this.bigqueryHelper = bigqueryHelper;
        this.translationHelper = translationHelper;
    }
//    @Scheduled(cron = "0 13 * * *")
    public void classificationReviews() throws Exception {
        String eventDate = ZonedDateTime.now(ZoneId.of("UTC")).minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        TableResult result = bigqueryHelper.bigqueryCommand("SELECT * FROM `zegobi-datacenters.AllProject_OverviewMetric.GOOGLE_PLAYSTORE_play_store_review` \n" +
                "WHERE event_date BETWEEN \"2023-11-01\" AND \"2023-11-23\"");
        System.out.println(result.getTotalRows());
        while (result.hasNextPage() || result.streamValues().count() != 0L) {
            List<String> reviewTexts = new ArrayList<>();
            result.getValues().forEach(row -> {
                reviewTexts.add(row.get("review_text").getStringValue());
            });
            List<String> reviewTextsTranslated = translationHelper.translateTexts(reviewTexts);
            List<Map<String, Object>> predictResult = predictionReviews(reviewTextsTranslated);
            Iterable<RowToInsert> rowsReviewPredict = rawDataToRowsReviewPredict(result, reviewTextsTranslated, predictResult);
            System.out.println(rowsReviewPredict);
            bigqueryHelper.tableInsertRows(
                    "AllProject_OverviewMetric",
                    "GOOGLE_PLAYSTORE_play_store_labeled_review",
                    rowsReviewPredict
            );
            result = result.getNextPage();
            if(result == null) break;

        }
    }

    private Iterable<RowToInsert> rawDataToRowsReviewPredict(TableResult reviews,
                                                             List<String> reviewTextsTranslated,
                                                             List<Map<String, Object>> predictReviews) {
        Schema schemaReview = reviews.getSchema();
        System.out.println(schemaReview);
        return Streams.mapWithIndex(
                reviews.streamValues(),
                (review, index) -> {
                    Map<String, Object> rowContent = new HashMap<>();
                    schemaReview.getFields().forEach(field -> {
                        rowContent.put(field.getName(), review.get(field.getName()).getValue());
                    });
                    rowContent.put("review_text_translated", reviewTextsTranslated.get((int) index));
                    rowContent.put("label", predictReviews.get((int) index).get("label"));
                    return RowToInsert.of(rowContent);
                }).toList();
    }

    private List<Map<String, Object>> predictionReviews(List<String> reviewTextsTranslated) throws IOException {
        URL url = new URL("http://localhost:5000");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        String encoded = Base64.getEncoder().encodeToString(("ikame"+":"+"ikamemobi07").getBytes(StandardCharsets.UTF_8));
//        conn.setRequestProperty("Authorization", "Basic "+encoded);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        String jsonBody = "{ \"reviews\": " + GSON.toJson(reviewTextsTranslated) + "}";
        try(OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            return GSON.fromJson(response.toString(), new TypeToken<List<Map<String, Object>>>(){}.getType());
        }
    }
}
