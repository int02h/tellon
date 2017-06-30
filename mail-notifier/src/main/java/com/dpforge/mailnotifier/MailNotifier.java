package com.dpforge.mailnotifier;

import com.dpforge.tellon.core.notifier.ChangesNotifier;
import com.dpforge.tellon.core.notifier.ChangesNotifierException;
import com.dpforge.tellon.core.notifier.ProjectNotifier;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.util.ConfigLoader;

import java.io.File;
import java.io.IOException;

public class MailNotifier implements ProjectNotifier {

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
    public void init() throws ChangesNotifierException {
        ConfigLoader.loadProperties(new File(".", "mail-notifier.properties"), false);
        mailer = new Mailer();

        try {
            changesNotifier = MailChangesNotifier.create(mailer);
        } catch (IOException e) {
            throw new ChangesNotifierException(e);
        }
    }

    @Override
    public ChangesNotifier getChangesNotifier() {
        return changesNotifier;
    }
}
