#!/bin/sh
weston_user=$(ps aux | grep '/usr/bin/weston '|grep -v 'grep'|awk '{print $1}')

source /usr/local/demo-ai/computer-vision/nbg-image-classification/bin/resources/config_board.sh
cmd="/usr/local/demo-ai/computer-vision/nbg-image-classification/bin/label_nbg_gst_gtk -m /usr/local/demo-ai/computer-vision/models/mobilenet/mobilenet.nb -l /usr/local/demo-ai/computer-vision/models/mobilenet/labels_mobilenet_nbg.txt --framerate $DFPS --frame_width $DWIDTH --frame_height $DHEIGHT"

if [ "$weston_user" != "root" ]; then
	echo "user : "$weston_user
	script -qc "su -l $weston_user -c '$cmd'"
else
	$cmd
fi
