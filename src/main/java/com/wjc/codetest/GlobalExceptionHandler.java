package com.wjc.codetest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
/**
 * 1. 문제: 예외에 대한 처리가 누락되는 Controller가 발생할 가능성
 * 2. 원인: @ControllerAdvice 어노테이션에 basePackage scope가 지정되어 있음
 * 3. 개선안: 해당 클래스의 명칭이나 프로젝트 내에서의 패키지 위치로 봤을 때,
 *          해당 ExceptionHandler는 프로젝트 전반에 걸쳐 공통적으로 발생할 수 있는 예외에 대한 처리를 담당하는 클래스로 보입니다.
 *
 *          그러나 @ControllerAdvice 어노테이션에 value(basePackage) 속성이 지정되어 있으면, 해당하는 Controller에 대해서만 동작하게 됩니다.
 *          현업에서 프로젝트를 진행하다 보면, 규모에 따라 적게는 수십 많게는 수백개 까지도 Controller가 만들어지게 되는데
 *          현실적으로 모든 Controller를 만들 때마다 직접 작성하는 것은 번거롭고, 개발자의 실수에 의해 누락되거나 오타로 인하여 정상적인 적용이 되지 않을 수 있습니다.
 *          (또한 여러 개발자가 동시에 작업하고 Git을 통한 merge를 진행할 때도 매번 충돌이 일어날 수 있는 부분입니다.)
 *
 *          따라서, 프로젝트 전반에 걸쳐 처리되어야 하는 부분이기에 @ControllerAdvice 어노테이션의 속성을 작성하지 않도록 아래와 같이 변경하고
 *          만약 하나의 도메인에만 적용되어야 하는 ExceptionHandler 라면, com.wjc.codetest.product.exception 등 도메인 하위에 패키지를 생성하여
 *          ExceptionHandler를 만들고, basePackage를 지정하여 사용하는 것이 좋을 것 같습니다.
 *
 *          * [변경] @ControllerAdvice(value = {"com.wjc.codetest.product.controller"}) -> @ControllerAdvice
 */
@ControllerAdvice(value = {"com.wjc.codetest.product.controller"})
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> runTimeException(Exception e) {
        /**
         * 1. 문제: 발생한 예외에 대한 추적 불가능
         * 2. 원인: 예외에 대한 로그에 추적이 가능한 정보(클래스, 메서드, 라인 등)가 없음
         * 3. 개선안: 현재 RuntimeException이 발생했을 때 로그에 남기는 정보는 아래와 같습니다.
         *          1. HTTP Response Status
         *          2. 오류 타입(runtimeException 고정)
         *          3. exception message
         *
         *          위와 같은 정보들로는 어떤 포인트에서 예외가 발생했는지, 구체적으로 어떤 예외가 발생했는지
         *          (RuntimeException을 상속받은 예외들도 해당 메서드가 처리하게 되는데, runtimeException으로만 남게 됩니다.)
         *          알 수가 없습니다.
         *
         *          따라서 저는 아래와 같은 정보들을 추가로 로깅해야 한다고 생각합니다.
         *          1. 오류가 발생한 클래스의 정보(이름)
         *          2. 오류가 발생한 메서드의 정보(이름)
         *          3. 오류가 발생한 포인트의 라인 정보
         *
         *          그리고 아래 로그 내용 중 errorType에 현재 runtimeException으로만 로그를 남기고 있는데,
         *          e.getClass().getSimpleName() 등을 통해 구체적인 예외에 대한 정보도 함께 남기는 것이 좋을 것 같습니다.
         */
        log.error("status :: {}, errorType :: {}, errorCause :: {}",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "runtimeException",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
