#!/bin/bash
# Compilation script for the distributed algorithms project

echo "Compiling framework..."
javac -cp ".:lib/*" -d bin src/framework/*.java

echo "Compiling week1..."
javac -cp ".:lib/*:bin" -d bin src/week1/*.java

echo "Compiling week2..."
javac -cp ".:lib/*:bin" -d bin src/week2/*.java

echo "Compiling week34..."
javac -cp ".:lib/*:bin" -d bin src/week34/*.java

echo "Compiling week56..."
javac -cp ".:lib/*:bin" -d bin src/week56/*.java

echo "Compiling week78..."
javac -cp ".:lib/*:bin" -d bin src/week78/*.java

echo "Compilation completed successfully!"
