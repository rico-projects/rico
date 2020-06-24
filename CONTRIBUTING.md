# Contributing to Rico

As an open source project, Rico welcomes contributions of many forms.

## Bug reporting

Please use the [issue tracker on GitHub][1]. 

Please make sure to check these notes whenever you create an issue for Rico:
- If you are not using the latest version of Rico please add the used version in the description
- Please give us some information about the server infrastructure that you are using. Examples: Spring Boot 1.2.2 or JBOSS 6.4 EAP
- In case of an error / bug it's always helpful to add the error log from the client and server application

[1]: https://github.com/rico-project/Rico/issues

## Patches submission

Patches are welcome as [pull requests on GitHub][2] 

[2]: https://github.com/rico-project/Rico/pulls

## Local Development Setup

The entire Rico build and tests can be run with the following command:

```bash
./gradlew clean verify
```

If you are using an IDE and want to be able to run tests within it you need to configure the following JVM argument:
```bash
-Djdk.attach.allowAttachSelf
```
For IntelliJ this can be done in the Run/Debug Configuration templates.
<img src="readme/intellij_run_config.png?raw=true" alt="IntelliJ Run/Debug Configuration"/>
