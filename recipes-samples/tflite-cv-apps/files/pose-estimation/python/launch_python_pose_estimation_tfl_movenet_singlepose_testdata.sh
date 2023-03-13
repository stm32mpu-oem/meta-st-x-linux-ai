#!/bin/sh
weston_user=$(ps aux | grep '/usr/bin/weston '|grep -v 'grep'|awk '{print $1}')

source /usr/local/demo-ai/computer-vision/tflite-pose-estimation/python/resources/config_board.sh
cmd="python3 /usr/local/demo-ai/computer-vision/tflite-pose-estimation/python/pose_estimation_tfl.py -m /usr/local/demo-ai/computer-vision/models/movenet/lite-model_movenet_singlepose_lightning_tflite_int8_4.tflite -i /usr/local/demo-ai/computer-vision/models/movenet/testdata/ $COMPUTE_ENGINE"

if [ "$weston_user" != "root" ]; then
	echo "user : "$weston_user
	script -qc "su -l $weston_user -c '$cmd'"
else
	$cmd
fi
