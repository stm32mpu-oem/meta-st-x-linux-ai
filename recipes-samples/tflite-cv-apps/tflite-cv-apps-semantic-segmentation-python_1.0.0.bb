# Copyright (C) 2022, STMicroelectronics - All Rights Reserved
SUMMARY = "TensorFlowLite Python Computer Vision semantic segmentation application example"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

SRC_URI  = " file://semantic-segmentation/python/510-tflite-semantic-segmentation-python.yaml;subdir=${BPN}-${PV} "
SRC_URI += " file://semantic-segmentation/python/sem_seg_tfl.py;subdir=${BPN}-${PV} "
SRC_URI += " file://semantic-segmentation/python/launch_python_semantic_seg_tfl_deeplabv3.sh;subdir=${BPN}-${PV} "
SRC_URI += " file://semantic-segmentation/python/launch_python_semantic_seg_tfl_deeplabv3_testdata.sh;subdir=${BPN}-${PV} "
SRC_URI += " file://semantic-segmentation/python/Default.css;subdir=${BPN}-${PV} "
SRC_URI += " file://resources/TensorFlowLite_Python.png;subdir=${BPN}-${PV} "
SRC_URI += " file://resources/st_icon_42x52.png;subdir=${BPN}-${PV} "
SRC_URI += " file://resources/st_icon_65x80.png;subdir=${BPN}-${PV} "
SRC_URI += " file://resources/st_icon_130x160.png;subdir=${BPN}-${PV} "
SRC_URI += " file://resources/st_icon_next_inference_42x52.png;subdir=${BPN}-${PV} "
SRC_URI += " file://resources/st_icon_next_inference_65x80.png;subdir=${BPN}-${PV} "
SRC_URI += " file://resources/st_icon_next_inference_130x160.png;subdir=${BPN}-${PV} "
SRC_URI += " file://resources/exit_25x25.png;subdir=${BPN}-${PV} "
SRC_URI += " file://resources/exit_50x50.png;subdir=${BPN}-${PV} "
SRC_URI += " file://resources/config_board.sh;subdir=${BPN}-${PV} "

SRC_URI:append:stm32mp1common = "   file://resources/check_camera_preview.sh;subdir=${BPN}-${PV} \
                                    file://resources/setup_camera.sh;subdir=${BPN}-${PV} "

SRC_URI:append:stm32mp25common = "  file://resources/check_camera_preview_main_isp.sh;subdir=${BPN}-${PV} \
                                    file://resources/setup_camera_main_isp.sh;subdir=${BPN}-${PV} "

S = "${WORKDIR}/${BPN}-${PV}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${prefix}/local/demo/application
    install -d ${D}${prefix}/local/demo-ai/computer-vision/tflite-semantic-segmentation/python
    install -d ${D}${prefix}/local/demo-ai/computer-vision/tflite-semantic-segmentation/python/resources

    if [ -f ${S}/resources/check_camera_preview_main_isp.sh ]; then
        mv  ${S}/resources/check_camera_preview_main_isp.sh ${S}/resources/check_camera_preview.sh
    fi

    if [ -f ${S}/resources/setup_camera_main_isp.sh ]; then
        mv  ${S}/resources/setup_camera_main_isp.sh ${S}/resources/setup_camera.sh
    fi

    # install applications into the demo launcher
    install -m 0755 ${S}/semantic-segmentation/python/*.yaml ${D}${prefix}/local/demo/application

    # install the icons
    install -m 0755 ${S}/resources/* ${D}${prefix}/local/demo-ai/computer-vision/tflite-semantic-segmentation/python/resources

    # install python scripts and launcher scripts
    install -m 0755 ${S}/semantic-segmentation/python/*.py ${D}${prefix}/local/demo-ai/computer-vision/tflite-semantic-segmentation/python
    install -m 0755 ${S}/semantic-segmentation/python/*.sh ${D}${prefix}/local/demo-ai/computer-vision/tflite-semantic-segmentation/python
    install -m 0755 ${S}/semantic-segmentation/python/*.css ${D}${prefix}/local/demo-ai/computer-vision/tflite-semantic-segmentation/python/resources
}

FILES:${PN} += "${prefix}/local/"

RDEPENDS:${PN} += " \
	python3-core \
	python3-numpy \
	python3-opencv \
	python3-pillow \
	python3-pygobject \
	python3-tensorflow-lite \
	tflite-models-deeplabv3 \
	bash \
"
