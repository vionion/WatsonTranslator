
package com.ITKA352.translator;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    byte[] wav;
    private InputStream translation;

    @RequestMapping(path = "/getWav", method = RequestMethod.GET)
    public
    @ResponseBody
    byte[] getWav() {
        return this.wav;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String home(ModelMap model) {
        return "home";
    }

    @RequestMapping(method = RequestMethod.POST)
    public
    @ResponseBody
    String sendWAV(@RequestBody byte[] wav) throws IOException {
        this.wav = wav;
        String url = "https://langtrans.eu-gb.mybluemix.net/api/translate";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);

        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("link", "https://watson-translator.herokuapp.com/getWav"));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        //Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            translation = entity.getContent();
        }
        return "great!";
    }

    @RequestMapping(path = "getTranslationAudio", method = RequestMethod.GET)
    public void getTranslationAudio(HttpServletResponse servletResponse) throws IOException {
        byte[] bytes = getByteArrayFromInputStream(translation);
        IOUtils.copy(new ByteArrayInputStream(bytes), servletResponse.getOutputStream());
        servletResponse.flushBuffer();
    }


    private static byte[] getByteArrayFromInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }

}
