@echo off
chcp 1252 > nul
java -jar --enable-preview -Dfile.encoding=windows-1252 %0\..\math.jar