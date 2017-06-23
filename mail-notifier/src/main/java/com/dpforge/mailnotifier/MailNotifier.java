package com.dpforge.mailnotifier;

import com.dpforge.mailnotifier.format.SourceCodeHtmlFormatter;
import com.dpforge.tellon.core.Changes;
import com.dpforge.tellon.core.ProjectItem;
import com.dpforge.tellon.core.Revision;
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
    private ProjectInfo currentProject;

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
            codeFormatter = SourceCodeHtmlFormatter.create();
        } catch (IOException e) {
            throw new ChangesNotifierException(e);
        }
    }

    @Override
    public void onStartProject(ProjectInfo projectInfo) {
        currentProject = projectInfo;
    }

    @Override
    public void onFinishedProject() {

    }

    @Override
    public void notifyChanges(ProjectItem item, Changes changes) {
        final List<String> watchers = new ArrayList<>();
        final HtmlBuilder body = new HtmlBuilder();
        final MailList mailList = new MailList();

        body.text("Some changes has been made in file ").italic(item.getDescription()).br().br();

        try {
            final Revision actualRevision = item.getActualRevision();
            final Revision previousRevision = item.getPreviousRevision();
            body.text("Author of changes: ").italic(actualRevision.getAuthor()).br()
                    .text("Previous version: ").italic(previousRevision.getVersion()).br()
                    .text("Actual version: ").italic(actualRevision.getVersion()).br()
                    .br();
        } catch (IOException ignored) {
        }

        if (changes.hasUpdated()) {
            body.text("The following source code block(s) has been ").bold("CHANGED").text(":").br();
            for (Changes.Update update : changes.getUpdated()) {
                body.line("Was:")
                        .line(codeFormatter.getHtml(update.getOldBlock()))
                        .line("Now:")
                        .line(codeFormatter.getHtml(update.getNewBlock()))
                        .br();

                extractMailWatchers(watchers, update.getOldBlock().getWatchers());
                extractMailWatchers(watchers, update.getNewBlock().getWatchers());
            }

            for (String watcher : watchers) {
                mailList.add(watcher, body.build());
            }
            watchers.clear();
            body.clear();
        }

        if (changes.hasAdded()) {
            body.text("The following source code block(s) has been ").bold("ADDED").text(":").br();
            for (AnnotatedBlock block : changes.getAdded()) {
                body.line(codeFormatter.getHtml(block)).br();
                extractMailWatchers(watchers, block.getWatchers());
            }

            for (String watcher : watchers) {
                mailList.add(watcher, body.build());
            }
            watchers.clear();
            body.clear();
        }

        if (changes.hasDeleted()) {
            body.text("The following source code block(s) has been ").bold("DELETED").text(":").br();
            for (AnnotatedBlock block : changes.getDeleted()) {
                body.line(codeFormatter.getHtml(block)).br();
                extractMailWatchers(watchers, block.getWatchers());
            }

            for (String watcher : watchers) {
                mailList.add(watcher, body.build());
            }
            watchers.clear();
            body.clear();
        }

        for (MailData mailData : mailList) {
            sendEmail(mailData.address, "Some code was changed in " + currentProject.getName(), mailData.body);
        }
    }

    @Override
    public void notifyItemAdded(ProjectItem item, Changes changes) {
        final List<String> watchers = new ArrayList<>();
        final HtmlBuilder body = new HtmlBuilder();

        body.text("The following source code block(s) has been ").bold("ADDED").text(":").br();
        for (AnnotatedBlock block : changes.getAdded()) {
            body.line(codeFormatter.getHtml(block));
            extractMailWatchers(watchers, block.getWatchers());
        }

        sendEmail(watchers, "Some code was added in " + currentProject.getName(), body.build());
    }

    @Override
    public void notifyItemDeleted(ProjectItem item, Changes changes) {
        final List<String> watchers = new ArrayList<>();
        final HtmlBuilder body = new HtmlBuilder();

        body.text("The following source code block(s) has been ").bold("DELETED").text(":").br();
        for (AnnotatedBlock block : changes.getDeleted()) {
            body.line(codeFormatter.getHtml(block));
            extractMailWatchers(watchers, block.getWatchers());
        }

        sendEmail(watchers, "Some code was deleted in " + currentProject.getName(), body.build());
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
