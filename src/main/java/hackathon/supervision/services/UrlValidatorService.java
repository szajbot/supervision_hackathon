package hackathon.supervision.services;

import hackathon.supervision.model.ScamPossibility;
import hackathon.supervision.model.UrlNormalizator;
import hackathon.supervision.model.ValidatorReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
@RequiredArgsConstructor
public class UrlValidatorService {

    private UrlNormalizator normalizedUrl;
    private ValidatorReport validatorReport;
    private ScamPossibility scamPossibility;

    public ValidatorReport reportValidation(String url) throws IOException {
        normalizedUrl = new UrlNormalizator(url);

        return ValidatorReport.builder()
                .domain(normalizedUrl.getDomain())
                .scamPossibility(getRatio())
                .isHttps(isHttps())
                .isUntypicalNumsInDomain(hasManyNumbers())
                .isCommonTopDomain(isCommonTopDomain())
                .numberOfSpecialChar(numberOfSpecialChar())
                .numberOfDigits(numberOfDigits())
                .domainLength(normalizedUrl.getDomain().length())
                .build();
    }

    private ScamPossibility getRatio() {
        int result = 1;
        if (numberOfSpecialChar() > 3) result++;
        if (numberOfDigits() > 3) result++;
        if (hasManyNumbers()) result++;
        if (normalizedUrl.getDomain().length() > 30) result++;
        if (!isCommonTopDomain()) result++;
        if (!isHttps()) result++;
        if (result >= 5) {
            return ScamPossibility.VERY_HIGH;
        }
        if (result == 4) {
            return ScamPossibility.HIGH;
        }
        if (result == 3) {
            return ScamPossibility.MEDIUM;
        }
        if (result == 2) {
            return ScamPossibility.LOW;
        }
        return ScamPossibility.VERY_LOW;
    }

    private boolean isHttps() {
        return normalizedUrl.getProtocol().equals("https://");
    }

    private int numberOfSpecialChar() {
        int counter = 0;
        for (char el : normalizedUrl.getDomain().toCharArray()) {
            if ((int) el >= 33 && (int) el <= 46) {
                counter++;
            }
        }
        return counter;
    }

    private int numberOfDigits() {
        int counter = 0;
        for (char el : normalizedUrl.getDomain().toCharArray()) {
            if ((int) el >= 48 && (int) el <= 57) {
                counter++;
            }
        }
        return counter;
    }

    public boolean isCommonTopDomain() {
        List<String> list = new ArrayList<>();
        if (!canFileRead(list)) {
            return false;
        }
        return list.contains(normalizedUrl.getTopLevelDomain());
    }

    private boolean canFileRead(List<String> list) {
        try {
            Scanner s = new Scanner(ResourceUtils.getFile("classpath:domains.txt"));
            while (s.hasNextLine()) {
                list.add(s.nextLine().replace(".", ""));
            }
            s.close();
        } catch (FileNotFoundException ex) {
            return false;
        }
        return true;
    }

    public boolean hasManyNumbers() {
        int counter = 0;
        for (char el : normalizedUrl.getDomain().toCharArray()) {
            if ((int) el >= 48 && (int) el <= 57) {
                counter++;
                if (counter > 3) {
                    return true;
                }
            } else counter = 0;
        }
        return false;
    }

}
