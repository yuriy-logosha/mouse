#!/usr/bin/env bash
echo -------------------------------------------
echo Usage:
echo "First argument - location of release folder (Default: ./release)"
echo "Second argument - new version (Default: ./server/target/release)"
echo "Third argument - new client (Default: ./client/target)"
echo "Fifths argument - startup folder (Default: ./)"
echo -------------------------------------------
echo Example: ./deploy.sh release server/target/release ./

SRT=%4
CLT=%3
CUR=%2
DST=%1
LTT=$DST/latest
SR=$DST/$(date +%Y%m%d%H%M)

if [ $# -eq 0 ]
  then
    echo No arguments supplied
    SRT="./"
    CUR="./server/target/release"
    DST="./release"
    LTT="./release/latest"
    SR="./release/"$(date +%Y%m%d%H%M)
    CLT="./client/target"
fi

echo
echo Deploying new version!!!
echo

echo Current version stored to: $SR
mkdir $SR
mv -fv $LTT/* $SR

echo Deploying new version:
cp -rfv $CUR/*.jar $LTT
cp -rfv $CLT/*.jar $LTT

cp -rfv $CUR/*.sh $SRT