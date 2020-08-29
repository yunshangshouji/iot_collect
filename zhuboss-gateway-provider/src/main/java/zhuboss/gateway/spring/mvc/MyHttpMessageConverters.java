package zhuboss.gateway.spring.mvc;

import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter4;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.*;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
public class MyHttpMessageConverters {

    private static final List<String> formarts = new ArrayList<>(4);
    static{
        formarts.add("yyyy-MM");
        formarts.add("yyyy-MM-dd");
        formarts.add("yyyy-MM-dd hh:mm");
        formarts.add("yyyy-MM-dd hh:mm:ss");
    }
    /**
     * spring MVC 参数日期格式转换
     * @return
     */
	@Bean
	public Converter MyDateFormatConverter(){
        return  new Converter<String, Date>(){
            @Override
            public Date convert(String source) {
                String value = source.trim();
                if ("".equals(value)) {
                    return null;
                }
                if(source.matches("^\\d{4}-\\d{1,2}$")){
                    return parseDate(source, formarts.get(0));
                }else if(source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")){
                    return parseDate(source, formarts.get(1));
                }else if(source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")){
                    return parseDate(source, formarts.get(2));
                }else if(source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")){
                    return parseDate(source, formarts.get(3));
                }else {
                    throw new IllegalArgumentException("Invalid boolean value '" + source + "'");
                }
            }
            /**
             * 格式化日期
             * @param dateStr String 字符型日期
             * @param format String 格式
             * @return Date 日期
             */
            public  Date parseDate(String dateStr, String format) {
                Date date=null;
                try {
                    DateFormat dateFormat = new SimpleDateFormat(format);
                    date = dateFormat.parse(dateStr);
                } catch (Exception e) {

                }
                return date;
            }
        };
    }

	@Bean("httpMessageConverters")
	public HttpMessageConverters getHttpMessageConverters() throws Exception {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        List<MediaType> supportedMediaTypes = new ArrayList<>();

        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        stringHttpMessageConverter.setWriteAcceptCharset(false);  // see SPR-7316
        supportedMediaTypes.add(MediaType.TEXT_HTML);
        stringHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);

        messageConverters.add(new ByteArrayHttpMessageConverter());
        messageConverters.add(stringHttpMessageConverter);
        messageConverters.add(new ResourceHttpMessageConverter());
        messageConverters.add(new ResourceRegionHttpMessageConverter());
        messageConverters.add(new SourceHttpMessageConverter<>());
        messageConverters.add(new AllEncompassingFormHttpMessageConverter());

		//TEXT
//		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));

//        supportedMediaTypes.add(MediaType.TEXT_EVENT_STREAM);
//        supportedMediaTypes.add(MediaType.TEXT_HTML);
//        supportedMediaTypes.add(MediaType.TEXT_MARKDOWN);
//        supportedMediaTypes.add(MediaType.TEXT_PLAIN);
//        supportedMediaTypes.add(MediaType.TEXT_XML);
//        stringHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);

		//JSON
		FastJsonHttpMessageConverter4 fastJsonHttpMessageConverter4 = new FastJsonHttpMessageConverter4();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setCharset(Charset.forName("UTF-8"));
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
		fastJsonHttpMessageConverter4.setDefaultCharset(Charset.forName("UTF-8"));
        fastJsonHttpMessageConverter4.setFastJsonConfig(fastJsonConfig);
        // 避免：'Content-Type' cannot contain wildcard type '*'
		supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
        supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastJsonHttpMessageConverter4.setSupportedMediaTypes(supportedMediaTypes);
        messageConverters.add(fastJsonHttpMessageConverter4);

		//TODO 更多FastJson配置
//		fastJsonHttpMessageConverter4.setFastJsonConfig(fastJsonConfig);
		return new HttpMessageConverters(messageConverters);
	}
}
