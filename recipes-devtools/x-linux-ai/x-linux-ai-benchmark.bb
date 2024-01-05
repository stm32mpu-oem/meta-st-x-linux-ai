DESCRIPTION = "X-LINUX-AI NN unified benchmark"
AUTHOR = "STMicroelectronics"
SUMMARY = "X-LINUX-AI NN unified benchmark binaries"
LICENSE = "CLOSED"

SRC_URI = "	file://x-linux-ai-benchmark/x_linux_ai_benchmark_mp1 \
			file://x-linux-ai-benchmark/x_linux_ai_benchmark_mp2 \
"

S = "${WORKDIR}/${BPN}"

BOARD_USED:stm32mp1common = "stm32mp1"
BOARD_USED:stm32mp25common = "stm32mp2_npu"

do_install() {
    install -d ${D}/usr/bin
    install -m 0755 ${S}/x_linux_ai_benchmark_mp1 ${D}/usr/bin/x-linux-ai-benchmark
}

do_install:append:stm32mp25common(){
    install -m 0755 ${S}/x_linux_ai_benchmark_mp2 ${D}/usr/bin/x-linux-ai-benchmark
}

FILES:${PN} += "/usr/bin"
RDEPENDS:${PN} += " libpython3 \
                    python3-prettytable \
                    "
