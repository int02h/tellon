package com.dpforge.mailnotifier;

import com.dpforge.tellon.core.notifier.ChangesNotifier;
import com.dpforge.tellon.core.notifier.ProjectNotifierException;
import com.dpforge.tellon.core.notifier.ProjectNotifier;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.util.ConfigLoader;

import java.io.File;
import java.io.IOException;

public class MailNotifier implements ProjectNotifier {

    static final String PREFIX = "mailto:";

    private Mailer mailer;
    private MailChangesNotifier changesNotifier;

    @Override
    public String getName() {
        return "mail-notifier";
    }

    @Override
    public String getDescription() {
        return "Send e-mail when source code changes. Use 'mailto:' prefix before e-mail address. " +
                "For example: @NotifyChanges(\"mailto:changes@example.com\").";
    }

    @Override
    public void init() throws ProjectNotifierException {
        ConfigLoader.loadProperties(new File(".", "mail-notifier.properties"), false);
        mailer = new Mailer();

        try {
            changesNotifier = MailChangesNotifier.create(mailer);
        } catch (IOException e) {
            throw new ProjectNotifierException(e);
        }
    }

    @Override
    public void reportError(String watcher, String errorMessage) {
        if (!watcher.startsWith(PREFIX)) {
            return;
        }

        final String email = watcher.substring(PREFIX.length());
        final EmailBuilder emailBuilder = new EmailBuilder()
                .from("Tellon", ConfigLoader.getProperty(ConfigLoader.Property.SMTP_USERNAME))
                .subject("Error occurred in Tellon")
                .text(errorMessage)
                .to(email);
        mailer.sendMail(emailBuilder.build(), false);
    }

    @Override
    public ChangesNotifier getChangesNotifier() {
        return changesNotifier;
    }
}
