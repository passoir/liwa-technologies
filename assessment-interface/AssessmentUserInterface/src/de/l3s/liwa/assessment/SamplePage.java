package de.l3s.liwa.assessment;

//*  Helper class to be able to break long urls on the assessment iterface. */
public class SamplePage {

    private String realUrl;
    private String dashedUrl;

    public SamplePage(String realUrl, String dashedUrl){
        this.realUrl = realUrl;
        this.dashedUrl = dashedUrl;
    }
    public String getRealUrl() {
        return realUrl;
    }
    public String getDashedUrl() {
        return dashedUrl;
    }
}