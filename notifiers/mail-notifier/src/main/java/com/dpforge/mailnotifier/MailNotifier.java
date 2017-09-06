package com.dpforge.mailnotifier;

import com.dpforge.tellon.core.notifier.ChangesNotifier;
import com.dpforge.tellon.core.notifier.ProjectNotifierException;
import com.dpforge.tellon.core.notifier.ProjectNotifier;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.util.ConfigLoader;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

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
        File codeLocation = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
        File propertyFile = new File(codeLocation.getParentFile(), "mail-notifier.properties");
        ConfigLoader.loadProperties(propertyFile, false);
        mailer = new Mailer();

        try {
            changesNotifier = MailChangesNotifier.create(mailer);
        } catch (IOException e) {
            throw new ProjectNotifierException(e);
        }
    }

    @Override
    public void reportError(Collection<String> watchers, String errorMessage) {
        for (String watcher : watchers) {
            reportError(watcher, errorMessage);
        }
    }

    @Override
    public ChangesNotifier getChangesNotifier() {
        return changesNotifier;
    }

    private void reportError(final String watcher, final String errorMessage) {
        if (!watcher.startsWith(PREFIX)) {
            return;
        }

        final String email = watcher.substring(PREFIX.length());
        final String username = ConfigLoader.getProperty(ConfigLoader.Property.SMTP_USERNAME);
        final EmailBuilder emailBuilder = new EmailBuilder()
                .from("Tellon", username)
                .subject("Error occurred in Tellon")
                .text(errorMessage)
                .to(email);
        mailer.sendMail(emailBuilder.build(), false);
    }
}
