#!/bin/sh
weston_user=$(ps aux | grep '/usr/bin/weston '|grep -v 'grep'|awk '{print $1}')

source /usr/local/demo-ai/computer-vision/tflite-semantic-segmentation/python/resources/config_board.sh
cmd="python3 /usr/local/demo-ai/computer-vision/tflite-semantic-segmentation/python/sem_seg_tfl.py -m /usr/local/demo-ai/computer-vision/models/deeplabv3/deeplabv3_quant.tflite -l /usr/local/demo-ai/computer-vision/models/deeplabv3/labelmap.txt --framerate $DFPS --frame_width $DWIDTH --frame_height $DHEIGHT $COMPUTE_ENGINE"

if [ "$weston_user" != "root" ]; then
	echo "user : "$weston_user
	script -qc "su -l $weston_user -c '$cmd'"
else
	$cmd
fi
