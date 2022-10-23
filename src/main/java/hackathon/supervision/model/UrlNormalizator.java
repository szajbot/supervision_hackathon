package hackathon.supervision.model;

import com.google.common.net.InternetDomainName;
import lombok.Getter;


@Getter
public class UrlNormalizator {
    private String protocol;
    private String basicUrl;
    private String domain;
    private String path;
    private String topLevelDomain;

    public UrlNormalizator(String url){
        this.protocol = recognizeProtocol(url);
        this.basicUrl = recognizeBasicUrl(url);
        this.domain = this.basicUrl.replace("www.", "");
        this.path = recognizePath(url);
        try{
            this.topLevelDomain = String.valueOf(InternetDomainName.from(this.domain).publicSuffix());
        }catch (Exception e){

        }

    }

    private String recognizePath(String url) {
        return url.replace(this.protocol, "").replace(this.basicUrl, "");
    }

    private String recognizeBasicUrl(String url) {
        var urlWithoutProtocol = url.replace(this.protocol, "");
        return urlWithoutProtocol.replaceAll("\\/.*", "");
    }

    private String recognizeProtocol(String url){
        if(url.startsWith("https://")){
            return "https://";
        }else if(url.startsWith("http://")){
            return "http://";
        }
        return "";
    }

}
