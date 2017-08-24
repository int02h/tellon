# Tellon
Tellon is a Java library that analyzes source code written in Java and notifies concerned people when annotated code has been modified.

Tellon is abstracted from source code observer and notification machanism as much as possible. Source code observer can be implemented over Git or SVN, notifications can be sent through e-mail or instant messengers. You are able to implement it any way you want.

Apart from Tellon library there are some useful modules in this repository:
  - Console application that uses Tellon library
  - Git-based implementation of source code observer
  - E-mail notifier
 
## How it works
Tellon builds up [Abstract Syntax Tree (AST)](https://en.wikipedia.org/wiki/Abstract_syntax_tree) for previous and actual revisions of source code received from source code observer. On the next step it is trying to find source code blocks annotated with special annotation. When all annonated blocks are found Tellon figures out differences between blocks and notify corresponding watchers using notifier.

## How to use
First of all you should annotate source code you take care of with annotation @NotifyChanges as follows:
```java
@NotifyChanges("mailto:watcher@example.com")
public void doSomeWork() {
    // some code
}
```

Tellon supports annotation of following source code elements:
  - Class/Interface
  - Method/Constructor
  - Field
  - Annotation
 
Arguments of @NotifyChanges annotation represent how to notify watchers of this source code block. You are able to use string literals or constant fields:
```java
public class Contacts {
    public static final String DEVELOPER2 = "mailto:dev2@example.com";
}

@NotifyChanges({"mailto:dev1@example.com", Contacts.DEVELOPER2})
public void foo() {
    // some code
}
```

Because of using AST Tellon has no information about values stored in constant fields. So it is trying to get this information from source code observer. Therefore there are some limitations on declaring watchers using constant fields. These fields must be:
  - Static
  - Final
  - Of non-array String data type

## Console Application
In this repository you may find console application that wraps Tellon library for the convenient usage. Now it supports the following commands:
  - verify
  - notify
  - help

Name of the command is passed as first argument.

### Verify Command
*Verify* command lets you to check up Tellon's environment. It prints list of project observers and project notifiers that has been found by using ServiceLoader. Try out *--help* argument for more information.

### Notify Command
*Notify* command does all the magic! It has several arguments:
  - **observer-args** - you can pass arguments to the project observer such as specific Git-revision or something else;
  - **observer** or **o** - lets you choose project observer. Tellon will use it to retrieve source code for the further changes detection;
  - **master** - contacts of the responsible developer, who will be notified if something goes wrong with Tellon.

**WARNING!** Be careful! Master developer will be notified only if notification system has been correctly initialized. Otherwise application error code will help you to find out what happened.

### Help Command
*Help* prints useful information about supported commands. Also you are able to use *--help* argument with any supported command to get more information about it.

### Application Error Codes
Code | Desciption
--- | ---
0 | Everything went well 
1 | Bad config. No notifier or observer was specified
2 | Fail to init observer or notifier
3 | Fail to execute command
4 | Wrong arguments passed to the command
5 | Command execution has failed at runtime

## Git Project Observer
Tellon works with source code provided by project observer. In this repository you may find the implementation based on Git (using [JGit](https://github.com/eclipse/jgit)). It calculates difference between two specified revisions and let Tellon to find out what source code blocks has been changed. Git-Observer has four following arguments:
  - *gitPath* (required) - path to .git folder of your repository;
  - *srcDir* (required) - path to source code directory where all of your java packages are placed. It makes Git-observer able to retrieve source code of the class by its qualified name so you can use constant fields as arguments for @NotifyChanges annotation;
  - *newRev* (optional) - latest revision of your source code. Up to this revision Tellon will try to find changes. By default it is *HEAD*;
  - *oldRev* (optional) - oldest revision of your source code. From this revision Tellon will try to find changes. By default it is previous revision relatively to *newRev*.

Arguments for any observer can be passed through the *observer-args* argument of console application or directly to the method *init* which every observer implements.

If you use console application then you can pass arguments to the observer as key-value pairs as shown below:
```
--observer-args "arg1=value1" "arg2=value2" "argN=valueN"
```

## Mail Project Notifier
When Tellon has detected some changes it is necessary to notify concerned people about them. In this repository you may find project notifier implementation based on [Simple Java Mail](https://github.com/bbottema/simple-java-mail). It just sends e-mails about changes found in source code. Also e-mail will be sent if something go wrong (except cases when notifier fail to initialize).

To specify e-mail address of concerned watcher in @NofityChanges annotation you need to put the prefix *"mailto:"* (without quotes) before e-mail:
```java
@NotifyChanges("mailto:dev1@example.com")
class Foo {
}
```

If you forget to put "mailto:" prefix before address then notifier will skip this watcher. Be careful! 

The main idea of prefix is to specify the way how to contact watcher. It can be something like "sms:+123456" or "telegram:@example". But obviously mail project notifier supports just "mailto:". But you can implement project notifier any way you want.

Property file *mail-notifier.properties* contains info about e-mail sender. You need to set up at least these properties:
```
simplejavamail.transportstrategy=SMTP_SSL
simplejavamail.smtp.host=smtp.example.com
simplejavamail.smtp.port=465
simplejavamail.smtp.username=test@example.com
simplejavamail.smtp.password=wordpass
```