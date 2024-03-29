package com.ti.Skanboo.exceptions;

import com.ti.Skanboo.services.exceptions.DataBindingViolationException;
import com.ti.Skanboo.services.exceptions.ObjectNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.security.access.AccessDeniedException;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler implements AuthenticationFailureHandler {

    @Value("${server.error.include-exception}")
    private boolean printStackTrace;

    // @Override
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException methodArgumentNotValidException,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Erro de validacao. Verifique o log de erros para mais detalhes.");
        for (FieldError fieldError : methodArgumentNotValidException.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.unprocessableEntity().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtException(
            Exception exception,
            WebRequest request) {
        final String errorMessage = "Erro desconhecido ocorrido";
        log.error(errorMessage, exception);
        return buildErrorResponse(
                exception,
                errorMessage,
                HttpStatus.INTERNAL_SERVER_ERROR,
                request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataIntegrityViolationException(
            DataIntegrityViolationException dataIntegrityViolationException,
            WebRequest request) {
        String errorMessage = dataIntegrityViolationException.getMostSpecificCause().getMessage();
        log.error("Falha para salvar a entidade com os seguintes problemas: " + errorMessage,
                dataIntegrityViolationException);
        return buildErrorResponse(
                dataIntegrityViolationException,
                errorMessage,
                HttpStatus.CONFLICT,
                request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<Object> handleConstraintViolationException(
            ConstraintViolationException constraintViolationException,
            WebRequest request) {
        log.error("Falha ao validar o elemento", constraintViolationException);
        return buildErrorResponse(
                constraintViolationException,
                HttpStatus.UNPROCESSABLE_ENTITY,
                request);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleObjectNotFoundException(
            ObjectNotFoundException objectNotFoundException,
            WebRequest request) {
        log.error("Falha ao encontrar o objeto solicitado", objectNotFoundException);
        return buildErrorResponse(
                objectNotFoundException,
                HttpStatus.NOT_FOUND,
                request);
    }

    @ExceptionHandler(DataBindingViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataBindingViolationException(
            DataBindingViolationException dataBindingViolationException,
            WebRequest request) {
        log.error("Falha para salvar entidade com dados associados", dataBindingViolationException);
        return buildErrorResponse(
                dataBindingViolationException,
                HttpStatus.CONFLICT,
                request);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleAuthenticationException(
            AuthenticationException authenticationException,
            WebRequest request) {
        log.error("Erro de autenticacao", authenticationException);
        return buildErrorResponse(
                authenticationException,
                HttpStatus.UNAUTHORIZED,
                request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleAccessDeniedException(
            AccessDeniedException accessDeniedException,
            WebRequest request) {
        log.error("Erro de autenticacao", accessDeniedException);
        return buildErrorResponse(
                accessDeniedException,
                HttpStatus.FORBIDDEN,
                request);
    }

    @ExceptionHandler(AuthorizationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleAuthorizationException(
            AuthorizationException authorizationException,
            WebRequest request) {
        log.error("Erro de autenticacao", authorizationException);
        return buildErrorResponse(
                authorizationException,
                HttpStatus.FORBIDDEN,
                request);
    }

    @ExceptionHandler(PostCreationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handlePostCreationException(PostCreationException postCreationException,
            WebRequest request) {

        log.error("Erro na criacao da postagem", postCreationException);
        return buildErrorResponse(postCreationException, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException entityNotFoundException,
            WebRequest request) {

        log.error("Entidade nao encontrada", entityNotFoundException);
        return buildErrorResponse(entityNotFoundException, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(DuplicateOfferCreationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDuplicateOfferCreationException(
            DuplicateOfferCreationException duplicateOfferCreationException,
            WebRequest request) {

        log.error("Oferta duplicada", duplicateOfferCreationException);
        return buildErrorResponse(duplicateOfferCreationException, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(OfferCreationException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<Object> handleOfferCreationException(OfferCreationException offerCreationException,
            WebRequest request) {

        log.error("Falha ao criar a oferta", offerCreationException);
        return buildErrorResponse(offerCreationException, HttpStatus.METHOD_NOT_ALLOWED, request);
    }

    @ExceptionHandler(ExchangeCreationException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<Object> handleExchangeCreationException(ExchangeCreationException exchangeCreationException,
            WebRequest request) {

        log.error("Falha ao criar troca", exchangeCreationException);
        return buildErrorResponse(exchangeCreationException, HttpStatus.METHOD_NOT_ALLOWED, request);
    }

    @ExceptionHandler(ExchangeUpdateException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<Object> handleExchangeUpdateException(ExchangeUpdateException exchangeUpdateException,
            WebRequest request) {

        log.error("Falha ao atualizar a troca", exchangeUpdateException);
        return buildErrorResponse(exchangeUpdateException, HttpStatus.METHOD_NOT_ALLOWED, request);
    }

    @ExceptionHandler(ExchangeNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleExchangeNotFound(ExchangeNotFound exchangeNotFound,
            WebRequest request) {

        log.error("Falha ao encontrar troca", exchangeNotFound);
        return buildErrorResponse(exchangeNotFound, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(RatingNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleRatingNotFound(RatingNotFound ratingNotFound,
            WebRequest request) {

        log.error("Falha ao encontrar avaliacao", ratingNotFound);
        return buildErrorResponse(ratingNotFound, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(RatingCreationException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<Object> handleRatingCreationException(RatingCreationException ratingCreationException,
            WebRequest request) {

        log.error("Falha ao criar avaliacao", ratingCreationException);
        return buildErrorResponse(ratingCreationException, HttpStatus.METHOD_NOT_ALLOWED, request);
    }

    @ExceptionHandler(OfferUpdateException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<Object> handleOfferCreationException(OfferUpdateException offerUpdateException,
            WebRequest request) {

        log.error("Falha ao atualizar a oferta", offerUpdateException);
        return buildErrorResponse(offerUpdateException, HttpStatus.METHOD_NOT_ALLOWED, request);
    }

    private ResponseEntity<Object> buildErrorResponse(
            Exception exception,
            String message,
            HttpStatus httpStatus,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);
        if (this.printStackTrace) {
            errorResponse.setStackTrace(ExceptionUtils.getStackTrace(exception));
        }
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    private ResponseEntity<Object> buildErrorResponse(
            Exception exception,
            HttpStatus httpStatus,
            WebRequest request) {
        return buildErrorResponse(exception, exception.getMessage(), httpStatus, request);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        int status = HttpStatus.UNAUTHORIZED.value();
        response.setStatus(status);
        response.setContentType("application/json");
        ErrorResponse errorResponse = new ErrorResponse(status, "Email ou senha invalidos");
        response.getWriter().append(errorResponse.toJson());
    }
}