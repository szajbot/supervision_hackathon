package hackathon.supervision.controllers;

import hackathon.supervision.model.*;
import hackathon.supervision.services.IcannService;
import hackathon.supervision.services.SimilarityModuleService;
import hackathon.supervision.services.UrlValidatorService;
import hackathon.supervision.services.VirustotalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
public class ReportController {
    private final UrlValidatorService urlValidatorService;
    private final IcannService icannService;
    private final VirustotalService virustotalService;
    private final SimilarityModuleService similarityModuleService;

    @RequestMapping(value = "result/**", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RaportResult> result(HttpServletRequest request) throws IOException, ExecutionException, InterruptedException {
        String url = request.getRequestURI().split(request.getContextPath() + "/result/")[1];
        GeneralReport generalReport = GeneralReport.builder()
                .similarityModuleReport(similarityModuleService.reportSimilarityModule(url))
                .validatorReport(urlValidatorService.reportValidation(url))
                .icannReport(icannService.reportIcann(url))
                .virustotalReport(virustotalService.reportVirustotal(url))
                .build();

             if (generalReport.getSimilarityModuleReport()!=null && generalReport.getSimilarityModuleReport().getScamPossibility() == ScamPossibility.ZERO) generalReport.setValue(0);
        else if (generalReport.getSimilarityModuleReport()!=null && generalReport.getSimilarityModuleReport().getScamPossibility() == ScamPossibility.VERY_HIGH) generalReport.setValue(100);
        else if (generalReport.getVirustotalReport()!=null       && generalReport.getVirustotalReport().getScamPossibility() == ScamPossibility.VERY_HIGH) generalReport.setValue(100);
        else if (generalReport.getVirustotalReport()!=null       && generalReport.getVirustotalReport().getScamPossibility() == ScamPossibility.HIGH) generalReport.setValue(90);
        else if (generalReport.getIcannReport()!=null            && generalReport.getIcannReport().getScamPossibility() == ScamPossibility.VERY_HIGH) generalReport.setValue(80);
        else if (generalReport.getIcannReport()!=null            && generalReport.getIcannReport().getScamPossibility() == ScamPossibility.HIGH) generalReport.setValue(70);
        else if (generalReport.getValidatorReport()!=null        && generalReport.getValidatorReport().getScamPossibility() == ScamPossibility.VERY_HIGH) generalReport.setValue(60);
        else if (generalReport.getValidatorReport()!=null        && generalReport.getValidatorReport().getScamPossibility() == ScamPossibility.HIGH) generalReport.setValue(50);
        else if (generalReport.getValidatorReport()!=null        && generalReport.getValidatorReport().getScamPossibility() == ScamPossibility.MEDIUM) generalReport.setValue(40);
        else if (generalReport.getValidatorReport()!=null        && generalReport.getValidatorReport().getScamPossibility() == ScamPossibility.LOW) generalReport.setValue(30);
        else generalReport.setValue(20);

        return ResponseEntity.ok(RaportResult.builder().domain(url).value(generalReport.getValue()).build());
    }

    @RequestMapping(value = "report/**", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GeneralReport> report(HttpServletRequest request) throws IOException, ExecutionException, InterruptedException {
        String url = request.getRequestURI().split(request.getContextPath() + "/report/")[1];
        GeneralReport generalReport = GeneralReport.builder()
                .similarityModuleReport(similarityModuleService.reportSimilarityModule(url))
                .validatorReport(urlValidatorService.reportValidation(url))
                .icannReport(icannService.reportIcann(url))
                .virustotalReport(virustotalService.reportVirustotal(url))
                .build();
        return ResponseEntity.ok(generalReport);
    }

    @RequestMapping(value = "reportValidation/**", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ValidatorReport> reportValidation(HttpServletRequest request) throws IOException {
        String url = request.getRequestURI().split(request.getContextPath() + "/reportValidation/")[1];
        return ResponseEntity.ok(urlValidatorService.reportValidation(url));
    }

    @RequestMapping(value = "reportIcann/**", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IcannReport> reportIcann(HttpServletRequest request) throws IOException {
        String url = request.getRequestURI().split(request.getContextPath() + "/reportIcann/")[1];
        return ResponseEntity.ok(icannService.reportIcann(url));
    }

    @RequestMapping(value = "reportVirustotal/**", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VirustotalReport> reportVirustotal(HttpServletRequest request) throws IOException, ExecutionException, InterruptedException {
        String url = request.getRequestURI().split(request.getContextPath() + "/reportVirustotal/")[1];
        return ResponseEntity.ok(virustotalService.reportVirustotal(url));
    }

    @RequestMapping(value = "reportSimilarityModule/**", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimilarityModuleReport> reportSimilarityModule(HttpServletRequest request) throws IOException, ExecutionException, InterruptedException {
        String url = request.getRequestURI().split(request.getContextPath() + "/reportSimilarityModule/")[1];
        return ResponseEntity.ok(similarityModuleService.reportSimilarityModule(url));
    }

    @RequestMapping(value = "copiedSite/**", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CopiedSiteReport> copiedSite(HttpServletRequest request) throws IOException, ExecutionException, InterruptedException {
        String url = request.getRequestURI().split(request.getContextPath() + "/copiedSite/")[1];
        return ResponseEntity.ok(similarityModuleService.reportCopied(url));
    }
}
