FROM zenika/kotlin

RUN apt-get update \
  && apt-get install -y \
  vim \
  zip \
  unzip \
  openssl \
  && apt-get clean \
  && rm -fr /var/lib/apt/lists/*

WORKDIR /home
RUN wget https://services.gradle.org/distributions/gradle-5.3.1-bin.zip

RUN mkdir /opt/gradle
RUN unzip -d /opt/gradle gradle-5.3.1-bin.zip
ENV PATH $PATH:/opt/gradle/gradle-5.3.1/bin
RUN rm gradle-5.3.1-bin.zip

WORKDIR /home
