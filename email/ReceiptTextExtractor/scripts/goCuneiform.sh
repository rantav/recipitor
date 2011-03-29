#! /bin/sh
# usage: go.sh messageID url ext
OUT_TXT_FILE=/tmp/$1.txt
EXT=$3
IMG_FILE=/tmp/$1-img.$EXT
URL=$2
#wget -O - $URL > $IMG_FILE
curl --output $IMG_FILE $URL
#convert $IMG_FILE -resize 500\> $IMG_FILE.resize.$EXT 
cuneiform $IMG_FILE -o $OUT_TXT_FILE
#convert $IMG_FILE.resize.$EXT $IMG_FILE.resize.pgm
gocr -l 70  -i $IMG_FILE.resize.$EXT > $OUT_TXT_FILE2
#gocr $IMG_FILE.resize.$EXT 
cat $OUT_TXT_FILE
#rm $OUT_TXT_FILE
