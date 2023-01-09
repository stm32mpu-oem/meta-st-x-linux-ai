DESCRIPTION = "TIM-VX is a software integration module provided by VeriSilicon to facilitate \
deployment of Neural-Networks on OpenVX enabled ML accelerators. It serves as the backend \
binding for runtime frameworks such as Android NN, Tensorflow-Lite, MLIR, TVM and more."
SUMMARY = "Tensor Interface Module for OpenVX"
HOMEPAGE = "https://github.com/VeriSilicon/TIM-VX"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=ededf2503f5d147ae718276dfd28801f"

PV = "1.1.57+git${SRCPV}"

SRCBRANCH = "main"
SRCREV = "0e211c8efdf412a7d09629aa0452d9356e6d113b"
SRC_URI ="git://github.com/VeriSilicon/TIM-VX.git;branch=${SRCBRANCH};protocol=https"

S = "${WORKDIR}/git"

# Only compatible with stm32mp25
COMPATIBLE_MACHINE = "stm32mp25common"

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

inherit cmake
DEPENDS += " patchelf-native \
	     gcnano-driver-stm32mp \
	     gcnano-userland \
	"

EXTRA_OECMAKE =  " \
    -DCONFIG=YOCTO \
    -DCMAKE_SYSROOT=${RECIPE_SYSROOT} \
    -DTIM_VX_ENABLE_TEST=ON \
    -DCMAKE_SKIP_RPATH=TRUE \
    -DFETCHCONTENT_FULLY_DISCONNECTED=OFF \
    -DTIM_VX_USE_EXTERNAL_OVXLIB=ON \
    -DOVXLIB_INC=${S}/src/tim/vx/internal/include/ \
    -DOVXLIB_LIB=${STAGING_LIBDIR}/libovxlib.so \
"
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

do_install() {
    # Install libtim-vx.so into libdir
    install -d ${D}${libdir}
    install -d ${D}/usr/local/bin/${PN}-${PVB}

    install -m 0755 ${WORKDIR}/build/src/tim/libtim-vx.so ${D}${libdir}/libtim-vx.so.${PVB}
    patchelf --set-soname libtim-vx.so ${D}${libdir}/libtim-vx.so.${PVB}

    ln -sf libtim-vx.so.${PVB} ${D}${libdir}/libtim-vx.so.${MAJOR}
    ln -sf libtim-vx.so.${PVB} ${D}${libdir}/libtim-vx.so

    # Install other libraries for benchmark
    install -m 0755 ${WORKDIR}/build/lib/libgtest_main.so ${D}${libdir}/libgtest_main.so.1.11.0
    install -m 0755 ${WORKDIR}/build/lib/libgtest.so      ${D}${libdir}/libgtest.so.1.11.0
    install -m 0755 ${WORKDIR}/build/lib/libgmock_main.so ${D}${libdir}/libgmock_main.so
    install -m 0755 ${WORKDIR}/build/lib/libgmock.so      ${D}${libdir}/libgmock.so
    install -m 0755 ${WORKDIR}/build/src/tim/unit_test    ${D}/usr/local/bin/${PN}-${PVB}/TIM-VX_test

    # Include
    install -d ${D}${includedir}
    cp -r ${S}/include/tim ${D}${includedir}
    cp -r ${STAGING_INCDIR}/CL/cl_viv_vx_ext.h ${D}/usr/local/bin/${PN}-${PVB}/cl_viv_vx_ext.h
}

PACKAGES =+ "${PN}-tools"
FILES_SOLIBSDEV = ""

FILES:${PN}-tools = "   /usr/local/bin/${PN}-${PVB}/TIM-VX_test \
			 /usr/local/bin/${PN}-${PVB}/cl_viv_vx_ext.h \
			 ${libdir}/libgtest_main.so.1.11.0 \
			 ${libdir}/libgtest.so.1.11.0 \
			 ${libdir}/libgmock_main.so \
			 ${libdir}/libgmock.so \
"

FILES:${PN}-dev += "${libdir}/libtim-vx.so"

FILES:${PN} += " ${libdir}/libtim-vx.so.${MAJOR} \
                 ${libdir}/libtim-vx.so.${PVB}   \
"
RDEPENDS:${PN} += "libopencl-gcnano-dev"

INSANE_SKIP:${PN} = "dev-deps"
