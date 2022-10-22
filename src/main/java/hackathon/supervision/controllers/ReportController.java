package hackathon.supervision.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import hackathon.supervision.services.UrlValidatorService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ReportController {
    private final UrlValidatorService urlValidatorService;

    @RequestMapping(value = "reportValidation/**", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> report(HttpServletRequest request) throws IOException {
        String url = request.getRequestURI().split(request.getContextPath() + "/reportValidation/")[1];
        return ResponseEntity.ok(urlValidatorService.reportValidation(url));
    }
}
