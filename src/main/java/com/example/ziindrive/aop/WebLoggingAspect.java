package com.example.ziindrive.aop;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Aspect
@Component
public class WebLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(WebLoggingAspect.class);

    private static long start;

    // 뷰 로딩시 포인트컷
    @Pointcut("within(@org.springframework.stereotype.Controller *)")
    private void viewControllerLayer() {
    }

    // api 호출시 포인트컷
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    private void apiControllerLayer() {
    }

    // 메소드 실행 전: 요청 메타데이터 기록하기
    @Before("apiControllerLayer() || viewControllerLayer()")
    public void logRequest(JoinPoint jp) {

        System.out.println("AOP HIT");

        // 요청 객체 및 메소드 정보
        HttpServletRequest request = currentRequest();
        if (request == null) {
            log.info("REQUEST: null");
            return;
        }

        MethodSignature sig = (MethodSignature) jp.getSignature();
        String classMethod = sig.getDeclaringType().getSimpleName() + "." + sig.getName();

        // HTTP 메소드명, uri, 쿼리문 받아오기
        String httpMethod = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();

        // 요청 파라미터: 쿼리스트링/폼필드 기준 (raw JSON body는 필터에서 처리 권장)
        String params = flattenParams(request.getParameterMap());

        // 메소드 인자 (민감/대용량 마스킹)
        String argsStr = Arrays.stream(jp.getArgs()).map(WebLoggingAspect::safeToString)
                .collect(Collectors.joining(", "));

        log.info("REQUEST: [{}] {}{} | handler={} params={} args={}",
                httpMethod,
                uri,
                (query == null || query.isEmpty() ? "" : "?" + query),
                classMethod,
                params,
                argsStr);

        start = System.nanoTime();
    }

    // view 로드 성공 시: 응답 메타데이터 기록하기
    @AfterReturning(pointcut = "viewControllerLayer()", returning = "result")
    public void logViewResponse(JoinPoint jp, Object result) {

        // 실행 시간 측정
        long tookMs = (System.nanoTime() - start) / 1_000_000;
        log.info("실행 시간: {} ms", tookMs);

        HttpServletResponse response = currentResponse();
        int status = response != null ? response.getStatus() : -1;

        MethodSignature sig = (MethodSignature) jp.getSignature();
        String classMethod = sig.getDeclaringType().getSimpleName();

        String viewDesc;
        if (result == null) {
            viewDesc = "null/void";

        } else if (result instanceof String) {
            viewDesc = "view=" + (String) result;

        } else {
            viewDesc = String.valueOf(result);
        }

        log.info("VIEW: handler={} status={} {}",
                classMethod,
                status,
                viewDesc);
    }

    // view 로드 실패 시
    @AfterThrowing(pointcut = "viewControllerLayer()", throwing = "ex")
    public void logViewException(JoinPoint jp, Throwable ex) {

        HttpServletResponse response = currentResponse();
        int status = response != null ? response.getStatus() : -1;

        MethodSignature sig = (MethodSignature) jp.getSignature();
        String classMethod = sig.getDeclaringType().getSimpleName();

        log.warn("ERROR: handler={} status={} threw={}: {}",
                classMethod,
                status,
                ex.getClass().getSimpleName(),
                ex.getMessage());
    }

    /*********************************************************************/

    // api 응답 성공 시: 응답 메타데이터 기록하기
    @AfterReturning(pointcut = "apiControllerLayer()", returning = "result")
    public void logResponse(JoinPoint jp, Object result) {

        // 실행 시간 측정
        long tookMs = (System.nanoTime() - start) / 1_000_000;
        log.info("실행 시간: {} ms", tookMs);

        // 응답 객체 및 정보 받기
        HttpServletResponse response = currentResponse();

        MethodSignature sig = (MethodSignature) jp.getSignature();
        String classMethod = sig.getDeclaringType().getSimpleName() + "." + sig.getName();

        int status = response != null ? response.getStatus() : -1;

        log.info("RESPONSE: handler={} status={} args={}",
                classMethod,
                status,
                safeToString(result));
    }

    // api 응답 오류 발생시
    @AfterThrowing(pointcut = "apiControllerLayer()", throwing = "ex")
    public void logException(JoinPoint jp, Throwable ex) {

        // 응답 객체 및 정보 받기
        HttpServletResponse response = currentResponse();

        MethodSignature sig = (MethodSignature) jp.getSignature();
        String classMethod = sig.getDeclaringType().getSimpleName() + "." + sig.getName();

        int status = response != null ? response.getStatus() : -1;

        log.warn("ERROR: handler={} status={} threw={}: {}",
                classMethod,
                status,
                ex.getClass().getSimpleName(),
                ex.getMessage());
    }

    /* ----------------------------- 유틸함수 ---------------------------------- */

    // 요청 객체 받아오기
    private static HttpServletRequest currentRequest() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();

        if (attrs instanceof ServletRequestAttributes sra) {
            return sra.getRequest();
        }
        return null;
    }

    // 요청에 대한 응답 객체 받아오기
    private static HttpServletResponse currentResponse() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();

        if (attrs instanceof ServletRequestAttributes sra) {
            return sra.getResponse();
        }
        return null;
    }

    // JSON 바디 파라미터 문자열로 변환
    private static String flattenParams(Map<String, String[]> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return "{}";
        }
        return paramMap.entrySet().stream()
                .map(e -> e.getKey() + "=" + safeToString(e.getValue()))
                .collect(Collectors.joining(", ", "{", "}"));
    }

    // 대용량/민감/바이너리 파라미터에 대한 안전한 문자열화
    private static String safeToString(Object o) {

        if (o == null) {
            return "null";
        }

        // 파일, 바이트배열, 스트림 등은 이름, 사이즈 등만 표시
        if (o instanceof org.springframework.web.multipart.MultipartFile file) {
            return "MultiPartFile(name=" + file.getOriginalFilename() + ", size=" + file.getSize() + ")";

        } else if (o instanceof byte[] bytes) {
            return "byte[" + bytes.length + "]";

        } else if (o instanceof java.io.InputStream) {
            return "InputStream";

        } else if (o instanceof java.io.Reader) {
            return "Reader";
        }

        // 문자열은 길이 제한
        if (o instanceof CharSequence cs) {
            String s = cs.toString();
            if (s.length() > 300)
                return s.substring(0, 300) + "...(truncated)";
            return s;
        }

        // 기본: toString
        String s = String.valueOf(o);
        if (s.length() > 300) {
            return s.substring(0, 300) + "...(truncated)";
        }
        return s;
    }
}
