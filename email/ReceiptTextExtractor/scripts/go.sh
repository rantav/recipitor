#! /bin/sh
# usage: go.sh messageID, url
OUT_TXT_FILE=/tmp/$1
IMG_FILE=/tmp/$1-img.png
URL=$2
wget -O - $URL > $IMG_FILE
cuneiform $IMG_FILE -o $OUT_TXT_FILE &> /dev/null
cat $OUT_TXT_FILE