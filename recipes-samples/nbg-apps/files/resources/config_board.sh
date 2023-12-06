#!/bin/bash

COMPATIBLE=$(cat /proc/device-tree/compatible)
STM32MP135="stm32mp135"
STM32MP157="stm32mp157"
STM32MP257="stm32mp257"

if [[ "$COMPATIBLE" == *"$STM32MP135"* ]]; then
  MACHINE=$STM32MP135
  DWIDTH=320
  DHEIGHT=240
  DFPS=10
  COMPUTE_ENGINE=""
  echo "machine used = "$MACHINE
fi

if [[ "$COMPATIBLE" == *"$STM32MP157"* ]]; then
  MACHINE=$STM32MP157
  DWIDTH=640
  DHEIGHT=480
  DFPS=15
  COMPUTE_ENGINE=""
  echo "machine used = "$MACHINE
fi

if [[ "$COMPATIBLE" == *"$STM32MP257"* ]]; then
  MACHINE=$STM32MP257
  DWIDTH=640
  DHEIGHT=480
  DFPS=30
  COMPUTE_ENGINE="--npu"
  echo "machine used = "$MACHINE
fi
