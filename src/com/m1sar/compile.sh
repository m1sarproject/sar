#!/bin/bash
find -name "*.java" > sources.txt
javac @sources.txt
rm sources.txt