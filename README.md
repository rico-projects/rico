<p>
  ⚠️ <b>IMPORTANT:</b> ⚠️ Rico has been put <b>"on hold"</b>. To learn what is meant by that please <a href="https://dev.karakun.com/rico/2021/07/02/rico-on-hold.html">read the blog post</a>  and feel free to contact us via rico@karakun.com to clarify upon questions.<br />
</p>

<p align="right">
<a href="https://dev.karakun.com" target="_blank"><img src="readme/supported_by_karakun.png?raw=true" alt="Supported by Karakun"/></a>
</p>

# Rico [![Build Status](https://travis-ci.org/rico-projects/rico.svg?branch=master)](https://travis-ci.org/github/rico-projects/rico)

This repository contains all Java related sources of Rico. Clients for other languages will be provided in seperate repositories (JavaScript, AngularJS, Polymer and Android).

![Rico Logo](readme/rico_logo.png)

![Hacktoberfest Logo](https://hacktoberfest.digitalocean.com/assets/HF-full-logo-b05d5eb32b3f3ecc9b2240526104cf4da3187b8b61963dd9042fdc2536e4a76c.svg)

*This project joined [Hacktoberfest 2020](https://hacktoberfest.digitalocean.com/)!
Issues that can give you a good start are [have the label "Hacktoberfest"](https://github.com/rico-projects/rico/issues?q=is%3Aissue+is%3Aopen+label%3A%22Hacktoberfest%22) and maintainers are willing to help.*

## About Rico
Rico is a client-server-framework that provides several functionalities and APIs to create enterprise applications. This
includes features like tracing pr monitoring that are critical in developing applications, escpecially in modern cloud based 
and distributed application landscapes.
To do so the Rico provides generic APIs with specific implementation for the mostly used frameworks
and toolkits in modern application development. 

![Client and server structure](readme/client_and_server.png)

Rico provides implementations for all its features for the given frameworks on the server:

* JavaEE / JakartaEE
* Spring (Boot)

Next to tis Rico provides implementations for the following ui toolkits and languages.

* Plain Java client
* JavaFX
* JavaScript client ([external repo](https://github.com/rico-projects/rico-js))
* Polymer 2 client ([external repo](https://github.com/rico-projects/rico-polymer))
* AngularJS (1.x) client ([external repo](https://github.com/rico-projects/rico-angularjs))
* Angular (>2.x) client  ([external repo](https://github.com/rico-projects/rico-angular))

## Features of Rico
As already mentioned Rico provides a list of general features that are usefull in mostly all enterprise applications. A
detailed description of the features will be part of the future technical documentation of Rico. Until now Rico provides
APIs for the following functionalities:

* client scope / session
* ui toolkit friendly http client
* application management
* general context manager
* metrics handling
* server timing
* security and sso
* remoting
* logging
* projection
* tracing

An introduction of Rico can be found in the [Karakun DevHub](https://dev.karakun.com/rico/).

## Issues

Please note that this repository is the main repository for all rico modules. Currently, we maintain some modules (especially the JavaScript libraries) in separate repos. To keep it easer to get an overview of the issues, we track all of them [in this repository](https://github.com/rico-projects/rico/issues).

## Development
To get an overview about the project structure, please look at the [repective chapter in our documentation]( https://rico-projects.github.io/rico/#_project_structure ).

Also, please refer to the [CODE_OF_CONDUCT.md]( CODE_OF_CONDUCT.md ), [CONTIBUTING.md]( CONTIBUTING.md ) and  [JAVA_STYLE_GUIDE.md]( JAVA_STYLE_GUIDE.md ) .

## Development origin
The Rico framework is the successor of the [Dolphin Platform](https://github.com/canoo/dolphin-platform) that provided 
functionality like an asynchronous remoting layer for enterprise applications based on generic APIs. While Rico shares 
the general idea of the Dolphin Platform it provides a lot more functionality.

## 🆕🐥🐶 First Timers Support
At Karakun we ❤️ open source. Based on this we develop the complete Rico framework as an open source project. If you are new to open source development we have some specific issues for you. Just have a look at all issues with the 'good first issue' label. This issues are reserved for people who never contributed to Open Source before. We know that the process of creating a pull request is the biggest barrier for new contributors. This issues are for you 💝 We use such issues for example to get involved at Hacktoberfest. If you want to know more about open source development and contribution in general you should have a look at this overview.

## License
The project is released as open source under the Apache License Version 2.0

![Rico Open Source](readme/rico_os.png)
