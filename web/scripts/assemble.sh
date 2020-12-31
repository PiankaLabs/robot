#!/bin/sh

yarn build
rm -r ../server/src/main/resources/web/*
cp -r build/* ../server/src/main/resources/web/

date