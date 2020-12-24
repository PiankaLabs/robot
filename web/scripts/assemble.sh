#!/bin/sh

yarn build
rm -r ../server/src/main/resources/*
cp -r build/* ../server/src/main/resources/
