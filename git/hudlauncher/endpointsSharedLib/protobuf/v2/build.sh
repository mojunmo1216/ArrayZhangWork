#!/bin/bash
protoc --proto_path=./ --java_out=../../src/main/java ./*.proto
