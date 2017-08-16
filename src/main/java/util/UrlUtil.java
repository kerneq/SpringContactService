package util;

import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

/**
 * Created by iters on 8/17/17.
 */
public class UrlUtil {
    public static String encodeUrlPathSegment(String pathSegment,
                                              HttpServletRequest request) {
        String enc = request.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }

        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return pathSegment;
    }
}
