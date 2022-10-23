package hackathon.supervision.services;

import hackathon.supervision.model.*;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SimilarityModuleService {

    private UrlNormalizator normalizedUrl;
    private List<String> reportedDomains;
    private List<String> verifiedDomains;

    public SimilarityModuleReport reportSimilarityModule(String url) {
        normalizedUrl = new UrlNormalizator(url);
        reportedDomains = loadUrlList("src/main/resources/reportedDomains.txt");
        verifiedDomains = loadUrlList("src/main/resources/verifiedDomains.txt");
//        reportedDomains = loadUrlList("src/main/resources/reportedDomainsWithoutTest.txt");
//        verifiedDomains = loadUrlList("src/main/resources/verifiedDomainsWithoutTest.txt");

        SimilarityModuleReport similarityModuleReport = SimilarityModuleReport.builder()
                .domain(normalizedUrl.getDomain())
                .numOfSimilarities(getNumberOfSimilarities())
                .verifiedDatabaseGrade(getScale(normalizedUrl.getDomain(), verifiedDomains))
                .reportedDatabaseGrade(getScale(normalizedUrl.getDomain(), reportedDomains))
                .build();

        similarityModuleReport.setScamPossibility(getRatio(similarityModuleReport));
        return similarityModuleReport;
    }

    public CopiedSiteReport reportCopied(String address) throws IOException {
        normalizedUrl = new UrlNormalizator(address);

        URI url = null;
        Elements links = null;
        try {
            url = new URI(address);
            links = Jsoup.connect(url.toString()).get().select("a[href]");
        } catch (Exception e) {
            return null;
        }

        Map<String, Integer> refMap = new HashMap<>();
        for (Element link : links) {
            try {
                URI tmp = new URI(link.attr("abs:href"));
                refMap.merge(tmp.getHost(), 1, Integer::sum);
            } catch (URISyntaxException e) {
            }
        }

        try {
            String result = refMap.entrySet().stream().max((e1, e2) -> e1.getValue() > e2.getValue() ? 1 : -1).get().getKey();

            List<String> test = new ArrayList<>();
            test.add(new UrlNormalizator(result).getDomain());

            return CopiedSiteReport.builder()
                    .domain(normalizedUrl.getDomain())
                    .result(getScale(normalizedUrl.getDomain(), test))
                    .build();
        }
        catch (Exception e){
            return null;
        }
    }

        private ScamPossibility getRatio (SimilarityModuleReport report){
            if (report.getVerifiedDatabaseGrade() == SimilarityScale.CONFIDENT) return ScamPossibility.ZERO;
            if (report.getVerifiedDatabaseGrade() == SimilarityScale.HIGH || report.getReportedDatabaseGrade() == SimilarityScale.CONFIDENT)
                return ScamPossibility.VERY_HIGH;
            if (report.getNumOfSimilarities() > 10) return ScamPossibility.HIGH;
            if (report.getNumOfSimilarities() > 5) return ScamPossibility.MEDIUM;
            if (report.getReportedDatabaseGrade().getGrade() > 70) return ScamPossibility.LOW;
            return ScamPossibility.VERY_LOW;
        }

        private static SimilarityScale getScale (String url, List < String > domains){
            double similarity = 0.5;
            do {
                similarity += 0.01;
                domains = similarities(url, similarity, domains);
            } while (!domains.isEmpty());
            if (similarity >= 1.0) {
                return SimilarityScale.CONFIDENT;
            }
            if (similarity >= 0.75) {
                return SimilarityScale.HIGH;
            }
            if (similarity >= 0.5) {
                return SimilarityScale.MEDIUM;
            }
            return SimilarityScale.LOW;
        }

        private int getNumberOfSimilarities () {
            return similarities(normalizedUrl.getDomain(), 0.75, reportedDomains).size();
        }

        public List<String> loadUrlList (String path){
            List<String> list = new ArrayList<>();

            try {
                Scanner s = new Scanner(new File(path));
                while (s.hasNextLine()) {
                    list.add(new UrlNormalizator(s.nextLine()).getDomain());
                }
                s.close();
                return list;
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            return list;
        }

        public static List<String> similarities (String url,double similarityRate, List<String > list){
            List<String> listDetected = new ArrayList<>();
            for (String e : list) {

                if (rateSimilarity(url, e) >= similarityRate) {
                    listDetected.add(e);
                }
            }
            return listDetected;
        }

        public static int getLevenshteinDistance (String X, String Y){
            int m = X.length();
            int n = Y.length();

            int[][] T = new int[m + 1][n + 1];
            for (int i = 1; i <= m; i++) {
                T[i][0] = i;
            }
            for (int j = 1; j <= n; j++) {
                T[0][j] = j;
            }

            int cost;
            for (int i = 1; i <= m; i++) {
                for (int j = 1; j <= n; j++) {
                    cost = X.charAt(i - 1) == Y.charAt(j - 1) ? 0 : 1;
                    T[i][j] = Integer.min(Integer.min(T[i - 1][j] + 1, T[i][j - 1] + 1),
                            T[i - 1][j - 1] + cost);
                }
            }

            return T[m][n];
        }

        public static double rateSimilarity (String x, String y){
            if (x == null || y == null) {
                throw new IllegalArgumentException("Strings must not be null");
            }

            double maxLength = Double.max(x.length(), y.length());
            if (maxLength > 0) {
                // optionally ignore case if needed
                return (maxLength - getLevenshteinDistance(x, y)) / maxLength;
            }
            return 1.0;
        }
    }
