#!/bin/bash

# compiles and executes this java project
if [[ -d "./bin/" ]]; then
    echo "./bin/ Already exists, skipping creation"

else
    mkdir bin/
    echo "bin/ doesn't exists, so creating one for class files"

fi

echo "Compiling the project"
javac -d ./bin ./src/tictoc/*.java ./src/tictoc/helpers/*.java ./src/tictoc/utils/*.java

read -r -p "Do you want to run the project?(y/n) : " VAR
if [[ $VAR == "y" ]]; then
    java -cp ./bin/ tictoc.GameEntry

else
    echo "Halting"

fi

