package hackathon.supervision.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hackathon.supervision.model.ScamPossibility;
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

    String api_key = "64e6e9ee72dea9ddacad6e7028ecddba85e7b3e1a5ff9132c1503e97c1ce0e8e";
    private UrlNormalizator normalizedUrl;
    private VirustotalReport virustotalReport;

    public VirustotalReport reportVirustotal(String url) throws IOException, ExecutionException, InterruptedException {
        normalizedUrl = new UrlNormalizator(url);
        try {
            return VirustotalReport.builder()
                    .domain(normalizedUrl.getDomain())
                    .scamPossibility(getRatio(url))
                    .virustotalSummary(getAnalysReportApi(getIdUrl(url)))
                    .build();
        } catch (Exception e) {

        }
        return null;

    }

    private ScamPossibility getRatio(String url) throws JsonProcessingException, ExecutionException, InterruptedException {
        VirustotalSummary x = getAnalysReportApi(getIdUrl(url));
        if (x.getMalicious() > 0) return ScamPossibility.VERY_HIGH;
        if (x.getSuspicious() > 0) return ScamPossibility.HIGH;
        return ScamPossibility.VERY_LOW;
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
