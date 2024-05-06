package com.backbase.accesscontrol.util;

import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.buildingblocks.presentation.errors.Error;
import com.backbase.buildingblocks.presentation.errors.ForbiddenException;
import com.backbase.buildingblocks.presentation.errors.InternalServerErrorException;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import java.util.Collections;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionUtil {

    static final String BAD_REQUEST_MESSAGE = "Bad Request";
    private static final String FORBIDDEN_MESSAGE = "Forbidden";
    private static final String NOT_FOUND_MESSAGE = "Not Found";

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionUtil.class);

    /**
     * Returns SDK Bad Request Exception.
     *
     * @param errorMessage - string containing the error message
     * @param errorCode    - string containing the error code
     * @return {@link BadRequestException}
     */
    public static BadRequestException getBadRequestException(String errorMessage, String errorCode) {

        LOGGER.warn("Bad request exception with message {} and code {}", errorMessage, errorCode);
        return new BadRequestException()
            .withMessage(BAD_REQUEST_MESSAGE)
            .withErrors(Collections.singletonList(new Error()
                .withMessage(errorMessage)
                .withKey(errorCode)));
    }

    /**
     * Returns SDK Not Found Exception.
     *
     * @param errorMessage - string containing the error message
     * @param errorCode    - string containing the error code
     * @return {@link NotFoundException}
     */
    public static NotFoundException getNotFoundException(String errorMessage, String errorCode) {
        LOGGER.warn("Not found exception with message {} and code {}", errorMessage, errorCode);
        return new NotFoundException()
            .withMessage(NOT_FOUND_MESSAGE)
            .withErrors(Collections.singletonList(new Error()
                .withMessage(errorMessage)
                .withKey(errorCode)));
    }

    /**
     * Returns SDK Forbidden Request Exception.
     *
     * @param errorMessage - string containing the error message
     * @param errorCode    - string containing the error code
     * @return {@link ForbiddenException}
     */
    public static ForbiddenException getForbiddenException(String errorMessage, String errorCode) {
        LOGGER.warn("Forbidden exception with message {} and code {}", errorMessage, errorCode);
        return new ForbiddenException()
            .withMessage(FORBIDDEN_MESSAGE)
            .withErrors(Collections.singletonList(new Error()
                .withMessage(errorMessage)
                .withKey(errorCode)));
    }

    /**
     * Returns SDK Internal Server Error Exception.
     *
     * @param errorMessage - string containing the error message
     * @return {@link InternalServerErrorException}
     */
    public static InternalServerErrorException getInternalServerErrorException(String errorMessage) {
        LOGGER.warn("Internal server error exception with message {}", errorMessage);
        return new InternalServerErrorException()
            .withMessage(errorMessage);
    }
}
