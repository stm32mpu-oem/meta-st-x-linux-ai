# Copyright 2020-2021 STMicroelectronics
DESCRIPTION = "Verisilicon TFLite VX Delegate for STM32 Devices"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7d6260e4f3f6f85de05af9c8f87e6fb5"

SRCBRANCH_vx = "main"
SRCREV_vx = "e691bb06246050290b33c12f9c34a89ea0401ac9"

SRCBRANCH_tf = "r2.8"
SRCREV_tf = "e994fb9c3ad250d38fd07511aaa445eda728f9af"

SRC_URI = "git://github.com/VeriSilicon/tflite-vx-delegate.git;branch=${SRCBRANCH_vx};name=vx;destsuffix=git_vx/;protocol=https \
           git://github.com/tensorflow/tensorflow.git;branch=${SRCBRANCH_tf};name=tf;destsuffix=git_tf/;protocol=https "

PV = "2.8.0+git${SRCREV_vx}"
S = "${WORKDIR}/git_vx"
COMPATIBLE_MACHINE = "stm32mp25common"

inherit cmake
DEPENDS += "tim-vx patchelf-native"

python () {
    #Get major of the PV variable
    version = d.getVar('PV')
    version = version.split("+")
    version_base = version[0]
    version = version_base.split(".")
    major = version[0]
    d.setVar('MAJOR', major)
    d.setVar('PVB', version_base)
}

do_configure[network] = "1"

do_configure:prepend() {
    if [ -n "${http_proxy}" ]; then
        export HTTP_PROXY=${http_proxy}
        export http_proxy=${http_proxy}
    fi
    if [ -n "${https_proxy}" ]; then
        export HTTPS_PROXY=${https_proxy}
        export https_proxy=${https_proxy}
    fi
}

EXTRA_OECMAKE += " -DFETCHCONTENT_SOURCE_DIR_TENSORFLOW=${WORKDIR}/git_tf \
                   -DTIM_VX_INSTALL=${STAGING_DIR_TARGET}/usr \
                   -DTFLITE_ENABLE_XNNPACK=OFF \
                   -DTFLITE_ENABLE_EXTERNAL_DELEGATE=ON \
                   -DFETCHCONTENT_FULLY_DISCONNECTED=OFF \
"

do_install() {
    # Install libvx_delegate.so into libdir
    install -d ${D}${libdir}
    install -d ${D}${includedir}/VX
    install -m 0755 ${WORKDIR}/build/libvx_delegate.so ${D}${libdir}/libvx_delegate.so.${PVB}
    patchelf --set-soname libvx_delegate.so ${D}${libdir}/libvx_delegate.so.${PVB}
    ln -sf libvx_delegate.so.${PVB} ${D}${libdir}/libvx_delegate.so.${MAJOR}
    ln -sf libvx_delegate.so.${PVB} ${D}${libdir}/libvx_delegate.so

    # Install cusom static lib
    install -m 0755 libvx_custom_op.a ${D}${libdir}/libvx_custom_op.a
    install -m 0644 ${S}/vsi_npu_custom_op.h ${D}${includedir}/VX/vsi_npu_custom_op.h
}

FILES:${PN} += " ${libdir}/libvx_delegate.so.${MAJOR} \
                 ${libdir}/libvx_delegate.so.${PVB} \
"

RDEPENDS:${PN} += "tim-vx-dev"

INSANE_SKIP:${PN} += " ldflags dev-deps"
