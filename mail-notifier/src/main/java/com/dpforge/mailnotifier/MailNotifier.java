package com.dpforge.mailnotifier;

import com.dpforge.mailnotifier.format.SourceCodeHtmlFormatter;
import com.dpforge.tellon.core.Changes;
import com.dpforge.tellon.core.ProjectItem;
import com.dpforge.tellon.core.notifier.ChangesNotifier;
import com.dpforge.tellon.core.notifier.ChangesNotifierException;
import com.dpforge.tellon.core.notifier.ProjectInfo;
import com.dpforge.tellon.core.parser.AnnotatedBlock;
import com.dpforge.tellon.core.parser.WatcherList;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.util.ConfigLoader;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MailNotifier implements ChangesNotifier {

    private static final String PREFIX = "mailto:";

    private Mailer mailer;
    private SourceCodeHtmlFormatter codeFormatter;
    private ProjectInfo currentPorject;

    @Override
    public String getName() {
        return "mail-notifier";
    }

    @Override
    public String getDescription() {
        return "Send notifications about source code changes using e-mail. Use 'mailto:' prefix before e-mail address. " +
                "For example @NotifyChanges(\"mailto:changes@example.com\")";
    }

    @Override
    public void init() throws ChangesNotifierException {
        ConfigLoader.loadProperties(new File(".", "mail-notifier.properties"), false);
        mailer = new Mailer();

        try {
            codeFormatter = SourceCodeHtmlFormatter.create();
        } catch (IOException e) {
            throw new ChangesNotifierException(e);
        }
    }

    @Override
    public void onStartProject(ProjectInfo projectInfo) {
        currentPorject = projectInfo;
    }

    @Override
    public void onFinishedProject() {

    }

    @Override
    public void notifyChanges(ProjectItem item, Changes changes) {
        final List<String> watchers = new ArrayList<>();
        final StringBuilder body = new StringBuilder();
        final MailList mailList = new MailList();

        if (changes.hasUpdated()) {
            body.append("The following source code block(s) has been <b>CHANGED</b>:<br/>");
            for (Changes.Update update : changes.getUpdated()) {
                body.append("Was:<br/>");
                body.append(codeFormatter.getHtml(update.getOldBlock().getBody()));
                body.append("<br/>Now:<br/>");
                body.append(codeFormatter.getHtml(update.getNewBlock().getBody()));

                extractMailWatchers(watchers, update.getOldBlock().getWatchers());
                extractMailWatchers(watchers, update.getNewBlock().getWatchers());
            }

            for (String watcher : watchers) {
                mailList.add(watcher, body.toString());
            }
            watchers.clear();
            body.setLength(0);
        }

        if (changes.hasAdded()) {
            body.append("The following source code block(s) has been <b>ADDED</b>:<br/>");
            for (AnnotatedBlock block : changes.getAdded()) {
                body.append(codeFormatter.getHtml(block.getBody())).append("<br/>");
                extractMailWatchers(watchers, block.getWatchers());
            }

            for (String watcher : watchers) {
                mailList.add(watcher, body.toString());
            }
            watchers.clear();
            body.setLength(0);
        }

        if (changes.hasDeleted()) {
            body.append("The following source code block(s) has been <b>DELETED</b>:<br/>");
            for (AnnotatedBlock block : changes.getDeleted()) {
                body.append(codeFormatter.getHtml(block.getBody())).append("<br/>");
                extractMailWatchers(watchers, block.getWatchers());
            }

            for (String watcher : watchers) {
                mailList.add(watcher, body.toString());
            }
            watchers.clear();
            body.setLength(0);
        }

        for (MailData mailData : mailList) {
            sendEmail(mailData.address, "Some code was changed in " + currentPorject.getName(), mailData.body);
        }
    }

    @Override
    public void notifyItemAdded(ProjectItem item, Changes changes) {
        final List<String> watchers = new ArrayList<>();
        final StringBuilder bodyBuilder = new StringBuilder();

        bodyBuilder.append("The following source code block(s) has been <b>ADDED</b>:<br/>");
        for (AnnotatedBlock block : changes.getAdded()) {
            bodyBuilder.append(codeFormatter.getHtml(block.getBody())).append("<br/>");
            extractMailWatchers(watchers, block.getWatchers());
        }

        sendEmail(watchers, "Some code was added in " + currentPorject.getName(), bodyBuilder.toString());
    }

    @Override
    public void notifyItemDeleted(ProjectItem item, Changes changes) {
        final List<String> watchers = new ArrayList<>();
        final StringBuilder bodyBuilder = new StringBuilder();

        bodyBuilder.append("The following source code block(s) has been <b>DELETED</b>:<br/>");
        for (AnnotatedBlock block : changes.getDeleted()) {
            bodyBuilder.append(codeFormatter.getHtml(block.getBody())).append("<br/>");
            extractMailWatchers(watchers, block.getWatchers());
        }

        sendEmail(watchers, "Some code was deleted in " + currentPorject.getName(), bodyBuilder.toString());
    }

    private void sendEmail(String watcher, String subject, String mailBody) {
        sendEmail(Collections.singletonList(watcher), subject, mailBody);
    }

    private void sendEmail(List<String> watchers, String subject, String mailBody) {
        final EmailBuilder emailBuilder = new EmailBuilder()
                .from("Tellon", ConfigLoader.getProperty(ConfigLoader.Property.SMTP_USERNAME))
                .subject(subject)
                .textHTML(mailBody);
        for (String watcher : watchers) {
            emailBuilder.to(watcher);
        }
        mailer.sendMail(emailBuilder.build(), false);
    }

    private static void extractMailWatchers(final List<String> result, final WatcherList watchers) {
        for (int i = 0; i < watchers.size(); i++) {
            final String watcher = watchers.get(i);
            if (watcher.startsWith(PREFIX)) {
                final String email = watcher.substring(PREFIX.length());
                if (!result.contains(email)) {
                    result.add(email);
                }
            }
        }
    }

    private static class MailData {
        private final String address;
        private final String body;

        public MailData(String address, String body) {
            this.address = address;
            this.body = body;
        }
    }

    private static class MailList implements Iterable<MailData> {
        private final Map<String, StringBuilder> addressToBody = new HashMap<>();

        void add(final String address, final String text) {
            StringBuilder body = addressToBody.get(address);
            if (body == null) {
                body = new StringBuilder();
            }
            body.append(text);
            addressToBody.put(address, body);
        }

        @Override
        public Iterator<MailData> iterator() {
            final Iterator<Map.Entry<String, StringBuilder>> mapIterator = addressToBody.entrySet().iterator();
            return new Iterator<MailData>() {
                @Override
                public boolean hasNext() {
                    return mapIterator.hasNext();
                }

                @Override
                public MailData next() {
                    final Map.Entry<String, StringBuilder> entry = mapIterator.next();
                    return new MailData(entry.getKey(), entry.getValue().toString());
                }
            };
        }
    }
}
