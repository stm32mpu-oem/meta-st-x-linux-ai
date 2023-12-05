<p align="center">
    <img width="720" src="https://raw.githubusercontent.com/STMicroelectronics/meta-st-stm32mpu-ai/master/x-linux-ai-logo.png">
</p>

X-LINUX-AI version: STM32MP25-beta

X-LINUX-AI is a free of charge open-source software package dedicated to AI.
It is a complete ecosystem that allow developers working with OpenSTLinux to create AI-based application very easily.
* **All-in-one AI solutions** for the entire STM32MPU serie
* **Pre-integrated** into Linux distribution based on ST environment
* Include **AI frameworks** to execute Neural Network models
* Include **AI model benchmark** application tools for MPU
* **Easy** application **prototyping** using Python language and AI frameworks Python API
* **C++ API** for embedded high-performance applications
* Optimized **open-source solutions** provided with source codes that allow extensive **code reuse** and **time savings**

# meta-st-x-linux-ai
X-LINUX-AI OpenEmbedded meta layer to be integrated into OpenSTLinux distribution.
It contains recipes for AI frameworks, tools and application examples for STM32MPx series

## Compatibility
The X-LINUX-AI OpenSTLinux Expansion Package STM32MP25-beta is compatible with the Yocto Project™ build system Mickledore.
It is validated over the OpenSTLinux Distribution v5.0.2.BETA on STM32MP257F-ev1 with its csi image sensor

## Versioning
This X-LINUX-AI STM32MP25-beta is dedicated to provide a complete AI ecosystem for STM32MP25 board and to demonstrate these AI hardware capabilities

## Available frameworks and tools within the meta-layer
[X-LINUX-AI STM32MP25-beta expansion package](https://wiki.st.com/stm32mp25-beta-v5/wiki/Category:X-LINUX-AI_expansion_package):
* TensorFlow™ Lite 2.11.0 with XNNPACK delegate activated
* Coral Edge TPU™ accelerator native support
  * libedgetpu 2.0.0 (Grouper) aligned with TensorFlow™ Lite 2.11.0
  * libcoral 2.0.0 (Grouper) aligned with TensorFlow™ Lite 2.11.0
  * PyCoral 2.0.0 (Grouper) aligned with TensorFlow™ Lite 2.11.0
* ONNX Runtime™  1.14.0 with XNNPACK execution engine activated
* OpenCV 4.7.x
* Python™ 3.11.x
* Support of Sony™ IMX335 5Mpx sensor with use of DCMIPP and internal ISP
* Support for the OpenSTLinux AI package repository allowing the installation of a prebuilt package using apt-* utilities
* Application  :
  * Image Classification :
      * C++ / Python™ example using TensorFlow™ Lite based on the MobileNet v3 quantized model
      * C++ / Python™ example using Coral Edge TPU™ based on the MobileNet v1 quantized model and compiled for the Edge TPU™
      * Python™ example using ONNX Runtime based on the MobileNet v3 quantized model
      * C++  example using Network Binary Graph based on MobileNet v3 quantized model
  * Object Detection :
    * C++ example using TensorFlow™ Lite based on the COCO SSD MobileNet v1 quantized model
    * Python™ example using TensorFlow™ Lite based on the YoloV4-tiny quantized model
    * C++ / Python™ example using Coral Edge TPU™ based on the COCO SSD MobileNet v1 quantized model and compiled for the Edge TPU™
    * C++ / Python™ example using ONNX Runtime based on the COCO SSD MobileNet v1 quantized model
  * Face Recognition :
    * C++ example using proprietary model capable of recognizing the face of a known (enrolled) user. Contact the local STMicroelectronics support for more information about this application or send a request to edge.ai@st.com
  * Human Pose Estimation
    * Python™ example using TensorFlow™ Lite based on Movenet SinglePose Lightning quantized model
  * Semantic Segmentation
    * Python™ example using TensorFlow™ Lite based on DeepLabV3 quantized model
* Application support for the 1080p, 720p, 480p, and 272p display configurations
* X-LINUX-AI SDK add-on extending the OpenSTLinux SDK with AI functionality to develop and build an AI application easily. The X-LINUX-AI SDK add-on provides support for all the above frameworks. It is available from the [X-LINUX-AI] STM32MP25-beta repository

## Further information on how to install and how to use X-LINUX-AI Starter package
<https://wiki.st.com/stm32mp25-beta-v5/wiki/X-LINUX-AI_Starter_package>

## Further information on how to install and how to use X-LINUX-AI Developer package
<https://wiki.st.com/stm32mp25-beta-v5/wiki/X-LINUX-AI_Developer_package>

## Further information on how to install and how to use X-LINUX-AI Distribution package
<https://wiki.st.com/stm32mp25-beta-v5/wiki/X-LINUX-AI_Distribution_package>

## Application samples
<https://wiki.st.com/stm32mp25-beta-v5/wiki/Category:AI_-_Application_examples>
