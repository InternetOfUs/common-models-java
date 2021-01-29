# syntax=docker/dockerfile:experimental

FROM maven:3.6.3-jdk-11-slim

WORKDIR /app

RUN echo "PS1='\[\033[01;32m\]WeNet@common-Dev\[\033[00m\]:\[\033[01;34m\]\w\[\033[00m\] \$ '" >> /root/.bashrc
RUN echo "alias mvnd=\"mvn -Dmaven.surefire.debug=\\\"-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=0.0.0.0:5005 -Xnoagent -Djava.compiler=NONE\\\"\"" >> /root/.bashrc

CMD ["bash"]