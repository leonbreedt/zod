#!/bin/bash

BASEDIR="$(cd $(dirname $0)/.. && pwd)"
JAR=zod-0.0.1.jar

java -jar ${BASEDIR}/target/${JAR}