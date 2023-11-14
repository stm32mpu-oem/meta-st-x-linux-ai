SUMMARY = "X-LINUX-AI full components (TFLite and application samples)"
LICENSE = "MIT & Apache-2.0 & BSD-3-Clause"

LIC_FILES_CHKSUM  = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
LIC_FILES_CHKSUM += "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
LIC_FILES_CHKSUM += "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = "\
    packagegroup-x-linux-ai                  \
    packagegroup-x-linux-ai-tflite           \
    packagegroup-x-linux-ai-coral   \
    packagegroup-x-linux-ai-onnxruntime      \
"

PACKAGES:append:stm32mp25common = " packagegroup-x-linux-ai-npu"

# Manage to provide all framework tools base packages with overall one
RDEPENDS:packagegroup-x-linux-ai = "\
    packagegroup-x-linux-ai-tflite           \
    packagegroup-x-linux-ai-coral   \
    packagegroup-x-linux-ai-onnxruntime      \
"
RDEPENDS:packagegroup-x-linux-ai:append:stm32mp25common = " packagegroup-x-linux-ai-npu"

SUMMARY:packagegroup-x-linux-ai-tflite = "X-LINUX-AI TensorFlow Lite components"
RDEPENDS:packagegroup-x-linux-ai-tflite = "\
    python3-tensorflow-lite \
    tensorflow-lite-tools \
    tensorflow-lite \
    x-linux-ai-tool \
    tflite-image-classification-cpp \
    tflite-image-classification-python \
    tflite-object-detection-cpp \
    tflite-object-detection-python \
"

RDEPENDS:packagegroup-x-linux-ai-tflite:append:stm32mp25common = "\
    tflite-pose-estimation-python \
    tflite-semantic-segmentation-python \
"

SUMMARY:packagegroup-x-linux-ai-coral = "X-LINUX-AI TensorFlow Lite Edge TPU components"
RDEPENDS:packagegroup-x-linux-ai-coral = "\
    libedgetpu \
    libcoral \
    python3-pycoral \
    python3-tensorflow-lite \
    tensorflow-lite \
    x-linux-ai-tool \
    coral-edgetpu-benchmark \
    coral-image-classification-cpp \
    coral-image-classification-python \
    coral-object-detection-cpp \
    coral-object-detection-python \
"

SUMMARY:packagegroup-x-linux-ai-onnxruntime = "X-LINUX-AI ONNX Runtime components"
RDEPENDS:packagegroup-x-linux-ai-onnxruntime = "\
    onnxruntime \
    onnxruntime-tools \
    python3-onnxruntime \
    x-linux-ai-tool \
    onnx-models-mobilenetv1 \
    onnx-models-coco-ssd-mobilenetv1 \
    onnx-image-classification-python \
    onnx-object-detection-python \
    onnx-object-detection-cpp \
"

SUMMARY:packagegroup-x-linux-ai-npu = "X-LINUX-AI minimum NPU components"
RDEPENDS:packagegroup-x-linux-ai-npu += "\
    tim-vx \
    tim-vx-tools \
    nbg-benchmark \
    nbg-image-classification-cpp \
    nbg-models-mobilenetv3 \
"
