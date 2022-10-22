package hackathon.supervision.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hackathon.supervision.model.UrlNormalizator;
import hackathon.supervision.model.VirustotalReport;
import hackathon.supervision.model.VirustotalSummary;
import lombok.RequiredArgsConstructor;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VirustotalService {

    String api_key = "13a09b8fdbd061baef8d959fe8a5ec70f032ac2853999dc6682440a9edb9dd06";
    private UrlNormalizator normalizedUrl;
    private VirustotalReport virustotalReport;

    public VirustotalReport reportVirustotal(String url) throws IOException, ExecutionException, InterruptedException {
        normalizedUrl = new UrlNormalizator(url);

        return VirustotalReport.builder()
                .domain(normalizedUrl.getDomain())
                .virustotalSummary(getAnalysReportApi(getIdUrl(url)))
                .build();
    }

    public String getIdUrl(String url) throws JsonProcessingException {
        String addressUrlId;
        try {
            AsyncHttpClient client = new DefaultAsyncHttpClient();
            addressUrlId = client.prepare("POST", "https://www.virustotal.com/api/v3/urls")
                    .setHeader("accept", "application/json")
                    .setHeader("content-type", "application/x-www-form-urlencoded")
                    .setHeader("x-apikey", api_key)
                    .setBody("url=" + url)
                    .execute().get().getResponseBody();
            client.close();
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return mapIdUrl(addressUrlId);
    }

    public String mapIdUrl(String jsonData) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(jsonData, new TypeReference<>() {
        });
        String id = map.entrySet().stream()
                .filter(it -> it.getKey().equals("data"))
                .map(it -> (Map<String, String>) it.getValue())
                .map(it -> it.get("id"))
                .collect(Collectors.joining());

        String[] split = id.split("-");
        return split[1];
    }


    public VirustotalSummary getAnalysReportApi(String url_id) throws ExecutionException, InterruptedException, JsonProcessingException {
        AsyncHttpClient client = new DefaultAsyncHttpClient();
        String report;

        try {
            report = client.prepare("GET", "https://www.virustotal.com/api/v3/urls/" + url_id)
                    .setHeader("accept", "application/json")
                    .setHeader("x-apikey", api_key)
                    .execute().get().getResponseBody();

            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> json = mapper.readValue(report, new TypeReference<>() {
        });

        return VirustotalSummary.builder()
                .harmless(getLastAnalysisStats(json, "harmless"))
                .malicious(getLastAnalysisStats(json, "malicious"))
                .suspicious(getLastAnalysisStats(json, "suspicious"))
                .undetected(getLastAnalysisStats(json, "undetected"))
                .timeout(getLastAnalysisStats(json, "timeout"))
                .build();
    }

    public int getLastAnalysisStats(Map<String, Object> jsonObject, String type) {
        return jsonObject.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals("data"))
                .map(it -> (Map<String, Object>) it.getValue())
                .map(it -> (Map<String, Object>) it.get("attributes"))
                .map(it -> (Map<String, Integer>) it.get("last_analysis_stats"))
                .toList()
                .stream()
                .map(element -> element.get(type))
                .toList()
                .get(0);
    }

}
