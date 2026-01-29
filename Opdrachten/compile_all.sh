#!/bin/bash
find /Users/SO28RO/IdeaProjects/ib2302/Opdrachten/src -name "*.java" > /Users/SO28RO/IdeaProjects/ib2302/Opdrachten/sources.txt
javac -cp "/Users/SO28RO/IdeaProjects/ib2302/Opdrachten/lib/*" -d /Users/SO28RO/IdeaProjects/ib2302/Opdrachten/bin/production/Opdrachten @/Users/SO28RO/IdeaProjects/ib2302/Opdrachten/sources.txt
rm /Users/SO28RO/IdeaProjects/ib2302/Opdrachten/sources.txt

