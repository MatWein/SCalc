#!/bin/bash

# use following command first to change java version to 11
# sudo update-alternatives --config java

mvn clean deploy -P deploy-official
