#! /bin/sh
#usage term num_of_errors tokens
# return non zero if exists
TERM=$1
NUM_OF_ERRORS=$2
TOKENS=$3 
echo $TOKENS | agrep -$NUM_OF_ERRORS "$TERM"
# echo $NUM_OF_ERRORS $TERM and RES is [$RES] >> /tmp/log.txt
#echo $RES
