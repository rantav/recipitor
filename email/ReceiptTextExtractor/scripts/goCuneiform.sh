#! /bin/sh
# usage: go.sh messageID, url
OUT_TXT_FILE=/tmp/$1.txt
IMG_FILE=/tmp/$1-img.png
URL=$2
wget -O - $URL > $IMG_FILE
cuneiform $IMG_FILE -o $OUT_TXT_FILE
#convert $IMG_FILE $IMG_FILE.pgm
#gocr -l 70  -i $IMG_FILE.pgm > $OUT_TXT_FILE
cat $OUT_TXT_FILE
rm -f $OUT_TXT_FILE