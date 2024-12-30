package com.icm.telemetria_peru_api.repositories;

import java.io.File;

public interface IEmailRepository {
    void sendEmail(String[] toUser, String subject, String message);

    void sendEmailWithFile(String[] toUser, String subject, String message, File file);
}
