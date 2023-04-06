package http.util;

import responsefactory.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class HttpRequestUtils {
    /**
     요청 메시지 해석에 관련된 도구들
     **/
    private static final Logger log = Logger.getLogger(HttpRequestUtils.class.getName());

    /**
     * 쿼리스트링 해석 및 분리해서 반환하는 함수
     * @param queryString
     * @return paramMap에 queryString을 key, value로 분리해서 map 형태로 반환
     */
    public Map<String,String> parseQueryString(String queryString){
        Map<String,String> paramMap = new HashMap<>();
        if(!queryString.isEmpty()){
            String[] parts=queryString.split("&");
            for(String part:parts) {
                String[] keyValue = part.split("=");
                String key=keyValue[0]; //key는 아이디, 비번, 이름, 이메일 주소 "타입 정보!"
                log.log(Level.INFO,key);
                String value = keyValue.length > 1 ? keyValue[1] : ""; //value는 해당 타입에 대한 실제 데이터 정보!
                //{key: value, key: value, key: value, key: value} 형식!
                try{
                    value= URLDecoder.decode(value,"UTF-8");
                    log.log(Level.INFO,value);
                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }
                paramMap.put(key,value);
            }
        }
        return paramMap;
    }

    /**
     * 강의로부터 받은 코드 -> 아직 이해가 잘 안됨
     */
    public static Map<String, String> parseQueryParameter(String queryString) {
        try {
            String[] queryStrings = queryString.split("&");

            return Arrays.stream(queryStrings)
                    .map(q -> q.split("="))
                    .collect(Collectors.toMap(queries -> queries[0], queries -> queries[1]));
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
