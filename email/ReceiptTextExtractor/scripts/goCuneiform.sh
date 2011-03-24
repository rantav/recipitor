#! /bin/sh
# usage: go.sh messageID url
OUT_TXT_FILE=/tmp/$1.txt
IMG_FILE=/tmp/$1-img.png
URL=$2
#wget -O - $URL > $IMG_FILE
curl --output $IMG_FILE $URL
convert $IMG_FILE -resize 500 $IMG_FILE.resize 
cuneiform $IMG_FILE.resize -o $OUT_TXT_FILE
#convert $IMG_FILE.resize $IMG_FILE.resize.pgm
#gocr -l 70  -i $IMG_FILE.resize.pgm > $OUT_TXT_FILE
cat $OUT_TXT_FILE
#rm $OUT_TXT_FILE
