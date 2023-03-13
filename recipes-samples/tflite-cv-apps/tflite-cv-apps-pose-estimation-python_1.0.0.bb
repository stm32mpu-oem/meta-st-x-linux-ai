# Copyright (C) 2022, STMicroelectronics - All Rights Reserved
SUMMARY = "TensorFlowLite Python Computer Vision pose estimation application example"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

COMPATIBLE_MACHINE = "(stm32mp2common)"
SRC_URI  = " file://pose-estimation/python/410-tflite-pose-estimation-python.yaml;subdir=${BPN}-${PV} "
SRC_URI += " file://pose-estimation/python/pose_estimation_tfl.py;subdir=${BPN}-${PV} "
SRC_URI += " file://pose-estimation/python/launch_python_pose_estimation_tfl_movenet_singlepose.sh;subdir=${BPN}-${PV} "
SRC_URI += " file://pose-estimation/python/launch_python_pose_estimation_tfl_movenet_singlepose_testdata.sh;subdir=${BPN}-${PV} "
SRC_URI += " file://pose-estimation/python/py_widgets.css;subdir=${BPN}-${PV} "
SRC_URI += " file://resources/TensorFlowLite_Python.png;subdir=${BPN}-${PV} "
SRC_URI += " file://resources/st_icon_42x52.png;subdir=${BPN}-${PV} "
SRC_URI += " file://resources/st_icon_65x80.png;subdir=${BPN}-${PV} "
SRC_URI += " file://resources/st_icon_130x160.png;subdir=${BPN}-${PV} "
SRC_URI += " file://resources/st_icon_next_inference_42x52.png;subdir=${BPN}-${PV} "
SRC_URI += " file://resources/st_icon_next_inference_65x80.png;subdir=${BPN}-${PV} "
SRC_URI += " file://resources/st_icon_next_inference_130x160.png;subdir=${BPN}-${PV} "
SRC_URI += " file://resources/exit_25x25.png;subdir=${BPN}-${PV} "
SRC_URI += " file://resources/exit_50x50.png;subdir=${BPN}-${PV} "
SRC_URI += " file://resources/setup_camera.sh;subdir=${BPN}-${PV} "
SRC_URI += " file://resources/config_board.sh;subdir=${BPN}-${PV} "
SRC_URI += " file://resources/check_camera_preview.sh;subdir=${BPN}-${PV} "

S = "${WORKDIR}/${BPN}-${PV}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${prefix}/local/demo/application
    install -d ${D}${prefix}/local/demo-ai/computer-vision/tflite-pose-estimation/python
    install -d ${D}${prefix}/local/demo-ai/computer-vision/tflite-pose-estimation/python/resources

    # install applications into the demo launcher
    install -m 0755 ${S}/pose-estimation/python/*.yaml ${D}${prefix}/local/demo/application

    # install the icons
    install -m 0755 ${S}/resources/* ${D}${prefix}/local/demo-ai/computer-vision/tflite-pose-estimation/python/resources

    # install python scripts and launcher scripts
    install -m 0755 ${S}/pose-estimation/python/*.py ${D}${prefix}/local/demo-ai/computer-vision/tflite-pose-estimation/python
    install -m 0755 ${S}/pose-estimation/python/*.sh ${D}${prefix}/local/demo-ai/computer-vision/tflite-pose-estimation/python
    install -m 0755 ${S}/pose-estimation/python/*.css ${D}${prefix}/local/demo-ai/computer-vision/tflite-pose-estimation/python/resources
}

FILES:${PN} += "${prefix}/local/"

RDEPENDS:${PN} += " \
	python3-core \
	python3-numpy \
	python3-opencv \
	python3-pillow \
	python3-pygobject \
	python3-tensorflow-lite \
	tflite-models-movenet-singlepose-lightning \
	bash \
"
