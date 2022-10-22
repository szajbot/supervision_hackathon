package hackathon.supervision.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hackathon.supervision.model.IcannReport;
import hackathon.supervision.model.UrlNormalizator;
import hackathon.supervision.model.UrlRatio;
import hackathon.supervision.model.ValidatorReport;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IcannService {

    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String API_DOMAIN = "https://rdap.verisign.com/com/v1/domain/";
    private UrlNormalizator normalizedUrl;

    public IcannReport reportIcann(String url) throws IOException{
        normalizedUrl = new UrlNormalizator(url);

        return IcannReport.builder()
                .domain(normalizedUrl.getDomain())
                .urlRatio(getRatio(url))
                .lifeSpan(getLifeSpan(url))
                .registrationDate(getRegistrationDate(url).toString())
                .expirationDate(getExpirationDate(url).toString())
                .build();
    }

    public UrlRatio getRatio(String url) throws IOException {
        long years = getLifeSpan(url);
        if (years == 1) {
            return UrlRatio.VERY_HIGH;
        } else if (years == 2) {
            return UrlRatio.HIGH;
        } else if (years == 3) {
            return UrlRatio.LOW;
        }
        return UrlRatio.VERY_LOW;
    }

    long getLifeSpan(String url) throws IOException {
        LocalDateTime registrationDate = getRegistrationDate(url);
        LocalDateTime expirationDate = getExpirationDate(url);

        return ChronoUnit.YEARS.between(registrationDate, expirationDate);
    }

    private LocalDateTime getRegistrationDate(String url) throws IOException {
        Map<String, Object> responseMap = readFromApi();
        String date = getDateOfType(responseMap, "registration");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        return LocalDateTime.parse(date, formatter);
    }

    private LocalDateTime getExpirationDate(String url) throws IOException {
        Map<String, Object> responseMap = readFromApi();
        String date = getDateOfType(responseMap, "expiration");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        return LocalDateTime.parse(date, formatter);
    }

    private String getDateOfType(Map<String, Object> responseMap, String type) {
        return responseMap.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals("events"))
                .map(it -> (List<Map<String, Object>>) it.getValue())
                .flatMap(List::stream)
                .filter(it -> it.get("eventAction").equals(type))
                .map(it -> (String) it.get("eventDate"))
                .collect(Collectors.joining());
    }

    private Map<String, Object> readFromApi() throws IOException {

        HttpGet httpGet = new HttpGet(API_DOMAIN + normalizedUrl.getDomain());

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(httpGet);

        var jsonResponse = EntityUtils.toString(response.getEntity());

        return new ObjectMapper().readValue(jsonResponse, new TypeReference<>() {});
    }
}
