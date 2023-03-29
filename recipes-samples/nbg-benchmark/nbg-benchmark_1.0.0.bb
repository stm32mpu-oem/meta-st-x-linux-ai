# COPYRIGHT (C) 2023, STMICROELECTRONICS - All Rights Reserved
SUMMARY = "NBG Benchmark tool to parse and benchmark nbg model files using OpenVX API"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

DEPENDS += "jpeg"
DEPENDS:append:stm32mp25common = " gcnano-driver-stm32mp gcnano-userland "

PV = "0.1.0"

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

SRC_URI = " file://src/nbg_benchmark.cpp \
            file://src/vnn_utils.cpp \
            file://src/vnn_utils.h \
            file://src/Makefile"

S = "${WORKDIR}"

do_configure() {
	oe_runmake -C ${S}/src/ clean
}

EXTRA_OEMAKE  = 'SYSROOT="${RECIPE_SYSROOT}"'

do_compile(){
    export SYSROOT=${RECIPE_SYSROOT}
	oe_runmake -C ${S}/src/ all 'CC=${CC}'
}

do_install() {
	install -d ${D}${prefix}/local/bin/${PN}-${PVB}/tools
    install -m 0755 ${S}/src/nbg_benchmark ${D}${prefix}/local/bin/${PN}-${PVB}/tools
}

INSANE_SKIP:${PN} = "ldflags"
FILES:${PN} += "${prefix}/local/bin/${PN}-${PVB}/tools/nbg_benchmark"
